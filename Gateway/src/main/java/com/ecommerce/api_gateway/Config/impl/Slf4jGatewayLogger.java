package com.ecommerce.api_gateway.Config.impl;

import com.ecommerce.api_gateway.Config.interfaces.IGatewayLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Slf4jGatewayLogger implements IGatewayLogger {

    private final Map<Class<?>, Logger> loggers = new ConcurrentHashMap<>();

    private Logger getLogger(Class<?> clazz) {
        Class<?> keyClass = clazz != null ? clazz : Slf4jGatewayLogger.class;
        return loggers.computeIfAbsent(keyClass, LoggerFactory::getLogger);
    }

    @Override
    public void info(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).info(message, args);
    }

    @Override
    public void warn(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).warn(message, args);
    }

    @Override
    public void error(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).error(message, args);
    }

    @Override
    public void debug(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).debug(message, args);
    }
}
