// spring-study 自动部署流水线
// 触发方式：Jenkins 每分钟轮询本地仓库（file:///repo），有新提交自动执行
pipeline {
    agent any

    environment {
        APP_IMAGE     = 'spring-study'
        APP_CONTAINER = 'spring-study-app'
    }

    stages {
        stage('打包 war') {
            steps {
                sh './mvnw -B -ntp clean package -DskipTests'
            }
        }

        stage('构建镜像') {
            steps {
                sh 'docker build -t $APP_IMAGE:latest .'
            }
        }

        stage('重启应用容器') {
            steps {
                // MariaDB / Redis / RabbitMQ 跑在宿主机容器上，从应用容器内用 host.docker.internal 访问
                sh '''
                    docker rm -f $APP_CONTAINER 2>/dev/null || true
                    docker run -d --name $APP_CONTAINER --restart unless-stopped \
                      -p 8080:8080 \
                      -e TZ=Asia/Shanghai \
                      -e "SPRING_DATASOURCE_URL=jdbc:mariadb://host.docker.internal:3306/springstudy?useSSL=false&useUnicode=true&characterEncoding=UTF-8" \
                      -e "SPRING_DATA_REDIS_HOST=host.docker.internal" \
                      -e "SPRING_RABBITMQ_HOST=host.docker.internal" \
                      $APP_IMAGE:latest
                '''
            }
        }

        stage('健康检查') {
            steps {
                sh '''
                    for i in $(seq 1 60); do
                      code=$(curl -s -o /dev/null -w "%{http_code}" http://host.docker.internal:8080/actuator/health || true)
                      if [ "$code" != "000" ]; then echo "应用已启动 (HTTP $code)"; exit 0; fi
                      sleep 2
                    done
                    echo "健康检查超时"; exit 1
                '''
            }
        }
    }

    post {
        failure {
            echo '部署失败，查看应用日志：docker logs spring-study-app'
        }
    }
}
