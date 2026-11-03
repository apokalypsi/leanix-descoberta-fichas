package com.exemplo.produtoservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Representacao de saida de um Produto, incluindo o endereco de origem
 * enriquecido via ViaCEP.
 */
public record ProdutoResponse(
        UUID id,
        String sku,
        String nome,
        String descricao,
        BigDecimal preco,
        Integer quantidadeEstoque,
        String cepOrigem,
        String logradouroOrigem,
        String bairroOrigem,
        String cidadeOrigem,
        String ufOrigem,
        Instant criadoEm,
        Instant atualizadoEm
) {
}
