USE livraria;

-- Script consolidado de migração e alinhamento do banco MySQL
-- Execute na sua base de dados 'livraria' para garantir todas as colunas necessárias.

-- 1. Tabela FUNCIONARIO
-- Adiciona cargo e data_admissao caso ainda não existam e flexibiliza email
ALTER TABLE funcionario ADD COLUMN IF NOT EXISTS cargo VARCHAR(40) NOT NULL DEFAULT 'Atendente';
ALTER TABLE funcionario ADD COLUMN IF NOT EXISTS data_admissao DATE NULL;
ALTER TABLE funcionario MODIFY COLUMN email VARCHAR(300) NULL;

-- 2. Tabela AUTOR
-- Flexibiliza email para permitir cadastro simplificado
ALTER TABLE autor MODIFY COLUMN email VARCHAR(300) NULL;

-- 3. Tabela LIVRO
-- Garante colunas de catálogo, código de barras e data de lançamento
ALTER TABLE livro ADD COLUMN IF NOT EXISTS codigo_barras VARCHAR(30) UNIQUE;
ALTER TABLE livro ADD COLUMN IF NOT EXISTS classificacao VARCHAR(20);
ALTER TABLE livro ADD COLUMN IF NOT EXISTS lancamento DATE;
ALTER TABLE livro ADD COLUMN IF NOT EXISTS ativo BOOLEAN DEFAULT TRUE;

-- 4. Tabela CLIENTE
ALTER TABLE cliente ADD COLUMN IF NOT EXISTS nacionalidade VARCHAR(20) DEFAULT 'Brasil';
ALTER TABLE cliente ADD COLUMN IF NOT EXISTS ativo BOOLEAN DEFAULT TRUE;
