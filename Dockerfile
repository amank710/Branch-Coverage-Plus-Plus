FROM ubuntu:22.04

RUN apt-get update
RUN apt-get install -y wget unzip

RUN wget -O /tmp/jdk-22_linux_x64_bin.deb https://download.oracle.com/java/22/latest/jdk-22_linux-x64_bin.deb
RUN dpkg -i /tmp/jdk-22_linux_x64_bin.deb

RUN wget -O /tmp/apache-maven-3.9.6-bin.zip https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip
RUN unzip /tmp/apache-maven-3.9.6-bin.zip -d /bin/

COPY . /app
WORKDIR /app

EXPOSE 8080
ENTRYPOINT ["/bin/apache-maven-3.9.6/bin/mvn", "spring-boot:run"]
