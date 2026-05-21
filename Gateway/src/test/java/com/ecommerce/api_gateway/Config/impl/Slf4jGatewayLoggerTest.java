package com.ecommerce.api_gateway.Config.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class Slf4jGatewayLoggerTest {

    private final Slf4jGatewayLogger logger = new Slf4jGatewayLogger();

    @Test
    @DisplayName("Should not throw exceptions when logging info")
    void info_whenCalled_shouldNotThrow() {
        assertDoesNotThrow(() -> logger.info(this.getClass(), "Test info message"));
    }

    @Test
    @DisplayName("Should handle null class for info")
    void info_whenNullClass_shouldNotThrow() {
        assertDoesNotThrow(() -> logger.info(null, "Test info message"));
    }

    @Test
    @DisplayName("Should not throw exceptions when logging warn")
    void warn_whenCalled_shouldNotThrow() {
        assertDoesNotThrow(() -> logger.warn(this.getClass(), "Test warn message"));
    }

    @Test
    @DisplayName("Should handle null message for warn")
    void warn_whenNullMessage_shouldNotThrow() {
        assertDoesNotThrow(() -> logger.warn(this.getClass(), null));
    }

    @Test
    @DisplayName("Should not throw exceptions when logging error")
    void error_whenCalled_shouldNotThrow() {
        assertDoesNotThrow(() -> logger.error(this.getClass(), "Test error message", new RuntimeException()));
    }

    @Test
    @DisplayName("Should handle null exception for error")
    void error_whenNullException_shouldNotThrow() {
        assertDoesNotThrow(() -> logger.error(this.getClass(), "Test error message", (Object) null));
    }

    @Test
    @DisplayName("Should not throw exceptions when logging debug")
    void debug_whenCalled_shouldNotThrow() {
        assertDoesNotThrow(() -> logger.debug(this.getClass(), "Test debug message"));
    }

    @Test
    @DisplayName("Should support varargs dynamically without failing")
    void info_whenVarargs_shouldNotThrow() {
        assertDoesNotThrow(() -> logger.info(this.getClass(), "Test {}, {}", "param1", "param2"));
    }
}
