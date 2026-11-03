package com.exemplo.produtoservice.mapper;

import com.exemplo.produtoservice.domain.Produto;
import com.exemplo.produtoservice.dto.ProdutoResponse;
import org.mapstruct.Mapper;

/**
 * Converte a entidade {@link Produto} para o DTO de saida {@link ProdutoResponse}.
 */
@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    ProdutoResponse toResponse(Produto produto);
}
