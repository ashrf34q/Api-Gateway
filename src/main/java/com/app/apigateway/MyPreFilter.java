package com.app.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

//@Component
public class MyPreFilter implements GlobalFilter {
    final Logger logger = LoggerFactory.getLogger(MyPreFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        logger.info("My first pre filter is executed...");

        /* Block any request that doesn't have an authorization header */

        ServerHttpRequest request = exchange.getRequest();

        if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
            return onError(exchange);
        }

        String requestPath = exchange.getRequest().getPath().toString();

        logger.info("Request path is: " + requestPath);

        HttpHeaders headers = exchange.getRequest().getHeaders();

        /* Returns a Set view of the keys contained in this map (Headers hashmap)
         The set is backed by the map,
         so changes to the map are reflected in the set, and vice-versa */
        Set<String> headerSet = headers.keySet();

        /*
        Here we're looping over the keys and for each key
        we're logging the value for that key from the map
         */
        headerSet.forEach((headerName) -> {
            logger.info(headerName + " " + headers.getFirst(headerName));
        });

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        return response.setComplete();
    }


} // end class