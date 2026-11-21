USE livraria;

-- Adiciona campos para localização física na prateleira e data da última alteração de estoque
ALTER TABLE estoque 
    ADD COLUMN localizacao_prateleira VARCHAR(60) DEFAULT 'Depósito Central',
    ADD COLUMN data_ultima_atualizacao DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
