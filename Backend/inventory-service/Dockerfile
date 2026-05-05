FROM eclipse-temurin:17-jre-jammy
LABEL maintainer="Adarsh" service="inventory-service"

RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -r appgroup && useradd -r -g appgroup -s /usr/sbin/nologin appuser

WORKDIR /app

COPY target/inventory-service-1.0.0.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8086

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD curl -sf http://localhost:8086/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]