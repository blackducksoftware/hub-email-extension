#!/bin/sh
set -e

verifyEnvironment() {
  # Verify JRE is present.
  if [ -n "$JAVA_HOME" ]; then
    if [ -d "$JAVA_HOME" ]; then
      if [ ! -f "$JAVA_HOME/lib/security/cacerts" ]; then 
        echo "ERROR: $JAVA_HOME/lib/security/cacerts is not a file or does not exist."
      fi
    else
      echo "ERROR: $JAVA_HOME is not a directory or does not exist."
      exit 1
    fi
  else
    echo "ERROR: JAVA_HOME is not defined."
    exit 1
  fi
}

importCertificate(){
echo "Attempting to import Hub Certificate"
echo $PUBLIC_HUB_WEBSERVER_HOST
echo $PUBLIC_HUB_WEBSERVER_PORT

# In case of email-extension container restart
if keytool -list -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit -alias publichubwebserver >/dev/null
then
    keytool -delete -alias publichubwebserver -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit
   	echo "Removing the existing certificate after container restart"
fi

#Workaround as the hub refresh token is not usuable
rm $EMAIL_EXT_HOME/config/oauth.properties
keytool -printcert -rfc -sslserver "$PUBLIC_HUB_WEBSERVER_HOST:$PUBLIC_HUB_WEBSERVER_PORT" -v | keytool -importcert -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit -alias publichubwebserver -noprompt

echo "Completed importing Hub Certificate"
}


verifyEnvironment
importCertificate
exec "$@"