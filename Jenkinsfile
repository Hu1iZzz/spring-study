// spring-study 自动部署流水线（外置 Tomcat + D 盘挂载版）
// 触发方式：Jenkins 每分钟轮询本地仓库（file:///repo），有新提交自动执行
//
// 与旧版（java -jar 镜像内嵌 war）的区别：
// - 应用容器是官方 tomcat:11-jre17，webapps 目录 bind-mount 到宿主机 D:/docker-data/spring-study/webapps
// - Jenkins 容器把同一宿主机目录挂载为 /webapps，部署 = 覆盖 ROOT.war + docker restart
// - 不再执行 docker build，不再删除/重建应用容器
pipeline {
    agent any

    environment {
        // Jenkins 容器内路径：/repo 是仓库挂载；/webapps 与应用容器的 webapps 是同一个宿主机目录
        WAR_FILE       = '/repo/target/study-0.0.1-SNAPSHOT.war'
        WEBAPPS        = '/webapps'
        APP_CONTAINER  = 'spring-study-app'
    }

    stages {
        stage('打包 war') {
            steps {
                sh './mvnw -B -ntp clean package -DskipTests'
            }
        }

        stage('替换 ROOT.war') {
            steps {
                // 连同 Tomcat 上次解压的 ROOT 目录一起清掉，确保重启后按新 war 重新展开
                sh 'rm -rf $WEBAPPS/ROOT $WEBAPPS/ROOT.war'
                sh 'cp $WAR_FILE $WEBAPPS/ROOT.war'
            }
        }

        stage('重启应用容器') {
            steps {
                // MariaDB / Redis / RabbitMQ 跑在宿主机容器上，应用容器通过 host.docker.internal 访问
                sh 'docker restart $APP_CONTAINER'
            }
        }

        stage('健康检查') {
            steps {
                sh '''
                    for i in $(seq 1 60); do
                      code=$(curl -s -o /dev/null -w "%{http_code}" http://host.docker.internal:8080/actuator/health || true)
                      if [ "$code" = "200" ] || [ "$code" = "302" ]; then echo "应用已启动 (HTTP $code)"; exit 0; fi
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
