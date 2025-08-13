package com.example.trendlog.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class FastApiClientConfig {
    @Bean
    public WebClient fastApiWebClient(
            @Value("${fastapi.base-url}") String baseUrl
    ) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(60));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                                .build()
                )
                .build();
    }

}
