package com.exemplo.produtoservice.exception;

public class SkuDuplicadoException extends RuntimeException {
    public SkuDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
