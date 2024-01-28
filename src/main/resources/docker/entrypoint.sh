#!/bin/sh

if [ -n "${APPLICATIONINSIGHTS_CONNECTION_STRING}" ]; then
  export JAVA_OPTS="${APPLICATIONINSIGHTS_OPTS} ${JAVA_OPTS}"
fi

if [ -n "${ROOKOUT_OPTS}" ]; then
  export JAVA_OPTS=" ${ROOKOUT_OPTS} ${JAVA_OPTS}"
fi

export JAVA_OPTS="${JAVA_OPTS} -Djava.net.preferIPv4Stack=true"

echo "entrypoint: Using JAVA_OPTS: ${JAVA_OPTS}"

exec java ${JAVA_OPTS} -jar /opt/textplus-service.jar
