package com.exemplo.produtoservice.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * RestClient dedicado ao ViaCEP, com base URL e timeouts configuraveis.
 */
@Configuration
public class RestClientConfig {

    @Bean
    RestClient viaCepRestClient(
            @Value("${integracoes.viacep.base-url:https://viacep.com.br}") String baseUrl,
            @Value("${integracoes.viacep.connect-timeout-ms:2000}") long connectTimeoutMs,
            @Value("${integracoes.viacep.read-timeout-ms:3000}") long readTimeoutMs) {

        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .withReadTimeout(Duration.ofMillis(readTimeoutMs));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }
}
