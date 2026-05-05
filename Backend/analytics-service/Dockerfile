FROM eclipse-temurin:17-jre-jammy

LABEL maintainer="Adarsh" service="analytic-service"

RUN groupadd -r appgroup && useradd -r -g appgroup -s /usr/sbin/nologin appuser
WORKDIR /app
COPY target/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8090
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]