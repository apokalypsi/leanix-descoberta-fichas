package com.exemplo.produtoservice.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Tradutor de excecoes de dominio para respostas HTTP (RFC 7807 ProblemDetail).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    ProblemDetail handleNaoEncontrado(ProdutoNaoEncontradoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Produto nao encontrado");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(SkuDuplicadoException.class)
    ProblemDetail handleSkuDuplicado(SkuDuplicadoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("SKU ja cadastrado");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(CepInvalidoException.class)
    ProblemDetail handleCepInvalido(CepInvalidoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setTitle("CEP invalido");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidacao(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Falha de validacao nos campos enviados");
        pd.setTitle("Requisicao invalida");
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> erros.put(fe.getField(), fe.getDefaultMessage()));
        pd.setProperty("erros", erros);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
