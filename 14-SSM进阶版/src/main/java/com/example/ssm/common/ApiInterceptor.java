package com.example.ssm.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * API 请求日志拦截器
 */
public class ApiInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiInterceptor.class);

    private long startTime;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        startTime = System.currentTimeMillis();
        log.info("[API] {} {} From {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        long cost = System.currentTimeMillis() - startTime;
        log.info("[API] {} {} - Status {} - {}ms",
                request.getMethod(), request.getRequestURI(), response.getStatus(), cost);
    }
}
