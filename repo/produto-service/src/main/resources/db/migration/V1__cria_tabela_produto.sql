-- Tabela de produtos (CRUD) com endereco de origem enriquecido via ViaCEP.
CREATE TABLE tb_produto (
    id                  UUID         PRIMARY KEY,
    sku                 VARCHAR(40)  NOT NULL UNIQUE,
    nome                VARCHAR(160) NOT NULL,
    descricao           VARCHAR(1000),
    preco               NUMERIC(12,2) NOT NULL CHECK (preco > 0),
    quantidade_estoque  INTEGER      NOT NULL DEFAULT 0 CHECK (quantidade_estoque >= 0),
    cep_origem          VARCHAR(8),
    logradouro_origem   VARCHAR(200),
    bairro_origem       VARCHAR(120),
    cidade_origem       VARCHAR(120),
    uf_origem           VARCHAR(2),
    criado_em           TIMESTAMPTZ  NOT NULL,
    atualizado_em       TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_tb_produto_sku ON tb_produto (sku);
