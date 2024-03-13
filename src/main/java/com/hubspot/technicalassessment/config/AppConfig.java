package com.hubspot.technicalassessment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@Slf4j
public class AppConfig {

    @Value("${app.inbox.baseUrl}")
    private String baseUrl;

    @Value("${app.inbox.apiKey}")
    private String apiKey;

    public final static String GET_API = "/dataset";
    public final static String POST_API = "/result";

    @Bean
    public WebClient webClient() {
        return WebClient
                .builder()
                .baseUrl(baseUrl)
                .filter(appendApiKey())
                .filter(logRequest())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.debug("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction appendApiKey() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            ClientRequest newRequest = ClientRequest.from(clientRequest)
                    .url(URI.create(clientRequest.url() + "?userKey=" + apiKey))
                    .build();
            return Mono.just(newRequest);
        });
    }
}
