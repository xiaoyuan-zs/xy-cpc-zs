package com.agileboot.infrastructure.aspectj;

import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


/**
 * @author valarchie
 */
@Aspect
@Component
@Slf4j
public class DbExceptionAspect {


    @Pointcut("within(com.agileboot.orm..*)")
    public void dbException() {
    }

    /**
     * 包装成ApiException 再交给globalExceptionHandler处理
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("dbException()")
    public Object aroundDbException(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed;
        try {
            proceed = joinPoint.proceed();
        } catch (Exception e) {
            throw new ApiException(e, ErrorCode.Internal.DB_INTERNAL_ERROR, e.getCause().getMessage());
        }
        return proceed;
    }


}
