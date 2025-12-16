# Build
FROM gradle:8.5-jdk17-alpine AS build

WORKDIR /app

# 의존성 캐시 최적화
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
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
