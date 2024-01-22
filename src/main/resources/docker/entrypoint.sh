#!/bin/sh
#
## Set JVM max memory to MAX_MEMORY_PERCENTAGE of the available memory, if not already set in JAVA_OPTS
#set_jvm_max_memory() {
#  if test -z "${MAX_MEMORY_PERCENTAGE}"; then
#    return
#  fi
#
#  maxMemoryFlag='-XX:MaxRAMPercentage'
#  if test "${JAVA_OPTS#*$maxMemoryFlag}" != "${JAVA_OPTS}"; then
#    # JVM max memory already set
#    return
#  fi
#
#  export JAVA_OPTS="${maxMemoryFlag}=${MAX_MEMORY_PERCENTAGE} ${JAVA_OPTS}"
#
#  echo "entrypoint: Setting max memory: ${JAVA_OPTS}"
#}
#
#set_jvm_max_memory

if [ -n "${APPLICATIONINSIGHTS_CONNECTION_STRING}" ]; then
  export JAVA_OPTS="${APPLICATIONINSIGHTS_OPTS} ${JAVA_OPTS}"
fi

if [ -n "${ROOKOUT_OPTS}" ]; then
  export JAVA_OPTS=" ${ROOKOUT_OPTS} ${JAVA_OPTS}"
fi

export JAVA_OPTS="${JAVA_OPTS} -Djava.net.preferIPv4Stack=true"

echo "entrypoint: Using JAVA_OPTS: ${JAVA_OPTS}"

exec java ${JAVA_OPTS} -jar /opt/textplus-service.jar
