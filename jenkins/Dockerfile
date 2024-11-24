# 1. 빌드 단계
FROM gradle:8.11.1-jdk21 AS build
WORKDIR /app

# Gradle 설정 파일 및 buildSrc 복사 후 의존성 설치
COPY build.gradle.kts settings.gradle /app/
COPY buildSrc /app/buildSrc
RUN gradle dependencies --parallel --stacktrace

# 소스 코드 복사 및 애플리케이션 빌드
COPY . /app
RUN gradle bootJar --no-daemon --build-cache --stacktrace

# Layered JAR에서 레이어 추출
RUN java -Djarmode=layertools -jar /app/stempo-api/build/libs/stempo-api.jar extract

# 2. 런타임 단계
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# 추출된 레이어 복사
COPY --from=build /app/dependencies/ ./
COPY --from=build /app/spring-boot-loader/ ./
COPY --from=build /app/snapshot-dependencies/ ./
COPY --from=build /app/application/ ./

# 포트 설정 및 애플리케이션 실행
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "org.springframework.boot.loader.launch.JarLauncher"]
