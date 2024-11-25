# 1. 빌드 단계
FROM gradle:8.11.1-jdk21 AS build
WORKDIR /app

# Gradle 설정 파일 및 buildSrc 복사 후 의존성 설치
COPY build.gradle.kts settings.gradle ./
COPY buildSrc ./buildSrc
RUN gradle dependencies --parallel --stacktrace

# 소스 코드 복사 및 애플리케이션 빌드
COPY . .
RUN gradle bootJar --no-daemon --build-cache --stacktrace

# Layered JAR에서 레이어 추출
RUN java -Djarmode=tools -jar stempo-api/build/libs/stempo-api.jar extract

# CDS 아카이브 생성
RUN java -XX:ArchiveClassesAtExit=stempo-cds.jsa \
    -Dspring.context.exit=onRefresh \
    -jar stempo-api/build/libs/stempo-api.jar

# 2. 런타임 단계
FROM bellsoft/liberica-runtime-container:jre-21-crac-cds-slim-musl AS runtime
WORKDIR /app

# 추출된 레이어 복사
COPY --from=build dependencies/ ./
COPY --from=build spring-boot-loader/ ./
COPY --from=build snapshot-dependencies/ ./
COPY --from=build application/ ./

# CDS 아카이브 파일 복사
COPY --from=build stempo-cds.jsa ./

# 포트 설정 및 애플리케이션 실행
EXPOSE 8080
ENTRYPOINT ["java", "-XX:SharedArchiveFile=stempo-cds.jsa", "-Dspring.profiles.active=prod", "org.springframework.boot.loader.launch.JarLauncher"]
