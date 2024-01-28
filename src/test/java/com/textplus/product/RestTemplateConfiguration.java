package com.textplus.product;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RestTemplateConfiguration {

    @LocalServerPort
    private int port;

    @Bean
    public TestRestTemplate getRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        RestTemplateBuilder restTemplate = restTemplateBuilder.rootUri("http://localhost:" + port);
        return new TestRestTemplate(restTemplate);
    }
}
