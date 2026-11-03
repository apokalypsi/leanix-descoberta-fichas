package com.exemplo.produtoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Aplicacao de exemplo: CRUD de Produto com consumo de evento Kafka,
 * cache e idempotencia em Redis, e integracao com o ViaCEP para enriquecer
 * o endereco de origem do produto.
 */
@SpringBootApplication
@EnableCaching
@EnableKafka
public class ProdutoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProdutoServiceApplication.class, args);
    }
}
