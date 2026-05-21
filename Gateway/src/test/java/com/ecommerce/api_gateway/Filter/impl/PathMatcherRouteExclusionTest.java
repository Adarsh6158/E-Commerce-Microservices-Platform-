package com.ecommerce.api_gateway.Filter.impl;

import com.ecommerce.api_gateway.properties.SecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathMatcherRouteExclusionTest {

    @Mock
    private SecurityProperties securityProperties;

    private PathMatcherRouteExclusion routeExclusion;

    @BeforeEach
    void setUp() {
        routeExclusion = new PathMatcherRouteExclusion(securityProperties);
    }

    @ParameterizedTest
    @MethodSource("provideExcludedPaths")
    @DisplayName("Should return true when path matches excluded pattern")
    void isExcluded_whenPathMatches_shouldReturnTrue(String path) {
        when(securityProperties.getExcludedPaths()).thenReturn(List.of("/api/auth/**", "/public/**"));

        assertTrue(routeExclusion.isExcluded(path));
    }

    @ParameterizedTest
    @MethodSource("provideNonExcludedPaths")
    @DisplayName("Should return false when path does not match excluded pattern")
    void isExcluded_whenPathDoesNotMatch_shouldReturnFalse(String path) {
        when(securityProperties.getExcludedPaths()).thenReturn(List.of("/api/auth/**"));

        assertFalse(routeExclusion.isExcluded(path));
    }

    private static Stream<String> provideExcludedPaths() {
        return Stream.of("/api/auth/login", "/public/assets/logo.png", "/api/auth/register",
                "/public/docs/openapi.yaml");
    }

    private static Stream<String> provideNonExcludedPaths() {
        return Stream.of("/api/secure/data", "/api/users", "/catalog/products", "/order/checkout");
    }
}
