package com.ecommerce.analytics_service.Dto;

import java.time.Instant;
import java.util.Map;

public record AnalyticsEventDto(
        String id,
        String eventType,
        String entityId,
        String userId,
        Instant timestamp,
        Map<String, Object> metadata
) {}
