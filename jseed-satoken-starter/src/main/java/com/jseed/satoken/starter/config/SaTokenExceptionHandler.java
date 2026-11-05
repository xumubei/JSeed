package com.jseed.satoken.starter.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.router.SaRouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jseed.core.starter.enums.ResponseStatusEnum;
import com.jseed.core.starter.exception.BusinessException;
import com.jseed.core.starter.model.R;
import com.jseed.core.starter.okhttp.constants.HttpContentType;
import com.jseed.satoken.starter.SecurityProperty;
import com.jseed.web.starter.GlobalExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Enumeration;

/**
 * Sa-Token exception handling
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@Slf4j
@AllArgsConstructor
@RestControllerAdvice(annotations = {Controller.class, RestController.class})
public class SaTokenExceptionHandler extends GlobalExceptionHandler {

    private final SecurityProperty property;
    private final static BigDecimal FILE_SIZE = BigDecimal.valueOf(1024);

    /**
     * Checks whether the request URI is excluded.
     */
    private boolean isExcludes() {
        return SaRouter.isMatchCurrURI(property.getExcludes());
    }

    /**
     * Permission denied
     *
     * @return
     */
    @ExceptionHandler({NotPermissionException.class, NotRoleException.class})
    public R<Void> unauthorizedException() {
        return R.fail(ResponseStatusEnum.UNAUTHORIZED);
    }

    /**
     * Authentication required
     *
     * @param exception
     * @return
     */
    @ExceptionHandler
    public R<Void> notLoginException(NotLoginException exception) {
        return R.fail(ResponseStatusEnum.UNAUTHORIZED, "未登录");
    }

    /**
     * Secondary authentication failed
     *
     * @param e
     * @return
     */
    @ExceptionHandler
    public R<Void> notSafeException(NotSafeException e) {
        return R.fail(ResponseStatusEnum.UNAUTHORIZED, "二级认证异常：" + e.getMessage());
    }

    /**
     * Handles business exceptions.
     */
    @ExceptionHandler(value = BusinessException.class)
    public R<Void> defaultErrorHandler(HttpServletRequest req, BusinessException e) {
        try {
            String session = "Anonymous";
            log.error("\n     uri -> {}\n  method -> {}\n   token -> {}\noperator -> {}\n   param -> {}\n message -> {}",
                    req.getServletPath(), req.getMethod(),
                    "", session, getParam(req), e.getMsg(), e);
        } catch (Exception error) {
            log.error("查询用户信息异常:", error);
        }
        return R.fail(e.getCode(), e.getMsg());
    }

    /**
     * Handles system exceptions.
     */
    @ExceptionHandler(value = Exception.class)
    public R<Void> defaultErrorHandler(HttpServletRequest req, Exception e) {
        try {
            String session = "Anonymous";
            log.error("\n     uri -> {}\n  method -> {}\n   token -> {}\noperator -> {}\n   param -> {}\n message -> {}",
                    req.getServletPath(), req.getMethod(),
                     "",
                    session, getParam(req), ResponseStatusEnum.SYSTEM_ERROR.getMsg(), e);
        } catch (Exception error) {
            log.error("查询用户信息异常:", error);
        }
        return R.fail(ResponseStatusEnum.SYSTEM_ERROR);
    }

    @SneakyThrows
    private String getParam(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isBlank(request.getContentType())) {
            // If needed, parse the query string into key-value pairs for JSON conversion.
            return request.getQueryString();
        }
        if (request.getContentType().startsWith(HttpContentType.JSON)) {
            String requestJsonStr = getRequestJsonStr(request);
            if (StringUtils.isNotBlank(requestJsonStr)) {
                return JSON.toJSONString(JSON.parseObject(requestJsonStr));
            }
            return "{}";
        }
        if (request.getContentType().startsWith(HttpContentType.FORM)) {
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                jsonObject.put(name, request.getParameter(name));
            }
            return JSON.toJSONString(jsonObject);
        }
        if (request.getContentType().startsWith(HttpContentType.MULTIPART_FILE)) {
            request.getParts().forEach(it -> {
                // KB
                String company = "KB";
                BigDecimal size = BigDecimal.valueOf(it.getSize()).divide(FILE_SIZE, 2, RoundingMode.HALF_UP);
                // MB
                if (FILE_SIZE.compareTo(size) <= 0) {
                    company = "MB";
                    size = BigDecimal.valueOf(it.getSize()).divide(FILE_SIZE.multiply(FILE_SIZE), 2, RoundingMode.HALF_UP);
                }
                jsonObject.put(it.getName(), String.format("%s%s", size, company));
            });
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                jsonObject.put(name, request.getParameter(name));
            }
            return JSON.toJSONString(jsonObject);
        }
        return "{}";
    }

    private static String getRequestJsonStr(HttpServletRequest request)
            throws IOException {
        byte[] buffer = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }

    private static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return new byte[0];
        }
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }
}
