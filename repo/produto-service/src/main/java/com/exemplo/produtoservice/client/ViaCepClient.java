package com.exemplo.produtoservice.client;

import com.exemplo.produtoservice.dto.EnderecoViaCep;
import com.exemplo.produtoservice.exception.CepInvalidoException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente da API publica ViaCEP. A consulta e cacheada em Redis (cache
 * {@code enderecos}) porque o endereco de um CEP e estavel — evita martelar
 * o ViaCEP a cada criacao/atualizacao de produto.
 */
@Component
public class ViaCepClient {

    private static final Logger log = LoggerFactory.getLogger(ViaCepClient.class);

    private final RestClient restClient;

    public ViaCepClient(RestClient viaCepRestClient) {
        this.restClient = viaCepRestClient;
    }

    /**
     * Consulta o endereco de um CEP (8 digitos, so numeros). Resultado cacheado
     * por CEP. Retorna vazio quando o CEP nao existe no ViaCEP.
     */
    @Cacheable(cacheNames = "enderecos", key = "#cep")
    public Optional<EnderecoViaCep> buscarEndereco(String cep) {
        if (cep == null || !cep.matches("\\d{8}")) {
            throw new CepInvalidoException("CEP deve conter 8 digitos numericos: " + cep);
        }
        try {
            EnderecoViaCep resposta = restClient.get()
                    .uri("/ws/{cep}/json/", cep)
                    .retrieve()
                    .body(EnderecoViaCep.class);

            if (resposta == null || resposta.naoEncontrado()) {
                log.info("viacep.cep_nao_encontrado cep={}", cep);
                return Optional.empty();
            }
            return Optional.of(resposta);
        } catch (RestClientException ex) {
            log.warn("viacep.falha_consulta cep={} motivo={}", cep, ex.getMessage());
            return Optional.empty();
        }
    }
}
