package com.jseed.core.starter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Standard system response codes
 *
 * @author cyrus
 * @date: 2024-07-10
 */
@Getter
@AllArgsConstructor
public enum ResponseStatusEnum implements ErrorEnumSpecification {

    /**
     * Success
     */
    OK(200, "成功"),

    /**
     * Internal server error
     */
    SYSTEM_ERROR(500, "系统繁忙，请稍后再试!"),
    /**
     * Bad gateway
     */
    BAD_GATEWAY(502, "网关错误"),
    /**
     * Service unavailable after the circuit opens
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    /**
     * Service timeout
     */
    GATEWAY_TIMEOUT(504, "服务超时"),

    /**
     * Invalid request parameters
     */
    BAD_REQUEST(400, "请求错误"),

    /**
     * Unauthorized
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * Forbidden
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * The requested resource does not exist. Use 404 when a lookup finds no data.
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * The HTTP method is unsupported, for example GET on a POST endpoint. For WebSocket requests, this means the command is invalid or unknown.
     */
    METHOD_NOT_ALLOWED(405, "请求方式错误"),

    /**
     * Proxy authentication is required. In business flows this can represent authorization by a third-party agent.
     */
    PROXY_AUTHENTICATION_REQUIRED(407, "需要代理授权"),

    /**
     * Request timeout
     */
    REQUEST_TIMEOUT(408, "请求超时， 请重试"),

    /**
     * Conflict, such as when creating a resource that already exists.
     */
    CONFLICT(409, "冲突"),

    /**
     * The resource is gone. Applications may use this status for a soft-deleted resource that still exists in storage.
     *
     * @see #NOT_FOUND The resource is absent from the server.
     */
    GONE(410, "资源已被删除"),
    /**
     * Precondition failed
     */
    PRECONDITION_FAILED(412, "前置条件失败"),
    /**
     * The request payload is too large, such as when text or a file exceeds its limit.
     */
    PAYLOAD_TOO_LARGE(413, "请求的内容太长"),

    /**
     * The requested range is invalid, such as a time range or a range within a resource.
     */
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "请求的范围不符合要求"),

    /**
     * Insufficient space, time, or another resource.
     */
    INSUFFICIENT_SPACE_ON_RESOURCE(419, "资源空间不足"),

    /**
     * The resource or operation is locked. Use this status when another caller already holds the lock.
     */
    LOCKED(421, "请求的资源已被锁定，请稍后重试"),

    /**
     * An upgrade is required to use this API. Older clients can use this response to prompt an update.
     */
    UPGRADE_REQUIRED(426, "请求接口需要升级才能访问"),

    /**
     * Too many requests. Use this status when rate limiting rejects excess traffic.
     */
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),
    /**
     * Unsupported file type
     */
    UNSUPPORTED_FILE_TYPE(430, "不支持的文件类型"),
    /**
     * Unavailable for legal reasons
     */
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "因为法律原因，无法展示"),
    ;

    private final Integer code;
    private final String msg;

    public static ResponseStatusEnum ofCode(Integer code) {
        for (ResponseStatusEnum value : ResponseStatusEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("未匹配到返回枚举");
    }

}
