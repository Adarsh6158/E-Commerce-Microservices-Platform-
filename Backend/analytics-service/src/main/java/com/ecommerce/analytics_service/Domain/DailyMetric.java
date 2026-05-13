package com.ecommerce.analytics_service.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "daily_metrics")
public class DailyMetric {
    @Id
    private String id;
    private LocalDate date;
    private String metricName;
    private Double value;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
}
