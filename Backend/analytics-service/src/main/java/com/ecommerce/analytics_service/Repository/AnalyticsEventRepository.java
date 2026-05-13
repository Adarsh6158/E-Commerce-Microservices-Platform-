package com.ecommerce.analytics_service.Repository;

import com.ecommerce.analytics_service.Domain.AnalyticsEvent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsEventRepository extends ReactiveMongoRepository<AnalyticsEvent, String> {
}
