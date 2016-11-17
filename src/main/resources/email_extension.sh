#! /bin/sh
#*******************************************************************************
# Copyright (C) 2016 Black Duck Software, Inc.
# http://www.blackducksoftware.com/
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#*******************************************************************************

PID_FILE=email_extension.pid
MAX_ATTEMPTS=20
PROGRAM="$0"
DIR=`dirname "$PROGRAM"`

checkIsRunning() {
  if [ -f $DIR/$PID_FILE ]; then
    local running_pid="$(pgrep -f hub-email-extension)"
    local saved_pid="$(<$DIR/$PID_FILE)"
    if [ ! -z $running_pid  ]; then 
        if [ "$saved_pid" -eq "$running_pid" ]; then
            return 1 
        fi
    fi 
    return 0
  fi 
  return 0
}

startExtension() {
  checkIsRunning
  returnValue=$?
  if [ "$returnValue" == 1 ]; then 
      echo "Hub Email Extension is already running"
      return 0;
  fi
  echo "Starting Hub Email Extension"
  $DIR/hub-email-extension & 
  sleep 1s
  local pid="$(pgrep -f hub-email-extension)"
  echo "$pid" > $DIR/$PID_FILE  
  echo "Started extension with PID: $pid"
}

stopExtension() {
  checkIsRunning
  returnValue=$?
  if [ "$returnValue" == 1 ]; then
     local pid=$(<$DIR/$PID_FILE)
     echo "Stopping extension with PID: $pid"
     for ((index=0; index<MAX_ATTEMPTS; index ++)); do 
       checkIsRunning
       stillRunning=$?
       if [ "$stillRunning" == 0 ]; then
         rm -f $PID_FILE
         echo "Stopped extension with PID: $pid"
         return 0;
       fi
       kill $pid
       sleep .5
     done
  fi
  return 1
}

extensionStatus () {
  checkIsRunning
  returnValue=$?
  if [ "$returnValue" == 1 ]; then
     echo "Hub Email Extension is running"
  else
     echo "Hub Email Extension is not running"
  fi
}

case "$1" in
    start) 
          startExtension
          ;;
    stop)
          stopExtension
          ;;
    restart)
          stopExtension
          startExtension
          ;;
    status)
          extensionStatus
          ;;
    *)    
          echo "Usage: start|stop|restart|status"
          exit 1
          ;;
esac
