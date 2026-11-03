package com.exemplo.produtoservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resposta do ViaCEP (https://viacep.com.br/ws/{cep}/json/). Campos nao
 * usados sao ignorados; {@code erro=true} indica CEP inexistente.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EnderecoViaCep(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        @JsonProperty("erro") Boolean erro
) {
    public boolean naoEncontrado() {
        return Boolean.TRUE.equals(erro);
    }
}
