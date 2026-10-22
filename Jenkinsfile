// spring-study 自动部署流水线（外置 Tomcat + D 盘挂载版）
// 触发方式：Jenkins 每分钟轮询 GitHub 远程仓库（master 分支），push 新提交自动执行
//
// 部署方式：应用容器是官方 tomcat:11-jre17，webapps 目录 bind-mount 到宿主机
// D:/docker-data/spring-study/webapps，Jenkins 容器把同一目录挂载为 /webapps。
// 部署 = 停容器 → 覆盖 ROOT.war → 起容器，不再 docker build / 删除重建容器。
pipeline {
    agent any

    environment {
        // Jenkins 构建的 workspace 就是 job 的检出目录，war 在其 target/ 下（相对路径）
        WAR_FILE       = 'target/study-0.0.1-SNAPSHOT.war'
        // 与应用容器 /usr/local/tomcat/webapps 是同一个宿主机目录（D:/docker-data/spring-study/webapps）
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
                // 必须先停 Tomcat 再换文件：Tomcat 后台热部署线程会在 cp 后立刻解压 war，
                // 若此时 restart 打断它，会留下半成品 ROOT/ 目录，且目录时间戳比 war 新，
                // 下次启动时 Tomcat 会跳过解压直接用残骸（表现为 404）
                sh 'docker stop $APP_CONTAINER'
                sh 'rm -rf $WEBAPPS/ROOT $WEBAPPS/ROOT.war && cp $WAR_FILE $WEBAPPS/ROOT.war'
            }
        }

        stage('启动应用容器') {
            steps {
                // MariaDB / Redis / RabbitMQ 跑在宿主机容器上，应用容器通过 host.docker.internal 访问
                sh 'docker start $APP_CONTAINER'
            }
        }

        stage('健康检查') {
            steps {
                // Spring Boot 外置部署启动约 2~3 分钟，最多等 5 分钟
                sh '''
                    for i in $(seq 1 100); do
                      code=$(curl -s -o /dev/null -w "%{http_code}" http://host.docker.internal:8080/actuator/health || true)
                      if [ "$code" = "200" ] || [ "$code" = "302" ]; then echo "应用已启动 (HTTP $code)"; exit 0; fi
                      sleep 3
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
