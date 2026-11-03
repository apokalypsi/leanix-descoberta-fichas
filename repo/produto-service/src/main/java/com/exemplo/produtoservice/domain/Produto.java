package com.exemplo.produtoservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade de dominio Produto, persistida na tabela {@code tb_produto}.
 * Guarda tambem o endereco de origem (CEP + logradouro/cidade/UF)
 * enriquecido pela integracao com o ViaCEP.
 */
@Entity
@Table(name = "tb_produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "sku", nullable = false, unique = true, length = 40)
    private String sku;

    @Column(name = "nome", nullable = false, length = 160)
    private String nome;

    @Column(name = "descricao", length = 1000)
    private String descricao;

    @Column(name = "preco", nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque;

    // --- Endereco de origem (enriquecido via ViaCEP) ---
    @Column(name = "cep_origem", length = 8)
    private String cepOrigem;

    @Column(name = "logradouro_origem", length = 200)
    private String logradouroOrigem;

    @Column(name = "bairro_origem", length = 120)
    private String bairroOrigem;

    @Column(name = "cidade_origem", length = 120)
    private String cidadeOrigem;

    @Column(name = "uf_origem", length = 2)
    private String ufOrigem;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @PrePersist
    void aoCriar() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        Instant agora = Instant.now();
        criadoEm = agora;
        atualizadoEm = agora;
    }

    @PreUpdate
    void aoAtualizar() {
        atualizadoEm = Instant.now();
    }
}
