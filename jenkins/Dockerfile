# 1. 빌드 단계
FROM bellsoft/liberica-openjdk-alpine-musl:21.0.5-cds AS build
WORKDIR /app

# Gradle 설치
ENV GRADLE_VERSION=8.11.1
RUN apk add --no-cache curl unzip bash && \
    curl -fsSL https://downloads.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle.zip && \
    unzip gradle.zip && \
    rm gradle.zip && \
    mv gradle-${GRADLE_VERSION} /opt/gradle && \
    ln -s /opt/gradle/bin/gradle /usr/local/bin/gradle

# Gradle 설정 파일 및 buildSrc 복사 후 의존성 설치
COPY build.gradle.kts settings.gradle /app/
COPY buildSrc /app/buildSrc
RUN gradle dependencies --parallel --stacktrace

# 소스 코드 복사 및 애플리케이션 빌드
COPY . /app
RUN gradle bootJar --no-daemon --build-cache --stacktrace

# CDS 아카이브 생성
RUN java -XX:ArchiveClassesAtExit=stempo-cds.jsa \
         -Dspring.profiles.active=cds \
         -Dspring.context.exit=onRefresh \
         -jar stempo-api/build/libs/stempo-api.jar

# 2. 런타임 단계
FROM bellsoft/liberica-openjdk-alpine-musl:21.0.5-cds
WORKDIR /app

# JAR 파일 복사
COPY --from=build /app/stempo-api/build/libs/stempo-api.jar stempo-api.jar

# CDS 아카이브 파일 복사
COPY --from=build /app/stempo-cds.jsa ./

# 포트 설정 및 애플리케이션 실행
EXPOSE 8080
ENTRYPOINT ["java",
            "-XX:SharedArchiveFile=stempo-cds.jsa",
            "-Dspring.profiles.active=prod",
            "-jar", "stempo-api.jar"]
