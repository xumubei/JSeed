package com.jseed.web.starter.encryption.aop;

import cn.hutool.core.util.ReflectUtil;
import com.jseed.core.starter.enums.ResponseStatusEnum;
import com.jseed.core.starter.exception.BusinessException;
import com.jseed.core.starter.utils.ClassTypeUtil;
import com.jseed.core.starter.utils.ObjectConverter;
import com.jseed.web.starter.encryption.ApplicationAes;
import com.jseed.web.starter.property.WebConfigProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

import java.util.Objects;

/**
 * API response encryption
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@Aspect
@EnableAspectJAutoProxy
@Order
@AllArgsConstructor
@Slf4j
public class ApiEncryptAspect {

    private final WebConfigProperty.EncryptionProperty encryptionProperty;
    private final ApplicationAes applicationAes;

    @Pointcut("@annotation(com.jseed.web.starter.encryption.annotation.Encrypt)")
    public void point() {
    }

    @Around("point()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //Skip the advice if encryption is not configured.
        if (!encryptionProperty.isEnabled()) {
            return joinPoint.proceed();
        }
        Object result = joinPoint.proceed();
        try {
            Object data = ReflectUtil.getFieldValue(result, "data");
            if (Objects.isNull(data)) {
                return result;
            }
            // Encrypt the payload.
            if (ClassTypeUtil.isBasicType(data.getClass())) {
                String s = ObjectConverter.convertObj(data, String.class);
                data = StringUtils.isNotBlank(s) ? applicationAes.encrypt(s) : s;
            }else {
                data = applicationAes.encrypt(ObjectConverter.toJsonString(data));
            }
            // Set the data field via reflection.
            ReflectUtil.setFieldValue(result, "data", data);
        } catch (Exception e) {
            log.error("数据加密异常：", e);
            throw new BusinessException(ResponseStatusEnum.SYSTEM_ERROR, "数据加密异常");
        }
        return result;
    }

}
