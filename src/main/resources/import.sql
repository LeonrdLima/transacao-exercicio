-- Dados iniciais: executado pelo Hibernate logo depois de criar as tabelas.
INSERT INTO transacao (descricao, tipo, categoria, valor, data)
VALUES ('Compra feita no Débito - Mercado Livre', 'Débito', 'Market Place', 99.9, DATE '2026-10-01');

INSERT INTO transacao (descricao, tipo, categoria, valor, data)
VALUES ('Transferência feita via PIX para Fernanda', 'Débito', 'Transferência', 100, DATE '2026-10-03');

INSERT INTO transacao (descricao, tipo, categoria, valor, data)
VALUES ('Compra no iFood', 'Crédito', 'Alimentação', 87.5, DATE '2026-10-04');

