FROM amazoncorretto:17
MAINTAINER dev@team9.com
VOLUME /tmp
EXPOSE 8761
COPY build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]

#FROM amazoncorretto:17
#
## 메타데이터
#LABEL maintainer="dev@team9.com" \
#      description="Eureka Service Discovery Server" \
#      version="1.0.0"
#
## 작업 디렉토리 설정
#WORKDIR /app
#
## 임시 볼륨 설정 (Spring Boot 기본)
#VOLUME /tmp
#
## 타임존 설정 (한국 시간)
#ENV TZ=Asia/Seoul
#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#
## JAR 파일 인수 설정
#ARG JAR_FILE=build/libs/*.jar
#
## JAR 파일을 app.jar로 복사
#COPY ${JAR_FILE} app.jar
#
## 포트 (유레카 서버 기본 포트)
#EXPOSE 8761
#
## JVM 최적화 및 애플리케이션 실행
#ENTRYPOINT ["java", \
#    "-Xms256m", \
#    "-Xmx512m", \
#    "-XX:+UseContainerSupport", \
#    "-XX:MaxRAMPercentage=75.0", \
#    "-Djava.security.egd=file:/dev/./urandom", \
#    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:default}", \
#    "-jar", \
#    "/app.jar"]

## 루트가 아닌 사용자로 실행 (보안 강화) - 검토해보기
#RUN groupadd --gid 1000 spring && \
#    useradd --uid 1000 --gid spring --shell /bin/bash --create-home spring

## 헬스체크용 curl 설치
#RUN yum update -y && yum install -y curl && yum clean all
#
## 헬스체크 설정
#HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
#    CMD curl -f http://localhost:8761/actuator/health || exit 1