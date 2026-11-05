# Migrating to Java 17 and Spring Boot 4.1

- Use Java 17 or later and upgrade servlet, validation and annotation imports to Jakarta. Java SE packages such as `javax.crypto` retain their existing names.
- Web uses Boot's Jackson 3 mapper. Core's `JsonUtils` and `ObjectConverter` and existing Redis JSON values use Jackson 2. Keep serializers and mappers in their corresponding API families.
- Add `jseed-swagger-starter` explicitly for OpenAPI 3 and Swagger UI. Configure metadata under `jseed.swagger` and use `springdoc.api-docs.enabled` and `springdoc.swagger-ui.enabled` to control those endpoints.
- Redis and Sa-Token are explicitly enabled with `jseed.redis.enabled` and `jseed.satoken.enabled`. Supply a Redis URI under `jseed.redis.address` and configure Spring Data Redis for the same server. The default starter config uses a standalone Redis server. Supply your own `RedissonClient` for other topologies.
- MyBatis applies dynamic table names to the configured list under `mybatis-plus.split.tables`. Set `mybatis-plus.split.rule` to the request context key containing an identifier suffix. Data scope is enabled through `jseed.datascope.enabled`. Add `@DataScope` to protected mapper statements and register a resolver that gets allowed IDs from a trusted principal. An empty scope denies access. Use the supplied interceptor chain or explicitly add the data-permission interceptor before pagination when replacing it.
- The HTTP helper validates TLS certificates and hostnames. Enable outbound metadata logs with `OkBuilder.enableLog()`.
- `@Log` emits operation metadata. Add an `@EventListener` for `OperationLogEvent` to forward events to your own store.
- MinIO buckets are private by default. Use credentials or presigned URLs to download, or explicitly configure `oss.public-read` for newly created public buckets. `downloadFile` returns a temporary file that the caller deletes.

For crypto, keep the Base64-encoded AES key in a secret manager and expose it as `JSEED_CRYPTO_AES_KEY`. The keyed overloads accept explicit keys. Store passwords through the bcrypt helper and use a separate key for HMAC signatures.
