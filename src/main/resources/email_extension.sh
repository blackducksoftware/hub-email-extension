#! /bin/sh

PID_FILE=email_extension.pid
MAX_ATTEMPTS=20
PROGRAM="$0"
DIR=`dirname "$PROGRAM"`

checkIsRunning() {
  if [ -f $DIR/$PID_FILE ]; then
    local running_pid="$(ps aux | grep bin/java | grep hub-email-extension | grep -v grep | awk '{print $2}')"
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
  local pid="$(ps aux | grep bin/java | grep hub-email-extension | grep -v grep | awk '{print $2}')"
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
