package com.ecommerce.analytics_service.Dto;

import com.ecommerce.analytics_service.Domain.AnalyticsEvent;
import com.ecommerce.analytics_service.Domain.DailyMetric;

public class DtoMapper {
    public static AnalyticsEventDto toDto(AnalyticsEvent entity) {
        return new AnalyticsEventDto(
                entity.getId(),
                entity.getEventType(),
                entity.getEntityId(),
                entity.getUserId(),
                entity.getTimestamp(),
                entity.getMetadata()
        );
    }

    public static DailyMetricDto toDto(DailyMetric entity) {
        return new DailyMetricDto(
                entity.getId(),
                entity.getDate(),
                entity.getMetricName(),
                entity.getValue()
        );
    }
}
