package com.sy.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig extends RouteLocatorBuilder {

    public FilterConfig(ConfigurableApplicationContext context) {
        super(context);
    }

    @Bean
    public RouteLocator gatewayRoutes() {
        return this.routes()
            .route(predicate -> predicate.path("/first-service/**")
                .uri("http://localhost:8081"))
            .route(predicate -> predicate.path("/second-service/**")
                .uri("http://localhost:8082"))
            .build();
    }
}
