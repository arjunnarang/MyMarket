package com.Arjun.MyMarket.apigateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

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
                                        .requestRateLimiter(rateLimitConfig -> rateLimitConfig
                                                .setKeyResolver(keyResolver())  //setting key resolver here
                                                .setRateLimiter(redisRateLimiter()))

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


    //this keyresolver is basically on what basis does api know that requests are coming from a single user
    //like ipaddress, some header, idempotency key, or something like that.
    @Bean
    public KeyResolver keyResolver(){
        return exchange -> Mono.just(exchange.getRequest().getHeaders().getFirst("user"));
    }

    @Bean
    public RedisRateLimiter redisRateLimiter(){
        //replenishRate - rate at which bucket is filled with tokens in 1 second
        //burstCapacity - maximum number of requests a user is allowed in a single second or maximum tokens bucket can hold
        //requestedToken - property is how many tokens a request costs default is 1 means 1 token is 1 request
        //note: BurstCapacity(1) must be greater than or equal than replenishRate(3)
        return new RedisRateLimiter(1,1, 2);
    }
}
