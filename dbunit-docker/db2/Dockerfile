FROM ibmcom/db2express-c:10.5.0.5-3.10.0

# Install JDK 1.8/packages
RUN yum makecache fast
RUN yum -y install yum-plugin-ovl
RUN yum -y update
RUN yum -y install java-1.8.1-openjdk java-1.8.0-openjdk-devel

# Install Maven
ARG MAVEN_VERSION=3.5.2
ARG MAVEN_USER_HOME_DIR="/root"
ARG MAVEN_BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${MAVEN_BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
&& ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$MAVEN_USER_HOME_DIR/.m2"

ENV LICENSE accept
ENV DB2INST1_PASSWORD "DB2INST1-PWD"

COPY startdb.sh /startdb.sh
COPY runit.sh /runit.sh
COPY stopdb.sh /stopdb.sh