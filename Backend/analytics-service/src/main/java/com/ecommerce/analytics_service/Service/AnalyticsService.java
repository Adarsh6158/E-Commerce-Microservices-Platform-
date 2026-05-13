package com.ecommerce.analytics_service.Service;

import com.ecommerce.analytics_service.Domain.AnalyticsEvent;
import com.ecommerce.analytics_service.Domain.DailyMetric;
import com.ecommerce.analytics_service.Repository.AnalyticsEventRepository;
import com.ecommerce.analytics_service.Repository.DailyMetricRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyticsService {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);
    
    private final AnalyticsEventRepository eventRepository;
    private final DailyMetricRepository metricRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    public AnalyticsService(AnalyticsEventRepository eventRepository, 
                            DailyMetricRepository metricRepository,
                            ReactiveMongoTemplate mongoTemplate) {
        this.eventRepository = eventRepository;
        this.metricRepository = metricRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<AnalyticsEvent> trackEvent(String eventType, String entityId, String userId, Map<String, Object> metadata) {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setEventType(eventType);
        event.setEntityId(entityId);
        event.setUserId(userId);
        event.setTimestamp(Instant.now());
        event.setMetadata(metadata);
        
        return eventRepository.save(event)
                .doOnSuccess(savedEvent -> log.info("Tracked event: {}", savedEvent.getEventType()));
    }

    public Mono<DailyMetric> incrementMetric(String metricName, double incrementBy) {
        LocalDate today = LocalDate.now();
        Query query = new Query(Criteria.where("date").is(today).and("metricName").is(metricName));
        Update update = new Update().inc("value", incrementBy);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
        
        return mongoTemplate.findAndModify(query, update, options, DailyMetric.class);
    }

    public Mono<Map<String, Object>> getRealtimeAnalytics() {
        LocalDate today = LocalDate.now();
        
        return metricRepository.findAll()
                .filter(metric -> metric.getDate().equals(today))
                .collectList()
                .map(metrics -> {
                    Map<String, Object> response = new HashMap<>();
                    for (DailyMetric metric : metrics) {
                        response.put(metric.getMetricName(), metric.getValue());
                    }
                    if (!response.containsKey("TOTAL_REVENUE")) response.put("TOTAL_REVENUE", 0.0);
                    if (!response.containsKey("TOTAL_ORDERS")) response.put("TOTAL_ORDERS", 0.0);
                    if (!response.containsKey("CANCELLED_ORDERS")) response.put("CANCELLED_ORDERS", 0.0);
                    if (!response.containsKey("COMPLETED_PAYMENTS")) response.put("COMPLETED_PAYMENTS", 0.0);
                    if (!response.containsKey("REFUNDED_PAYMENTS")) response.put("REFUNDED_PAYMENTS", 0.0);
                    return response;
                });
    }
}
