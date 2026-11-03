package com.exemplo.produtoservice.exception;

public class CepInvalidoException extends RuntimeException {
    public CepInvalidoException(String mensagem) {
        super(mensagem);
    }
}
