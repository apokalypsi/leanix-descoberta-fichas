package com.exemplo.produtoservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Payload de criacao/atualizacao de Produto. O CEP de origem (opcional) e
 * usado para enriquecer o endereco via ViaCEP.
 */
public record ProdutoRequest(
        @NotBlank @Size(max = 40) String sku,
        @NotBlank @Size(max = 160) String nome,
        @Size(max = 1000) String descricao,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal preco,
        @NotNull @PositiveOrZero Integer quantidadeEstoque,
        @Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 digitos numericos") String cepOrigem
) {
}
