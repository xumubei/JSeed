# JSeed

[English](./README.md) | **简体中文**

JSeed 提供一组可独立安装、按需引入的 Spring Boot Starter。项目基于 Java 17 和 Spring Boot 4.1。

## NestJS 脚手架

TypeScript 版位于独立的 [NestJS Starter 仓库](https://github.com/xumubei/nestjs-starter)。

## 支持的模块

| Spring Boot Starter | Java 功能 |
| --- | --- |
| `jseed-core-starter` | 统一 API 响应、异常和工具类 |
| `jseed-web-starter` | Spring MVC、操作日志和 JSON 脱敏 |
| `jseed-swagger-starter` | OpenAPI 和 Swagger UI |
| `jseed-satoken-starter` | Sa-Token 认证与授权 |
| `jseed-mybatis-starter` | MyBatis-Plus 和动态表路由 |
| `jseed-datascope-starter` | 按数据权限过滤 SQL |
| `jseed-crypto-starter` | 加密、哈希和签名 |
| `jseed-job-starter` | XXL-Job 执行器集成 |
| `jseed-redis-starter` | 缓存、过期时间和分布式锁 |
| `jseed-kafka-starter` | 消息发布、消费、重试和死信主题 |
| `jseed-rabbitmq-starter` | 消息发布、消费、重试和死信队列 |
| `jseed-mqtt-starter` | MQTT 消息发布与订阅 |
| `jseed-netty-starter` | Netty TCP 客户端和服务端 |
| `jseed-websocket-starter` | WebSocket 会话与广播 |
| `jseed-oss-starter` | MinIO 上传、下载和对象管理 |
| `jseed-tdengine-starter` | TDengine SQL 查询与写入 |

配置和调用方式见 [Java 示例指南](jseed-starter-example/README.md)。

## 运行示例

在仓库根目录构建示例及其依赖，然后启动应用。

```sh
mvn -pl jseed-starter-example -am package
java -jar jseed-starter-example/target/jseed-starter-example-1.0.1-SNAPSHOT.jar
```

应用启动后，在另一个终端调用示例接口。

```sh
curl -fsS http://127.0.0.1:8080/demo/hello
curl -fsS 'http://127.0.0.1:8080/demo/crypto?text=hello'
curl -fsS http://127.0.0.1:8080/demo/items
```

示例应用默认只监听本机回环地址，并使用 H2 内存数据库。完整的接口列表和可选 Profile 见[示例指南](jseed-starter-example/README.md)。

如需在其他应用中使用本地构建的 Starter，先执行 `mvn install`，再在应用中引入所需的依赖，例如 `com.jseed:jseed-crypto-starter:1.0.1-SNAPSHOT`。升级现有应用时，请参阅[迁移说明](docs/migration.md)。

运行 `mvn verify` 执行常规检查。若要同时运行依赖 Docker 的集成测试，请执行 `mvn -Pintegration verify`。

## GitHub Actions

[Build and Test JSeed](.github/workflows/ci.yml) 工作流会在分支推送和拉取请求时运行，也可以在仓库的 Actions 页面手动触发。工作流使用 Java 17 构建所有模块，并运行单元测试和依赖 Docker 的集成测试。可在每次运行的 Artifacts 区域下载 Maven 测试报告。

当前发布状态和发布要求见[发布准备](docs/releasing.md)。

## 开源许可证

JSeed 源代码采用 [Apache-2.0](./LICENSE) 许可证。
