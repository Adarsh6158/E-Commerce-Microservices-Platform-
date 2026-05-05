FROM eclipse-temurin:17-jre-jammy

LABEL org.opencontainers.image.authors="Adarsh" \
      org.opencontainers.image.title="search-service" \
      org.opencontainers.image.description="Search service for ecommerce platform" \
      org.opencontainers.image.version="1.0.0"

LABEL maintainer="Adarsh" service="search-service"

RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -r appgroup && useradd -r -g appgroup -s /usr/sbin/nologin appuser

WORKDIR /app

COPY target/search-service-1.0.0.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8083

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD curl -sf http://localhost:8083/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]