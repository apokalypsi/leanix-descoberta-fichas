package com.exemplo.produtoservice.service;

import com.exemplo.produtoservice.client.ViaCepClient;
import com.exemplo.produtoservice.domain.Produto;
import com.exemplo.produtoservice.dto.EnderecoViaCep;
import com.exemplo.produtoservice.dto.ProdutoRequest;
import com.exemplo.produtoservice.dto.ProdutoResponse;
import com.exemplo.produtoservice.exception.ProdutoNaoEncontradoException;
import com.exemplo.produtoservice.exception.SkuDuplicadoException;
import com.exemplo.produtoservice.mapper.ProdutoMapper;
import com.exemplo.produtoservice.repository.ProdutoRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negocio do CRUD de Produto. O produto por id e cacheado no Redis
 * (cache {@code produtos}); escritas invalidam a entrada. Na criacao/atualizacao,
 * o CEP de origem e enriquecido via ViaCEP.
 */
@Service
public class ProdutoService {

    private static final Logger log = LoggerFactory.getLogger(ProdutoService.class);

    private final ProdutoRepository repository;
    private final ProdutoMapper mapper;
    private final ViaCepClient viaCepClient;

    public ProdutoService(ProdutoRepository repository, ProdutoMapper mapper, ViaCepClient viaCepClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.viaCepClient = viaCepClient;
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest req) {
        if (repository.existsBySku(req.sku())) {
            throw new SkuDuplicadoException("Ja existe produto com o SKU " + req.sku());
        }
        Produto produto = Produto.builder()
                .sku(req.sku())
                .nome(req.nome())
                .descricao(req.descricao())
                .preco(req.preco())
                .quantidadeEstoque(req.quantidadeEstoque())
                .cepOrigem(req.cepOrigem())
                .build();
        enriquecerEndereco(produto);
        Produto salvo = repository.save(produto);
        log.info("produto.criado id={} sku={}", salvo.getId(), salvo.getSku());
        return mapper.toResponse(salvo);
    }

    @Cacheable(cacheNames = "produtos", key = "#id")
    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto nao encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @CacheEvict(cacheNames = "produtos", key = "#id")
    @Transactional
    public ProdutoResponse atualizar(UUID id, ProdutoRequest req) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto nao encontrado: " + id));

        if (!produto.getSku().equals(req.sku()) && repository.existsBySku(req.sku())) {
            throw new SkuDuplicadoException("Ja existe produto com o SKU " + req.sku());
        }
        produto.setSku(req.sku());
        produto.setNome(req.nome());
        produto.setDescricao(req.descricao());
        produto.setPreco(req.preco());
        produto.setQuantidadeEstoque(req.quantidadeEstoque());
        produto.setCepOrigem(req.cepOrigem());
        enriquecerEndereco(produto);

        Produto salvo = repository.save(produto);
        log.info("produto.atualizado id={}", salvo.getId());
        return mapper.toResponse(salvo);
    }

    @CacheEvict(cacheNames = "produtos", key = "#id")
    @Transactional
    public void remover(UUID id) {
        if (!repository.existsById(id)) {
            throw new ProdutoNaoEncontradoException("Produto nao encontrado: " + id);
        }
        repository.deleteById(id);
        log.info("produto.removido id={}", id);
    }

    /**
     * Ajuste de estoque idempotente — usado pelo consumidor Kafka. A
     * idempotencia (nao aplicar duas vezes) e garantida pelo chamador.
     */
    @CacheEvict(cacheNames = "produtos", key = "#result.id")
    @Transactional
    public ProdutoResponse ajustarEstoquePorSku(String sku, int delta) {
        Produto produto = repository.findBySku(sku)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto nao encontrado por SKU: " + sku));
        int novo = Math.max(0, produto.getQuantidadeEstoque() + delta);
        produto.setQuantidadeEstoque(novo);
        Produto salvo = repository.save(produto);
        log.info("produto.estoque_ajustado sku={} delta={} novo={}", sku, delta, novo);
        return mapper.toResponse(salvo);
    }

    private void enriquecerEndereco(Produto produto) {
        if (produto.getCepOrigem() == null || produto.getCepOrigem().isBlank()) {
            return;
        }
        Optional<EnderecoViaCep> endereco = viaCepClient.buscarEndereco(produto.getCepOrigem());
        endereco.ifPresent(e -> {
            produto.setLogradouroOrigem(e.logradouro());
            produto.setBairroOrigem(e.bairro());
            produto.setCidadeOrigem(e.localidade());
            produto.setUfOrigem(e.uf());
        });
    }
}
