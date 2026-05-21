package com.ecommerce.search_service.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI searchServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Search Service API")
                        .description("E-Commerce product search, filtering, suggestions, and recommendations powered by Elasticsearch")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Adarsh")
                                .email("adarsh@shopflux.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Local"),
                        new Server().url("http://search-service:8083").description("Docker")));
    }
}
