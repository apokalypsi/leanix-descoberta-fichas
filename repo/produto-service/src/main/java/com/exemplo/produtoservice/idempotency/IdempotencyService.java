package com.exemplo.produtoservice.idempotency;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Controle de idempotencia baseado em Redis. Uma chave de idempotencia
 * (enviada pelo cliente no header {@code Idempotency-Key}, ou o id de um
 * evento Kafka) e registrada com SET NX + TTL: a primeira ocorrencia grava e
 * segue; as repeticoes dentro da janela sao detectadas e ignoradas.
 */
@Service
public class IdempotencyService {

    private static final String PREFIXO = "idem:";

    private final StringRedisTemplate redis;
    private final Duration ttl;

    public IdempotencyService(StringRedisTemplate redis) {
        this.redis = redis;
        this.ttl = Duration.ofHours(24);
    }

    /**
     * Tenta registrar a chave. Retorna {@code true} se foi a PRIMEIRA vez
     * (pode processar); {@code false} se ja existia (repeticao — nao reprocessar).
     */
    public boolean registrarSePrimeiraVez(String chave) {
        Boolean gravou = redis.opsForValue()
                .setIfAbsent(PREFIXO + chave, "1", ttl);
        return Boolean.TRUE.equals(gravou);
    }

    /**
     * Indica se a chave ja foi vista (sem gravar).
     */
    public boolean jaProcessado(String chave) {
        return Boolean.TRUE.equals(redis.hasKey(PREFIXO + chave));
    }
}
