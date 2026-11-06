# JSeed

**English** | [简体中文](./README_CN.md)

JSeed is a collection of independently installable Spring Boot starters. The build targets Java 17 and Spring Boot 4.1.

## NestJS starter

The TypeScript starter is available in the [independent NestJS Starter repository](https://github.com/xumubei/nestjs-starter).

## Supported modules

| Spring Boot starter | Java capabilities |
| --- | --- |
| `jseed-core-starter` | Shared API responses, exceptions, and utilities |
| `jseed-web-starter` | Spring MVC, operation logs, and JSON masking |
| `jseed-swagger-starter` | OpenAPI and Swagger UI |
| `jseed-satoken-starter` | Sa-Token authentication and authorization |
| `jseed-mybatis-starter` | MyBatis-Plus and dynamic table routing |
| `jseed-datascope-starter` | SQL filtering by data permissions |
| `jseed-crypto-starter` | Encryption, hashing, and signing |
| `jseed-job-starter` | XXL-Job executor integration |
| `jseed-redis-starter` | Cache, TTLs, and distributed locks |
| `jseed-kafka-starter` | Publishing, consumption, retries, and dead-letter topics |
| `jseed-rabbitmq-starter` | Publishing, consumption, retries, and dead-letter queues |
| `jseed-mqtt-starter` | MQTT publishing and subscriptions |
| `jseed-netty-starter` | Netty TCP client and server |
| `jseed-websocket-starter` | WebSocket sessions and broadcast |
| `jseed-oss-starter` | MinIO uploads, downloads, and object management |
| `jseed-tdengine-starter` | TDengine SQL queries and writes |

See the [Java example guide](jseed-starter-example/README.md) for configuration and usage.

## Run the example

From the repository root, build the example and its dependencies:

```sh
mvn -pl jseed-starter-example -am package
java -jar jseed-starter-example/target/jseed-starter-example-1.0.1-SNAPSHOT.jar
curl -fsS http://127.0.0.1:8080/demo/hello
curl -fsS 'http://127.0.0.1:8080/demo/crypto?text=hello'
curl -fsS http://127.0.0.1:8080/demo/items
```

The default example starts on loopback and uses an in-memory H2 database. See the [example guide](jseed-starter-example/README.md) for the complete call map and optional profiles.

To integrate a starter from a local build, run `mvn install` and add the desired artifact, such as `com.jseed:jseed-crypto-starter:1.0.1-SNAPSHOT`. See [migration notes](docs/migration.md) when upgrading an existing application.

Run `mvn verify` for the standard checks. Use `mvn -Pintegration verify` to also run the Docker-backed integration tests.

## GitHub Actions

[Build and Test JSeed](.github/workflows/ci.yml) runs on branch pushes and pull requests. You can also start it manually from the repository's Actions tab. The workflow uses Java 17 to build all modules and run the unit tests and Docker-backed integration tests. The run's Artifacts section contains the Maven test reports.

See [release preparation](docs/releasing.md) for current publication status and requirements.

## License

JSeed source code is licensed under [Apache-2.0](./LICENSE).
