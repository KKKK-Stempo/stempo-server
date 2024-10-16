# 1. Gradle 8.10.2 기반 빌드 이미지 생성
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app

# build.gradle.kts와 settings.gradle, buildSrc 디렉토리를 복사하여 의존성 설치 준비
COPY build.gradle.kts settings.gradle /app/
COPY buildSrc /app/buildSrc

# 의존성 캐싱을 위해 먼저 의존성만 설치 (빌드 전에 캐시 활용)
RUN gradle dependencies --no-daemon --parallel --stacktrace

# 소스 파일을 모두 복사한 후 애플리케이션 빌드
COPY . /app
RUN gradle build --no-daemon --parallel --stacktrace

# 2. 런타임용 이미지 생성
FROM openjdk:21-jdk-slim
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일을 런타임 이미지로 복사
COPY --from=build /app/stempo-api/build/libs/stempo-api.jar stempo-api.jar

# 애플리케이션이 실행될 포트 설정 (8080)
EXPOSE 8080

# 스프링 프로파일을 prod로 설정하고 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "stempo-api.jar"]
