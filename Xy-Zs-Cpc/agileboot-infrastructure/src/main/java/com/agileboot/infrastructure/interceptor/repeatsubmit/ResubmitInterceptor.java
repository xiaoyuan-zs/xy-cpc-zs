package com.agileboot.infrastructure.interceptor.repeatsubmit;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.error.ErrorCode;
import com.agileboot.infrastructure.annotations.Resubmit;
import com.agileboot.infrastructure.cache.RedisUtil;
import com.agileboot.infrastructure.security.AuthenticationUtils;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

/**
 * 重复提交拦截器 如果涉及前后端加解密的话  也可以通过继承RequestBodyAdvice来实现
 *
 * @author valarchie
 */
@ControllerAdvice(basePackages = "com.agileboot")
@Slf4j
@RequiredArgsConstructor
public class ResubmitInterceptor extends RequestBodyAdviceAdapter {

    public static final String NO_LOGIN = "Anonymous";
    public static final String RESUBMIT_REDIS_KEY = "resubmit:{}:{}:{}";

    @NonNull
    private RedisUtil redisUtil;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
        Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasMethodAnnotation(Resubmit.class);
    }

    /**
     * @param body 仅获取有RequestBody注解的参数
     */
    @NotNull
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
        Class<? extends HttpMessageConverter<?>> converterType) {
        // 仅获取有RequestBody注解的参数
        String currentRequest = JSONUtil.toJsonStr(body);

        Resubmit resubmitAnno = parameter.getMethodAnnotation(Resubmit.class);
        if (resubmitAnno != null) {
            String redisKey = generateResubmitRedisKey(parameter.getMethod());

            log.info("请求重复提交拦截，当前key:{}, 当前参数：{}", redisKey, currentRequest);

            String preRequest = redisUtil.getCacheObject(redisKey);
            if (preRequest != null) {
                boolean isSameRequest = Objects.equals(currentRequest, preRequest);

                if (isSameRequest) {
                    throw new ApiException(ErrorCode.Client.COMMON_REQUEST_RESUBMIT);
                }
            }
            redisUtil.setCacheObject(redisKey, currentRequest, resubmitAnno.interval(), TimeUnit.SECONDS);
        }

        return body;
    }

    public String generateResubmitRedisKey(Method method) {
        String username;

        try {
            LoginUser loginUser = AuthenticationUtils.getLoginUser();
            username = loginUser.getUsername();
        } catch (Exception e) {
            username = NO_LOGIN;
        }

        return StrUtil.format(RESUBMIT_REDIS_KEY,
            method.getDeclaringClass().getName(),
            method.getName(),
            username);
    }
}
