package com.ecommerce.analytics_service.Controller;

import com.ecommerce.analytics_service.Service.AnalyticsService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/realtime")
    public Mono<Map<String, Object>> getRealtimeAnalytics() {
        return analyticsService.getRealtimeAnalytics();
    }
    
    @PostMapping("/track")
    public Mono<Void> trackEvent(@RequestBody Map<String, Object> payload) {
        String eventType = (String) payload.getOrDefault("eventType", "UNKNOWN");
        String entityId = (String) payload.getOrDefault("entityId", "");
        String userId = (String) payload.getOrDefault("userId", "");
        return analyticsService.trackEvent(eventType, entityId, userId, payload).then();
    }
}
