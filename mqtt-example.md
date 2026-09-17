# MQTT 消息示例说明

## 示例内容

通过 **EMQX 公共测试 broker**（无需本地安装）演示 MQTT 的发布/订阅闭环，接口发出的消息会被自己的订阅者收到：

```
MqttDemoController (POST /api/mqtt/publish)
        │
        ▼
MqttMessagePublisher ──(mqttOutboundChannel)──▶ MqttPahoMessageHandler ──▶ EMQX 公共 broker
                                                                              │ (topic: demo/topic)
MqttMessageSubscriber (@ServiceActivator) ◀── mqttInboundChannel ◀── MqttPahoMessageDrivenChannelAdapter
```

涉及的文件：

| 文件 | 作用 |
| --- | --- |
| `mqtt/MqttConfig.java` | Paho 连接工厂、入站/出站适配器、两个消息通道 |
| `mqtt/MqttMessagePublisher.java` | 发布者，向通道发消息（可指定 topic、retain） |
| `mqtt/MqttMessageSubscriber.java` | 订阅者，`@ServiceActivator` 监听入站通道并打日志 |
| `controller/MqttDemoController.java` | 触发发布的 REST 接口 |

## MQTT 核心概念

- **broker**：消息服务器（EMQX、Mosquitto 等），所有消息经它路由，对应 RabbitMQ 的服务器本体。
- **client-id**：客户端唯一标识，同一 broker 上必须唯一，重复连接会互踢下线（所以示例中入站/出站分别用了 `-inbound` / `-outbound` 后缀）。
- **topic**：主题，按 `层级/子层级` 组织（如 `demo/topic`、`home/livingroom/temperature`），对应 RabbitMQ 的交换机 + routing key。
  - 订阅时可用通配符：`+` 匹配单层（`demo/+`），`#` 匹配多层（`demo/#`）。
- **QoS**：服务质量。
  - `0` 最多一次：可能丢消息，最快；
  - `1` 至少一次：可能重复，最常用；
  - `2` 恰好一次：四步握手，最慢。
- **retain**：保留消息。broker 保存该主题最后一条 retain 消息，新订阅者一上线立即收到（常用于"当前状态"类数据，如温度）。
- **cleanSession / 自动重连**：示例中 `cleanSession=true` 且开启 `automaticReconnect`，断线后由适配器自动重连并重新订阅。

## 1. 启动应用并测试

默认配置连的是 EMQX 公共测试 broker（`tcp://broker.emqx.io:1883`），无需安装任何东西：

```bash
mvn spring-boot:run
```

发布一条消息（接口被 Spring Security 保护时请先登录或临时放行 `/api/mqtt/**`）：

```bash
curl -X POST "http://localhost:8080/api/mqtt/publish?message=hello-mqtt"
```

应用日志中会看到闭环：

```
[MQTT 发布者] 已发布 -> topic: demo/topic, payload: hello-mqtt, retain: false
[MQTT 订阅者] 收到消息 -> topic: demo/topic, payload: hello-mqtt
```

指定主题和保留消息试试：

```bash
curl -X POST "http://localhost:8080/api/mqtt/publish?message=在线&retain=true&topic=demo/status"
```

也可以用 MQTTX 等客户端工具连 `broker.emqx.io:1883` 订阅 `demo/topic`，观察另一个"设备"收到的消息。

## 2. 切换到本地 broker（可选）

```bash
docker run -d --name emqx -p 1883:1883 -p 18083:18083 emqx/emqx
```

把 `application.yaml` 里 `mqtt.broker-url` 改成 `tcp://localhost:1883` 即可。EMQX 管理控制台：http://localhost:18083 （默认账号 `admin` / `public`），可在 Dashboard 里看到客户端连接和消息流。

## 与 RabbitMQ 的对比

| 维度 | MQTT | RabbitMQ（AMQP） |
| --- | --- | --- |
| 路由模型 | topic 树 + 通配符订阅 | 交换机 + routing key |
| 典型场景 | 物联网设备、低带宽、长连接 | 业务系统间的任务/事件分发 |
| 队列 | 无原生队列概念（broker 按会话缓冲） | 核心概念，支持持久化、死信等 |
| 认证/权限 | 简单（username/password） | 完善的 vhost、用户权限体系 |
