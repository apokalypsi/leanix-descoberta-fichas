package com.exemplo.produtoservice.repository;

import com.exemplo.produtoservice.domain.Produto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    Optional<Produto> findBySku(String sku);

    boolean existsBySku(String sku);
}
