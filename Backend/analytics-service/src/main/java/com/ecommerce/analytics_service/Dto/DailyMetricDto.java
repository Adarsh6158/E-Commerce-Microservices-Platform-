package com.ecommerce.analytics_service.Dto;

import java.time.LocalDate;

public record DailyMetricDto(
        String id,
        LocalDate date,
        String metricName,
        Double value
) {}
