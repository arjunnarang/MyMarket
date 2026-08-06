package com.Arjun.MyMarket.apigateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Value("${product.service.id}")
    private String productServiceId;
    @Bean
    public RouteLocator route(RouteLocatorBuilder builder){
        return builder.routes()
                //creating a route "product-route" is the route id
                //giving route path
                //applying filters to rewrite the path if path received in request is "/product-service/whatever"
                //and this rewritten path will be connected to destination URI
                .route("product-route", route ->
                        route.path("/product-service/**")
                                .filters(f -> f
                                        .addRequestHeader("x-api-gateway", "value from api gateway")
                                        .circuitBreaker(c -> c.setName("productCircuitBreaker")
                                                                        .setFallbackUri("forward:/product-fallback"))
                                        .rewritePath("/product-service/?(?<remaining>.*)", "/${remaining}"))
                                .uri(productServiceId))
                .build();
    }

}
