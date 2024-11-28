# 1. 빌드 단계
FROM bellsoft/liberica-runtime-container:jdk-21-cds-slim-musl AS build
WORKDIR /app

# Gradle Wrapper 복사 및 실행 권한 부여
COPY gradlew /app/
COPY gradle /app/gradle
RUN chmod +x ./gradlew

# Gradle 설정 파일 및 buildSrc 복사 후 의존성 설치
COPY build.gradle.kts settings.gradle gradle.properties /app/
COPY buildSrc /app/buildSrc
RUN ./gradlew dependencies --parallel --stacktrace

# 소스 코드 복사
COPY . /app

# 애플리케이션 빌드
RUN ./gradlew bootJar -Pspring.aot.enabled=true --no-daemon --build-cache --stacktrace


# 2. Layered JAR 추출 단계
FROM bellsoft/liberica-runtime-container:jdk-21-cds-slim-musl AS extract
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/stempo-api/build/libs/stempo-api.jar /app/stempo-api.jar

# Layered JAR 추출
RUN java -Djarmode=tools -jar /app/stempo-api.jar extract --layers --launcher


# 3. CDS 아카이브 생성 단계
FROM bellsoft/liberica-runtime-container:jre-21-cds-slim-musl AS cds
WORKDIR /app

# Layered JAR 복사
COPY --from=extract /app/dependencies/ ./
COPY --from=extract /app/spring-boot-loader/ ./
COPY --from=extract /app/snapshot-dependencies/ ./
COPY --from=extract /app/application/ ./

# CDS 아카이브 생성
RUN java -XX:ArchiveClassesAtExit=./stempo-cds.jsa \
         -Dspring.aot.enabled=true \
         -Dspring.profiles.active=cds \
         -Dspring.context.exit=onRefresh \
         org.springframework.boot.loader.JarLauncher


# 4. 런타임 단계 \
FROM bellsoft/liberica-runtime-container:jre-21-cds-slim-musl
WORKDIR /app

# 빌드 단계에서 생성된 레이어 복사
COPY --from=cds /app/dependencies/ ./
COPY --from=cds /app/spring-boot-loader/ ./
COPY --from=cds /app/snapshot-dependencies/ ./
COPY --from=cds /app/application/ ./

# CDS 아카이브 파일 복사
COPY --from=cds /app/stempo-cds.jsa ./

# 포트 설정 및 애플리케이션 실행
EXPOSE 8080
ENTRYPOINT ["java", "-XX:SharedArchiveFile=stempo-cds.jsa", "-Dspring.aot.enabled=true", "-Dspring.profiles.active=prod", "org.springframework.boot.loader.JarLauncher"]
