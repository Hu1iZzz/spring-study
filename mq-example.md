# RabbitMQ 消息队列示例说明

## 示例内容

模拟「登录成功后异步发送通知」的场景，消息走完 **生产者 → 交换机 → 队列 → 消费者** 全流程：

```
MqDemoController (POST /api/mq/login-notify)
        │
        ▼
LoginMessageProducer ──(login.exchange, routing key: login.notify)──▶ login.queue
                                                                          │
                                                                          ▼
                                                              LoginMessageConsumer (@RabbitListener)
```

涉及的文件：

| 文件 | 作用 |
| --- | --- |
| `mq/RabbitMQConfig.java` | 声明 Direct 交换机、持久化队列、绑定，配置 JSON 消息转换器 |
| `mq/LoginMessage.java` | 消息体（用户名、IP、内容、时间） |
| `mq/LoginMessageProducer.java` | 生产者，`RabbitTemplate.convertAndSend` 发送消息 |
| `mq/LoginMessageConsumer.java` | 消费者，`@RabbitListener` 监听队列并处理 |
| `controller/MqDemoController.java` | 触发发送的 REST 接口 |

## 1. 启动 RabbitMQ（Docker）

```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

- 应用连接端口：`5672`
- 管理控制台：http://localhost:15672 （账号/密码均为 `guest`）

连接配置在 `application.yaml` 的 `spring.rabbitmq` 下，默认 `localhost:5672`。

## 2. 启动应用并测试

```bash
mvn spring-boot:run
```

发送一条测试消息（接口已被 Spring Security 保护时请先登录或临时放行 `/api/mq/**`）：

```bash
curl -X POST "http://localhost:8080/api/mq/login-notify?username=zhangsan"
```

应用日志中会看到：

```
[MQ 生产者] 发送登录通知消息: LoginMessage(username=zhangsan, ...)
[MQ 消费者] 收到登录通知 -> 用户: zhangsan, IP: 127.0.0.1, 内容: 您刚刚登录了系统, ...
```

也可以在管理控制台 http://localhost:15672 的 **Queues** 页面看到 `login.queue` 的收发计数。

## 常用说明

- 交换机为 **Direct** 类型，按 routing key（`login.notify`）精确投递；想按模式匹配可改用 `TopicExchange`。
- 消息体通过 `Jackson2JsonMessageConverter` 以 JSON 序列化，便于跨语言消费。
- 生产环境建议开启 publisher confirm（`spring.rabbitmq.publisher-confirm-type: correlated`）与手动 ACK（`acknowledge-mode: manual`）。
