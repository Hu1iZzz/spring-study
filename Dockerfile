# 运行时镜像：Jenkins 构建出 war 后打包成镜像
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY target/study-0.0.1-SNAPSHOT.war app.war

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]
