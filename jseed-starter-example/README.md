# JSeed example application

本模块演示 16 个 starter 的实际调用。默认启动只需要 Java 17 和 Maven。下面的表格可以按接口找到对应 Java 源码，服务模块通过 profile 单独开启。

Build and start with Java 17 from the repository root.

```sh
mvn -pl jseed-starter-example -am package
java -jar jseed-starter-example/target/jseed-starter-example-1.0.1-SNAPSHOT.jar
```

The example binds to `127.0.0.1:8080`. Use profiles by appending, for example, `--spring.profiles.active=netty,websocket`. Each external service profile has a matching `application-<profile>.yml` under [resources](src/main/resources).

| Starter | Example source | Call |
| --- | --- | --- |
| core | [DemoController](src/main/java/com/jseed/example/DemoController.java) | `GET /demo/hello` returns the shared `R` response. |
| web | [DemoController](src/main/java/com/jseed/example/DemoController.java) | `GET /demo/hello` emits an operation log event. `GET /demo/web` demonstrates JSON masking and large IDs. |
| crypto | [DemoController](src/main/java/com/jseed/example/DemoController.java) | `GET /demo/crypto?text=hello` encrypts, decrypts and hashes text. |
| mybatis | [DemoMapper](src/main/java/com/jseed/example/DemoMapper.java) | `GET /demo/partition` queries `demo_partition_1` via the configured suffix. |
| datascope | [DemoMapper](src/main/java/com/jseed/example/DemoMapper.java) | `GET /demo/items` only returns rows for demo department 10. |
| swagger | [application.yml](src/main/resources/application.yml) | Open `/swagger-ui.html` or fetch `/v3/api-docs`. |
| redis | [RedisDemo](src/main/java/com/jseed/example/RedisDemo.java) | Profile `redis`, `POST /demo/redis` caches a string under a lock. |
| satoken | [SaTokenDemo](src/main/java/com/jseed/example/SaTokenDemo.java) | Profile `satoken`, call login, then send the returned token to `/demo/auth/me`. |
| oss | [OssDemo](src/main/java/com/jseed/example/OssDemo.java) | Profile `oss`, upload multipart data and download by returned object name. |
| kafka | [KafkaDemo](src/main/java/com/jseed/example/KafkaDemo.java) | Profile `kafka`, `POST /demo/kafka` sends to `jseed-demo`. |
| rabbitmq | [RabbitDemo](src/main/java/com/jseed/example/RabbitDemo.java) | Profile `rabbitmq`, `POST /demo/rabbitmq` sends to `jseed.demo`. |
| mqtt | [MqttDemo](src/main/java/com/jseed/example/MqttDemo.java) | Profile `mqtt`, `POST /demo/mqtt` publishes to the subscribed demo topic. |
| job | [JobDemo](src/main/java/com/jseed/example/JobDemo.java) | Profile `job`, run the `jseedDemo` handler from XXL-Job Admin. |
| tdengine | [TdengineDemo](src/main/java/com/jseed/example/TdengineDemo.java) | Profile `tdengine`, `POST /demo/tdengine` writes and queries two points. |
| netty | [NettyDemo](src/main/java/com/jseed/example/NettyDemo.java) | Profile `netty`, `POST /demo/netty` uses a TCP echo connection. |
| websocket | [WebSocketDemo](src/main/java/com/jseed/example/WebSocketDemo.java) | Profile `websocket`, connect to `ws://127.0.0.1:8080/ws/jseed` and send text. |

## Local protocols

```sh
curl -fsS -H 'Content-Type: text/plain' --data-binary 'echo me' http://127.0.0.1:8080/demo/netty
curl -fsS -H 'Content-Type: text/plain' --data-binary 'hello sessions' http://127.0.0.1:8080/demo/websocket/broadcast
```

For WebSocket, use a WebSocket client and send a text frame to receive an echo. Broadcast returns the number of open sessions that accepted the message.

## Service profiles

| Profile | Service and configuration |
| --- | --- |
| `redis` | Redis on `localhost:6379`. Override with `REDIS_HOST`, `REDIS_PORT` and `REDIS_PASSWORD`. |
| `satoken` | Set `DEMO_JWT_SECRET` to a local secret. Add the `redis` profile to persist sessions in Redis. Demo login deliberately uses a fixed identity and is suitable only for local use. |
| `oss` | Set `MINIO_ACCESS_KEY` and `MINIO_SECRET_KEY`, and optionally `MINIO_URL`. Upload via `curl -F file=@README.md http://127.0.0.1:8080/demo/oss`. Download via `GET /demo/oss?name=<object-name>`. |
| `kafka` | Broker on `localhost:9092`. Override with `KAFKA_SERVERS`. The demo declares the topic and its `-dlt` topic. |
| `rabbitmq` | Broker on `localhost:5672`. Override with `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USER` and `RABBITMQ_PASSWORD`. The demo declares the queue and its dead-letter queue. |
| `mqtt` | Broker on `localhost:1883`. Override with `MQTT_HOST` and `MQTT_PORT`. For a deployed service configure broker authentication and TLS in `jseed.mqtt`. |
| `job` | Set `XXL_JOB_ADMIN_URL` and `XXL_JOB_ACCESS_TOKEN`. Register executor app name `jseed-demo` and use the `jseedDemo` handler. |
| `tdengine` | Create the `jseed` database before running the example. Set `TDENGINE_USER`, `TDENGINE_PASSWORD` and optionally `TDENGINE_URL`. |

For message and cache profiles, send a string with `curl -H 'Content-Type: text/plain' --data-binary 'hello' http://127.0.0.1:8080/demo/<profile>`. Send `fail` to the Kafka or RabbitMQ endpoint to exercise retry and dead-letter routing. Consult the application's log for consumer receipts.

Sa-Token 调用顺序如下。启动 `satoken` profile 后，先登录，再将返回的 `data.tokenName` 和 `data.tokenValue` 作为请求头传给受保护接口。

```sh
curl -fsS -X POST http://127.0.0.1:8080/demo/auth/login
curl -fsS -H 'satoken: <tokenValue>' http://127.0.0.1:8080/demo/auth/me
curl -fsS -X POST -H 'satoken: <tokenValue>' http://127.0.0.1:8080/demo/auth/logout
```

在自己的应用里注入 `CryptoService` 即可使用加密和签名。这里的 `encryptionKey` 和 `signingKey` 分别从应用的密钥配置取得。

```java
String ciphertext = crypto.encrypt("hello", encryptionKey);
String plaintext = crypto.decrypt(ciphertext, encryptionKey);
String signature = crypto.hmacSha256(plaintext, signingKey);
boolean authentic = crypto.verifyHmac(plaintext, signature, signingKey);
String passwordHash = crypto.hashPassword(password);
boolean passwordMatches = crypto.matchesPassword(password, passwordHash);
```
