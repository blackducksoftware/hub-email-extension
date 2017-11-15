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

importCertificate() {
  echo "Attempting to import Hub Root Certificate"

  # In case of email-extension container restart
  # Remove hub root
  if keytool -list -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit -alias hubrootcertificate >/dev/null
  then
    keytool -delete -alias hubrootcertificate -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit
   	echo "Removing the hub web root existing certificate after container restart"
  fi

  # Remove custom cert
  if keytool -list -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit -alias customhubservercertificate >/dev/null
  then
    keytool -delete -alias customhubservercertificate -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit
   	echo "Removing the existing custom hub web server certificate after container restart"
  fi

  targetCAHost="${HUB_CFSSL_HOST:-cfssl}"
  targetCAPort="${HUB_CFSSL_PORT:-8888}"
  url="http://$targetCAHost:$targetCAPort/api/v1/cfssl/info"
  export body="{ \"label\": \"root\", \"profile\": \"peer\" }"

  echo "Attempting to get root certificate."
  echo "URL: $url"
  echo "Body: $body"

  curl -X POST -H "Content-Type: application/json" -d "$body" -o ./root.json --connect-timeout 15 --max-time 30 --retry 20 --retry-delay 10 --retry-max-time 180 --retry-connrefused $url

  cat root.json |   jq '.result.certificate' | tr -d '"' | sed 's/\\n/\
/g' > root.crt

  if cat root.crt  | keytool -importcert -keystore "$JAVA_HOME/lib/security/jssecacerts" -storepass changeit -alias hubrootcertificate -noprompt > /dev/null
  then
	echo "Completed importing Hub Root Certificate"
  else
	echo "Unable to add the hub root certificate. Please try to import the certificate manually."
  fi

  if [ -f /run/secrets/WEBSERVER_CUSTOM_CERT_FILE ]; then
    if cat /run/secrets/WEBSERVER_CUSTOM_CERT_FILE  | keytool -importcert -keystore "$JAVA_HOME/lib/security/jssecacerts" -storepass changeit -alias hcustomhubservercertificate -noprompt > /dev/null
    then
	  echo "Completed importing Custom Hub Web Server Certificate"
    else
	  echo "Unable to add the custom hub web server certificate. Please try to import the certificate manually."
    fi
  else
    echo "Custom Hub Web Server Certificate not specified, skipping."
  fi

  #Workaround as the hub refresh token is not usable
  if [ -f $EMAIL_EXT_HOME/config/oauth.properties ]
  then
  	rm $EMAIL_EXT_HOME/config/oauth.properties
  fi
}


verifyEnvironment
importCertificate
exec "$@"
