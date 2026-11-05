package com.jseed.core.starter.model;

import com.jseed.core.starter.enums.ResponseStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Response body
 *
 * @author cyrus
 */
@Data
@NoArgsConstructor
@Schema(description = "请求响应对象")
public class R<T> implements Serializable {
    private int code;

    @Schema(description = "消息内容")
    private String msg;

    @Schema(description = "数据对象")
    private T data;

    public static <T> R<T> ok() {
        return restResult(null, ResponseStatusEnum.OK.getCode(), ResponseStatusEnum.OK.getMsg());
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, ResponseStatusEnum.OK.getCode(), ResponseStatusEnum.OK.getMsg());
    }

    public static <T> R<T> fail(int code, String message) {
        return restResult(null, code, message);
    }

    public static <T> R<T> fail(ResponseStatusEnum status) {
        return restResult(null, status.getCode(), status.getMsg());
    }

    public static <T> R<T> fail(ResponseStatusEnum status, String message) {
        return restResult(null, status.getCode(), message);
    }

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }

}
