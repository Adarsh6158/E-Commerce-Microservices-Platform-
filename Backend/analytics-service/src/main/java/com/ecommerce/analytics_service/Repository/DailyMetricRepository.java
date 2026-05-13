package com.ecommerce.analytics_service.Repository;

import com.ecommerce.analytics_service.Domain.DailyMetric;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface DailyMetricRepository extends ReactiveMongoRepository<DailyMetric, String> {
    Mono<DailyMetric> findByDateAndMetricName(LocalDate date, String metricName);
}
