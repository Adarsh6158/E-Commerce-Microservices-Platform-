package com.ecommerce.api_gateway.Filter.impl;

import com.ecommerce.api_gateway.Filter.interfaces.IRouteExclusionService;
import com.ecommerce.api_gateway.properties.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Service
public class PathMatcherRouteExclusion implements IRouteExclusionService {

    private final SecurityProperties securityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> SWAGGER_PATTERNS = List.of(
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger-resources/**"
    );

    public PathMatcherRouteExclusion(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public boolean isExcluded(String path) {
        if (path == null) {
            return false;
        }
        
        if (SWAGGER_PATTERNS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            return true;
        }

        if (securityProperties.getExcludedPaths() == null) {
            return false;
        }

        return securityProperties.getExcludedPaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
