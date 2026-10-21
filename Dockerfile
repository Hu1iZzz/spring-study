# 备选：镜像化部署用的 Dockerfile（当前流水线已改为外置 Tomcat + webapps 目录挂载，不再执行 docker build）
#
# 当前部署方式（Jenkinsfile）：
#   应用容器直接使用官方 tomcat:11-jre17 镜像创建，
#   宿主机 D:/docker-data/spring-study/webapps 挂载为容器内 /usr/local/tomcat/webapps，
#   部署 = 覆盖 webapps/ROOT.war + docker restart spring-study-app。
#
# 若要回到镜像化部署，可执行：
#   docker build -t spring-study:latest .
#   docker run -d --name spring-study-app -p 8080:8080 --restart unless-stopped \
#     -e TZ=Asia/Shanghai \
#     -e "SPRING_DATASOURCE_URL=jdbc:mariadb://host.docker.internal:3306/springstudy?useSSL=false&useUnicode=true&characterEncoding=UTF-8" \
#     -e "SPRING_DATA_REDIS_HOST=host.docker.internal" \
#     -e "SPRING_RABBITMQ_HOST=host.docker.internal" \
#     spring-study:latest
FROM tomcat:11-jre17

# Tomcat 11（Servlet 6.1）满足 Spring Boot 4 的 Jakarta EE 11 基线
# webapps 不放入任何文件，由外部挂载提供 ROOT.war
