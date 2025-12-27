package com.vibo.ApiGatewayApplication.security;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
public class JwtFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

//    @Override
//    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        String header = request.getHeader("Authorization");
//        if (header == null || !header.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//        String token = header.substring(7);
//        if (jwtUtil.validateToken(token)) {
//            String email = jwtUtil.extractEmail(token);
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, null);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        filterChain.doFilter(request,response);
//    }


        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

            String path = exchange.getRequest().getURI().getPath();

            if (path.startsWith("/auth")
                    || path.startsWith("/oauth2")
                    || (path.startsWith("/products")
                    && exchange.getRequest().getMethod().matches("GET"))) {
                return chain.filter(exchange);
            }


            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            Claims claims = jwtUtil.extractAllClaims(token);

            String userId = claims.getSubject();
            String email  = claims.get("email", String.class);

            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", userId)
                    .header("X-Email", email)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }
    }

