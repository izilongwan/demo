package com.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.annotation.BarAnno;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Aspect
@Slf4j
public class BarAnnoAspect {
    @Pointcut("@annotation(barAnno)")
    public void pointcut(BarAnno barAnno) {

    }

    @Around("pointcut(barAnno)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, BarAnno barAnno) throws Throwable {
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = ((MethodSignature) signature);
        // 方法
        Method method = methodSignature.getMethod();
        // 方法的类
        Class<?> c = proceedingJoinPoint.getTarget().getClass();

        // 参数值数组
        Object[] args = proceedingJoinPoint.getArgs();
        // 参数数组
        Parameter[] parameters = method.getParameters();
        log.info("c => {}; args => {}", c, args);

        for (Parameter parameter : parameters) {
            // 参数名
            String name = parameter.getName();
            // 参数名类型
            Class<?> type = parameter.getType();
            // 参数注解
            Annotation[] annos = parameter.getDeclaredAnnotations();

            log.info("name => {}; type => {}; annos => {}", name, type, annos);
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) requestAttributes);

        HttpServletRequest request = servletRequestAttributes.getRequest();
        log.info("{} {}", request.getRequestURI(), request.getQueryString());

        return proceedingJoinPoint.proceed();
    }

}
