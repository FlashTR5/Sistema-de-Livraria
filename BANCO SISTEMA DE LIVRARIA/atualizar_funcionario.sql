USE livraria;

-- Execute uma vez em bases já criadas. Mantém o vínculo das vendas existente.
ALTER TABLE funcionario
    ADD COLUMN cargo VARCHAR(40) NOT NULL DEFAULT 'Atendente' AFTER cpf,
    ADD COLUMN data_admissao DATE NULL AFTER cargo;
