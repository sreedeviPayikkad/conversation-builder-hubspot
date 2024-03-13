package com.hubspot.technicalassessment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.technicalassessment.domain.Exchanges;
import com.hubspot.technicalassessment.domain.Inbox;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RestServiceClient {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public RestServiceClient(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public Exchanges getMessages(String api) {
        return webClient
                .get()
                .uri(api)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, e -> Mono.error(new RuntimeException("Bad Request")))
                .onStatus(HttpStatusCode::is5xxServerError, e -> Mono.error(new RuntimeException("Internal server error")))
                .bodyToMono(Exchanges.class)
                .onErrorResume(error -> Mono.just(Exchanges.builder().build())) //in case API fails, fallback to providing a empty exchange object
                .block();
    }

    @SneakyThrows
    public String postInbox(String resultApi, Inbox inbox) {
        return webClient.post()
                .uri(resultApi)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(inbox))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, e -> Mono.error(new RuntimeException("Bad Request")))
                .onStatus(HttpStatusCode::is5xxServerError, e -> Mono.error(new RuntimeException("Internal server error")))
                .bodyToMono(String.class)
                .doOnError(error -> log.error("post failed with error message {}", error.getMessage(), error))
                .doOnSuccess(res -> log.info("inbox posted successfully, {}", res))
                .block();

    }
}
