# ========== 빌드 단계 ==========
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Maven 캐시 최적화
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 실제 소스 복사 후 빌드
COPY src ./src
RUN mvn clean package -DskipTests

# ========== 실행 단계 ==========
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 빌드 결과물 복사
COPY --from=builder /app/target/*-SNAPSHOT.jar app.jar

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 기본값: docker 프로파일
# 🚨 필요할 때 CI/CD나 docker-compose, k8s에서 SPRING_PROFILES_ACTIVE=prod 로 오버라이드 가능
ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8082

ENTRYPOINT ["java", "-Xmx400M", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
