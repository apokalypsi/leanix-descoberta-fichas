package com.exemplo.produtoservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.exemplo.produtoservice.client.ViaCepClient;
import com.exemplo.produtoservice.domain.Produto;
import com.exemplo.produtoservice.dto.EnderecoViaCep;
import com.exemplo.produtoservice.dto.ProdutoRequest;
import com.exemplo.produtoservice.dto.ProdutoResponse;
import com.exemplo.produtoservice.exception.SkuDuplicadoException;
import com.exemplo.produtoservice.mapper.ProdutoMapper;
import com.exemplo.produtoservice.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    ProdutoRepository repository;
    @Mock
    ProdutoMapper mapper;
    @Mock
    ViaCepClient viaCepClient;

    @InjectMocks
    ProdutoService service;

    private ProdutoRequest requestValido(String sku, String cep) {
        return new ProdutoRequest(sku, "Teclado Mecanico", "ABNT2", new BigDecimal("199.90"), 10, cep);
    }

    @Test
    void criar_enriquece_endereco_via_viacep() {
        var req = requestValido("TEC-001", "01001000");
        when(repository.existsBySku("TEC-001")).thenReturn(false);
        when(viaCepClient.buscarEndereco("01001000")).thenReturn(Optional.of(
                new EnderecoViaCep("01001-000", "Praca da Se", "Se", "Sao Paulo", "SP", null)));
        when(repository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Produto.class))).thenAnswer(inv -> {
            Produto p = inv.getArgument(0);
            return new ProdutoResponse(p.getId(), p.getSku(), p.getNome(), p.getDescricao(),
                    p.getPreco(), p.getQuantidadeEstoque(), p.getCepOrigem(), p.getLogradouroOrigem(),
                    p.getBairroOrigem(), p.getCidadeOrigem(), p.getUfOrigem(), p.getCriadoEm(), p.getAtualizadoEm());
        });

        ProdutoResponse resposta = service.criar(req);

        assertThat(resposta.sku()).isEqualTo("TEC-001");
        assertThat(resposta.cidadeOrigem()).isEqualTo("Sao Paulo");
        assertThat(resposta.ufOrigem()).isEqualTo("SP");
        assertThat(resposta.logradouroOrigem()).isEqualTo("Praca da Se");
    }

    @Test
    void criar_rejeita_sku_duplicado() {
        var req = requestValido("TEC-001", null);
        when(repository.existsBySku("TEC-001")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(SkuDuplicadoException.class);
    }

    @Test
    void ajustarEstoque_nunca_fica_negativo() {
        Produto produto = Produto.builder()
                .sku("TEC-001").nome("Teclado").preco(new BigDecimal("199.90"))
                .quantidadeEstoque(3).build();
        when(repository.findBySku("TEC-001")).thenReturn(Optional.of(produto));
        when(repository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Produto.class))).thenAnswer(inv -> {
            Produto p = inv.getArgument(0);
            return new ProdutoResponse(p.getId(), p.getSku(), p.getNome(), p.getDescricao(),
                    p.getPreco(), p.getQuantidadeEstoque(), null, null, null, null, null, null, null);
        });

        ProdutoResponse resposta = service.ajustarEstoquePorSku("TEC-001", -10);

        assertThat(resposta.quantidadeEstoque()).isZero();
    }
}
