-- Script de compatibilidade entre modelo relacional de Livros / Vendas / Gênero e os CRUDs
-- Garante view 'categoria' mapeada para 'genero', view 'itens_pedido' para 'item_pedido'
-- e colunas 'titulo', 'ano_publicacao', 'id_categoria' em 'livro'.

USE livraria;

-- 1. View para compatibilidade com o CRUD de Livros
CREATE OR REPLACE VIEW categoria AS 
SELECT id_genero AS id_categoria, nome, ativo, data_cadastro 
FROM genero;

-- 2. View para compatibilidade caso consultas chamem 'itens_pedido' no plural
CREATE OR REPLACE VIEW itens_pedido AS 
SELECT * FROM item_pedido;

-- 3. Adiciona colunas sinônimas em 'livro' se não existirem
ALTER TABLE livro ADD COLUMN IF NOT EXISTS titulo VARCHAR(120) NULL;
UPDATE livro SET titulo = COALESCE(titulo, nome);

ALTER TABLE livro ADD COLUMN IF NOT EXISTS ano_publicacao INT NULL;
UPDATE livro SET ano_publicacao = YEAR(lancamento) WHERE ano_publicacao IS NULL AND lancamento IS NOT NULL;

ALTER TABLE livro ADD COLUMN IF NOT EXISTS id_categoria INT NULL;
UPDATE livro SET id_categoria = id_genero WHERE id_categoria IS NULL;

-- 4. Permite defaults nos campos que o formulário de Livro não preenche diretamente
ALTER TABLE livro MODIFY COLUMN id_autor INT NULL DEFAULT 1;
ALTER TABLE livro MODIFY COLUMN id_genero INT NULL;
ALTER TABLE livro MODIFY COLUMN nome VARCHAR(120) NULL;
UPDATE livro SET nome = titulo WHERE (nome IS NULL OR nome = '') AND titulo IS NOT NULL;
