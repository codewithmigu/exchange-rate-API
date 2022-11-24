package com.exchange.api;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

@ExtendWith(SpringExtension.class)
public abstract class BaseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    protected WebTestClient.ResponseSpec doPost(String uri, Object requestBody) {
        return webTestClient.post()
                .uri(uri)
                .body(BodyInserters.fromValue(requestBody))
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }

    protected WebTestClient.ResponseSpec doGet(String uri, MultiValueMap<String, String> queryParams) {
        return webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParams(queryParams)
                        .build())
                .exchange();
    }
}
