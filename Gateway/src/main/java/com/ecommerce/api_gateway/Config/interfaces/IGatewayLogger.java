package com.ecommerce.api_gateway.Config.interfaces;

public interface IGatewayLogger {
    void info(Class<?> clazz, String message, Object... args);
    void warn(Class<?> clazz, String message, Object... args);
    void error(Class<?> clazz, String message, Object... args);
    void debug(Class<?> clazz, String message, Object... args);
}
