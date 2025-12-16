# Build
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Gradle Wrapper 사용
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 복사 & 빌드
COPY src src
RUN ./gradlew clean bootJar --no-daemon

# Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 빌드된 jar 복사
COPY --from=build /app/build/libs/*.jar app.jar

# Spring Boot 기본 포트
EXPOSE 8080

# 컨테이너 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]
