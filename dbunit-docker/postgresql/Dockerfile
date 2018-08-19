FROM postgres:9.6

# Install JDK 1.8/packages
RUN echo 'deb http://deb.debian.org/debian jessie-backports main' \
      > /etc/apt/sources.list.d/jessie-backports.list
RUN apt-get update -y
RUN apt-get install -t jessie-backports -y openjdk-8-jdk ca-certificates-java
RUN apt-get install -y curl

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

ENV POSTGRES_DB=dbunit
ENV POSTGRES_USER=dbunit
ENV POSTGRES_PASSWORD=dbunit

COPY startdb.sh /startdb.sh
COPY stopdb.sh /stopdb.sh