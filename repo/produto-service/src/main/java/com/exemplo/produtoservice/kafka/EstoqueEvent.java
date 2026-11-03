package com.exemplo.produtoservice.kafka;

/**
 * Evento de movimentacao de estoque consumido do topico Kafka
 * {@code estoque.movimentos}. {@code eventId} e a chave de idempotencia.
 *
 * @param eventId identificador unico do evento (idempotencia)
 * @param sku     SKU do produto afetado
 * @param delta   variacao de estoque (positivo = entrada, negativo = saida)
 * @param origem  sistema que emitiu o evento
 */
public record EstoqueEvent(
        String eventId,
        String sku,
        int delta,
        String origem
) {
}
