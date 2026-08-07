package com.Arjun.MyMarket.apigateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.time.Duration;

@Configuration
public class RouteConfig {

    @Value("${product.service.id}")
    private String productServiceId;

    @Value("${cartorder.service.id}")
    private String cartOrderServiceId;
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

                .route("cart-order-service", route ->
                        route.path("/cart-order-service/**")
                                .filters(f -> f
                                        .addRequestHeader("x-api-gateway", "value from api gateway")
                                        .rewritePath("/cart-order-service/?(?<remaining>.*)", "/${remaining}")
                                        .retry(retryConfig -> retryConfig
                                                .setRetries(3)
                                                .setMethods(HttpMethod.GET, HttpMethod.POST)
                                                .setBackoff(
                                                        Duration.ofMillis(100),
                                                        Duration.ofMillis(1000),
                                                        2,
                                                        true
                                                )))
                                .uri(cartOrderServiceId))
                .build();
    }

}
