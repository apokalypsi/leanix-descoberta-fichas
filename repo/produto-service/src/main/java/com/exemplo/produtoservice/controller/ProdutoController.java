package com.exemplo.produtoservice.controller;

import com.exemplo.produtoservice.dto.ProdutoRequest;
import com.exemplo.produtoservice.dto.ProdutoResponse;
import com.exemplo.produtoservice.idempotency.IdempotencyService;
import com.exemplo.produtoservice.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST do CRUD de Produto. A criacao aceita o header opcional
 * {@code Idempotency-Key}: repeticoes com a mesma chave na janela nao criam
 * produto duplicado (retorna 200 em vez de 201).
 */
@RestController
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produtos", description = "CRUD de produtos com idempotencia e enriquecimento de endereco via ViaCEP")
public class ProdutoController {

    private final ProdutoService service;
    private final IdempotencyService idempotency;

    public ProdutoController(ProdutoService service, IdempotencyService idempotency) {
        this.service = service;
        this.idempotency = idempotency;
    }

    @PostMapping
    @Operation(summary = "Cria um produto (idempotente via header Idempotency-Key)")
    public ResponseEntity<ProdutoResponse> criar(
            @Valid @RequestBody ProdutoRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        if (idempotencyKey != null && !idempotencyKey.isBlank()
                && !idempotency.registrarSePrimeiraVez(idempotencyKey)) {
            // Repeticao detectada: nao cria de novo. Devolve o estado atual do SKU.
            return ResponseEntity.ok(service.criar(request));
        }
        ProdutoResponse criado = service.criar(request);
        return ResponseEntity
                .created(URI.create("/api/v1/produtos/" + criado.id()))
                .body(criado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um produto por id")
    public ResponseEntity<ProdutoResponse> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Lista todos os produtos")
    public ResponseEntity<List<ProdutoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um produto")
    public ResponseEntity<ProdutoResponse> atualizar(
            @PathVariable UUID id, @Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um produto")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        service.remover(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
