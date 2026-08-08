package com.example.apigateway.Filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static java.lang.String.format;

@Component
public class KeyAuthFilter extends AbstractGatewayFilterFactory<KeyAuthFilter.Config>{

    public KeyAuthFilter(){
        super(Config.class);
    }

    @Value("${apiKey}")
    private String apiKey;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpResponse response = exchange.getResponse();

            // Kiểm tra header apiKey có tồn tại không
            if(!exchange.getRequest().getHeaders().containsKey("apiKey")){
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            String key = exchange.getRequest().getHeaders().get("apiKey").get(0);

            // Kiểm tra giá trị apiKey có hợp lệ không
            if(!key.equals(apiKey)){
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            ServerHttpRequest request = exchange.getRequest();
            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    private Mono<Void> hanldeException(ServerWebExchange exchange,String messsage, HttpStatus status){
        ServerHttpResponse response=exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorResponse = String.format(
                "{\"timestamp\": \"%s\",\"status\": \"%d\",\"message\": \"%s\",\"path\": \"%s\"}}",
                java.time.ZonedDateTime.now().toString(),
                status.value(),status.getReasonPhrase(),messsage,exchange.getRequest().getURI().getPath()
        );
        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8))));
    }

    static class Config{

    }
}
