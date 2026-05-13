package com.ecommerce.analytics_service;

import com.ecommerce.analytics_service.Controller.AnalyticsController;
import com.ecommerce.analytics_service.Domain.AnalyticsEvent;
import com.ecommerce.analytics_service.Domain.DailyMetric;
import com.ecommerce.analytics_service.Event.AnalyticsEventConsumer;
import com.ecommerce.analytics_service.Repository.AnalyticsEventRepository;
import com.ecommerce.analytics_service.Repository.DailyMetricRepository;
import com.ecommerce.analytics_service.Service.AnalyticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceApplicationTests {

    @Mock
    private AnalyticsEventRepository eventRepository;

    @Mock
    private DailyMetricRepository metricRepository;

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void testTrackEvent() {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setEventType("TEST_EVENT");
        when(eventRepository.save(any(AnalyticsEvent.class))).thenReturn(Mono.just(event));

        Mono<AnalyticsEvent> result = analyticsService.trackEvent("TEST_EVENT", "123", "user1", Map.of());

        StepVerifier.create(result)
                .expectNextMatches(e -> e.getEventType().equals("TEST_EVENT"))
                .verifyComplete();

        verify(eventRepository, times(1)).save(any(AnalyticsEvent.class));
    }

    @Test
    void testIncrementMetric() {
        DailyMetric metric = new DailyMetric();
        metric.setMetricName("TOTAL_ORDERS");
        metric.setValue(1.0);

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(DailyMetric.class)))
                .thenReturn(Mono.just(metric));

        Mono<DailyMetric> result = analyticsService.incrementMetric("TOTAL_ORDERS", 1.0);

        StepVerifier.create(result)
                .expectNextMatches(m -> m.getMetricName().equals("TOTAL_ORDERS") && m.getValue() == 1.0)
                .verifyComplete();

        verify(mongoTemplate, times(1)).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(DailyMetric.class));
    }

    @Test
    void testGetRealtimeAnalytics() {
        DailyMetric m1 = new DailyMetric();
        m1.setDate(LocalDate.now());
        m1.setMetricName("TOTAL_ORDERS");
        m1.setValue(10.0);

        DailyMetric m2 = new DailyMetric();
        m2.setDate(LocalDate.now());
        m2.setMetricName("TOTAL_REVENUE");
        m2.setValue(100.0);

        when(metricRepository.findAll()).thenReturn(Flux.just(m1, m2));

        Mono<Map<String, Object>> result = analyticsService.getRealtimeAnalytics();

        StepVerifier.create(result)
                .expectNextMatches(map -> 
                        map.get("TOTAL_ORDERS").equals(10.0) &&
                        map.get("TOTAL_REVENUE").equals(100.0) &&
                        map.get("CANCELLED_ORDERS").equals(0.0)
                )
                .verifyComplete();
    }

    @Test
    void testAnalyticsEventConsumer() throws Exception {
        AnalyticsService mockService = mock(AnalyticsService.class);
        ObjectMapper mapper = new ObjectMapper();
        AnalyticsEventConsumer consumer = new AnalyticsEventConsumer(mockService, mapper);

        String json = "{\"orderId\":\"ord-123\", \"userId\":\"user-1\", \"totalAmount\":50.0}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("order.created", 0, 0, "ord-123", json);

        when(mockService.trackEvent(anyString(), anyString(), anyString(), anyMap())).thenReturn(Mono.empty());
        when(mockService.incrementMetric(anyString(), anyDouble())).thenReturn(Mono.empty());

        consumer.consumeOrderCreated(record, acknowledgment);

        verify(mockService).trackEvent(eq("ORDER_CREATED"), eq("ord-123"), eq("user-1"), anyMap());
        verify(mockService).incrementMetric(eq("TOTAL_ORDERS"), eq(1.0));
        verify(mockService).incrementMetric(eq("TOTAL_REVENUE"), eq(50.0));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void testEndpoints() {
        AnalyticsController controller = new AnalyticsController(analyticsService);
        WebTestClient webTestClient = WebTestClient.bindToController(controller).build();

        // Mock getting analytics
        DailyMetric m1 = new DailyMetric();
        m1.setDate(LocalDate.now());
        m1.setMetricName("ACTIVE_USERS");
        m1.setValue(42.0);

        when(metricRepository.findAll()).thenReturn(Flux.just(m1));

        webTestClient.get()
                .uri("/analytics/realtime")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.ACTIVE_USERS").isEqualTo(42.0)
                .jsonPath("$.TOTAL_ORDERS").isEqualTo(0.0);

        // Mock tracking event
        when(eventRepository.save(any(AnalyticsEvent.class))).thenReturn(Mono.just(new AnalyticsEvent()));
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", "CUSTOM_VIEW");
        payload.put("entityId", "prod-1");
        
        webTestClient.post()
                .uri("/analytics/track")
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk();
                
        verify(eventRepository, times(1)).save(any(AnalyticsEvent.class));
    }
}
