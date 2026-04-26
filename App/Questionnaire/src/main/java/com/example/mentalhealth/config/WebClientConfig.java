package com.example.mentalhealth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {



//    https://jsonplaceholder.typicode.com/posts/1

    @Bean
    public WebClient.Builder builder() {
        return WebClient.builder();
    }

}
