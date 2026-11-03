package com.exemplo.produtoservice.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * Configura o Redis como backend de cache. TTLs distintos por cache:
 * - {@code enderecos}: endereco ViaCEP (estavel) — TTL longo;
 * - {@code produtos}: produto por id — TTL curto (invalidado na escrita).
 */
@Configuration
public class RedisCacheConfig {

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(10));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(base)
                .withCacheConfiguration("enderecos", base.entryTtl(Duration.ofDays(7)))
                .withCacheConfiguration("produtos", base.entryTtl(Duration.ofMinutes(5)))
                .build();
    }
}
