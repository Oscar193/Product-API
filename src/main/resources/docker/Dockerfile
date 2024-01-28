ARG CORRETTO_VERSION="@amazoncorretto.version@"
FROM amazoncorretto:${CORRETTO_VERSION}

ARG JAVA_LANG_VERSION="@java.version@"
#RUN yum -y install \
#    java-${JAVA_LANG_VERSION}-amazon-corretto-devel \
#    nspr \
#    nss \
#    nss-softokn \
#    nss-softokn-freebl \
#    nss-sysinit \
#    nss-tools \
#    nss-util \
#    cyrus-sasl-lib \
#    expat \
#    openssl-libs \
#    xz-libs \
#    zlib \
#    vim-data \
#    vim-minimal \
#    && yum -y clean all \
#    && rm -rf /var/cache

# Enviroment variables marked with @variable@ are filtered during the mvn resources:resources.
# In normal use after `mvn package` check `/target/Dockerfile` for final Dockerfile populated with correct variables.

ENV ARTIFACT_ID="@project.artifactId@"
ENV ARTIFACT_VERSION="@project.version@"
ENV ARTIFACT_NAME="@project.name@"
ENV BUILT_ON="@maven.build.timestamp@"

ENV MAX_MEMORY_PERCENTAGE="75"
ENV JAVA_OPTS=""

LABEL name=$ARTIFACT_NAME \
      version=$ARTIFACT_VERSION \
      artifactID=$ARTIFACT_ID \
      builtTimestamp=$BUILT_ON

ENV FINALNAME=@project.build.finalName@

ADD ./${FINALNAME}.jar /opt/textplus-service.jar
ADD ./entrypoint.sh /opt/

RUN chmod +x /opt/entrypoint.sh && sed -i 's/\r$//g' /opt/entrypoint.sh

ENTRYPOINT ["sh","/opt/entrypoint.sh"]
