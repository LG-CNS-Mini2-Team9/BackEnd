FROM amazoncorretto:17
RUN yum update -y && yum install -y curl && yum clean all
MAINTAINER dev@team9.com
VOLUME /tmp
EXPOSE 8080
COPY build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]