package com.vibo.ApiGatewayApplication.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig{

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("user-service",r->
                        r.path("/auth/**")
                                .uri("http://localhost:8081"))
                .route("product-service", r ->
                        r.path("/api/products/**")
                                .uri("http://localhost:8082"))
                .route("cart-service", r ->
                        r.path("/api/cart/**")
                                .uri("http://localhost:8082"))
                .route("payment-service",r->
                        r.path("/api/payment")
                                .uri("http://localhost:8083"))
                .route("ai-chat-service",r->
                        r.path("/api/ai/**")
                                .uri("http://localhost:8084"))
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

}
