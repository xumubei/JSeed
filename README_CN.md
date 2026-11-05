# JSeed

[English](./README.md) | **简体中文**

JSeed 提供一组可独立安装、按需引入的 Spring Boot Starter。项目基于 Java 17 和 Spring Boot 4.1。

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
