package com.exemplo.produtoservice.kafka;

import com.exemplo.produtoservice.exception.ProdutoNaoEncontradoException;
import com.exemplo.produtoservice.idempotency.IdempotencyService;
import com.exemplo.produtoservice.service.ProdutoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consome eventos de movimentacao de estoque do topico {@code estoque.movimentos}
 * e aplica o delta no produto correspondente. A idempotencia e garantida por
 * {@code eventId} no Redis: um evento reentregue (rebalance, retry) nao aplica o
 * delta duas vezes.
 */
@Component
public class EstoqueEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EstoqueEventConsumer.class);

    private final ProdutoService produtoService;
    private final IdempotencyService idempotency;

    public EstoqueEventConsumer(ProdutoService produtoService, IdempotencyService idempotency) {
        this.produtoService = produtoService;
        this.idempotency = idempotency;
    }

    @KafkaListener(
            topics = "${kafka.topicos.estoque-movimentos:estoque.movimentos}",
            groupId = "${spring.kafka.consumer.group-id:produto-service}")
    public void consumir(
            @Payload EstoqueEvent evento,
            @Header(name = KafkaHeaders.RECEIVED_TOPIC, required = false) String topico,
            Acknowledgment ack) {

        String chave = "estoque:" + evento.eventId();

        if (!idempotency.registrarSePrimeiraVez(chave)) {
            log.info("kafka.evento_duplicado_ignorado eventId={} sku={}", evento.eventId(), evento.sku());
            ack.acknowledge();
            return;
        }

        try {
            produtoService.ajustarEstoquePorSku(evento.sku(), evento.delta());
            log.info("kafka.estoque_aplicado topico={} eventId={} sku={} delta={}",
                    topico, evento.eventId(), evento.sku(), evento.delta());
            ack.acknowledge();
        } catch (ProdutoNaoEncontradoException ex) {
            // Produto ainda nao existe: confirma para nao travar a particao;
            // o evento vira obsoleto (o cadastro do produto o reconcilia depois).
            log.warn("kafka.estoque_sku_inexistente eventId={} sku={}", evento.eventId(), evento.sku());
            ack.acknowledge();
        }
    }
}
