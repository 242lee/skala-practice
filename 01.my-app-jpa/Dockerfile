# 베이스 이미지로 OpenJDK 17 버전의 JRE 이미지 사용
FROM openjdk:17-jdk-slim
# 작업 디렉토리 설정
WORKDIR /app
# 외부에서 컨테이너의 8080, 8081 포트 접근 허용
EXPOSE 8080
EXPOSE 8081
# 로컬에서 빌드된 jar 파일을 app.jar로 복사
COPY target/*.jar app.jar
# 애플리케이션 실행
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
