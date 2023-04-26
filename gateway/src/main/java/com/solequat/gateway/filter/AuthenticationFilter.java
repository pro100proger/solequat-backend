package com.solequat.gateway.filter;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RestTemplate restTemplate;

    @Autowired
    public AuthenticationFilter(RestTemplate restTemplate) {
        super(Config.class);
        this.restTemplate = restTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("Config: start");
        return (((exchange, chain) -> {

            log.info("GatewayFilter: start filtering");
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new RuntimeException("Missing authorization header!");
            }
            log.info("GatewayFilter: authorization header is not missing");

            String authHeader = Objects.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);

            if (authHeader!=null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }
            log.info("GatewayFilter: authHeader starts with Bearer");
            try {
                restTemplate.getForObject("http://localhost:8765/business-logic/api/v1/registration/validate?token="+authHeader,
                boolean.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return chain.filter(exchange);
        }));
    }

    public static class Config {

    }

}
