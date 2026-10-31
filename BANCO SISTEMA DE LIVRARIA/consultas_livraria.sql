-- ============================================================================
-- PROJETO: SISTEMA DE LIVRARIA
-- SGBD: MySQL 8.0
-- ARQUIVO: consultas_livraria.sql (15 consultas adaptadas ao esquema da livraria)
-- ============================================================================

USE livraria;

-- ============================================================================
-- CATEGORIA 1: CONSULTAS BÁSICAS (5 CONSULTAS)
-- Requisitos: projeção, WHERE, ordenação, LIKE, BETWEEN, IN e NULL
-- ============================================================================

-- ----------------------------------------------------------------------------
-- CONSULTA 1 (Básica com LIKE e ORDER BY)
-- Pergunta de negócio: Quais clientes possuem Silva ou Santos no nome,
-- exibindo contato e situação do cadastro em ordem alfabética?
-- ----------------------------------------------------------------------------
SELECT * FROM cliente;

-- ----------------------------------------------------------------------------
-- CONSULTA 2 (Básica com BETWEEN e ORDER BY DESC)
-- Pergunta de negócio: Quais livros custam entre R$ 30,00 e R$ 100,00,
-- do mais caro para o mais barato?
-- ----------------------------------------------------------------------------
SELECT
    id_livro,
    nome AS livro,
    preco,
    classificacao,
    lancamento
FROM livro
WHERE preco BETWEEN 30.00 AND 100.00
ORDER BY preco DESC, nome ASC;

-- ----------------------------------------------------------------------------
-- CONSULTA 3 (Básica com operador IN)
-- Pergunta de negócio: Quais pedidos estão em andamento ou foram cancelados
-- e podem precisar de acompanhamento da equipe?
-- ----------------------------------------------------------------------------
SELECT
    id_pedido,
    id_cliente,
    id_loja,
    valor_total,
    status,
    data_pedido
FROM pedidos
WHERE status IN ('EM ANDAMENTO', 'CANCELADO')
ORDER BY status, data_pedido DESC;

-- ----------------------------------------------------------------------------
-- CONSULTA 4 (Básica com IS NULL)
-- Pergunta de negócio: Quais pedidos ainda não têm um funcionário responsável?
-- ----------------------------------------------------------------------------
SELECT
    p.id_pedido,
    c.nome AS cliente,
    p.id_loja,
    p.status,
    p.data_pedido
FROM pedidos p
INNER JOIN cliente c ON p.id_cliente = c.id_cliente
WHERE p.id_funcionario IS NULL
ORDER BY p.data_pedido DESC;

-- ----------------------------------------------------------------------------
-- CONSULTA 5 (Básica com filtros múltiplos e projeção formatada)
-- Pergunta de negócio: Quais livros ativos custam até R$ 80,00 e foram
-- lançados a partir de 2020?
-- ----------------------------------------------------------------------------
SELECT
    id_livro,
    nome AS livro,
    CONCAT('R$ ', FORMAT(preco, 2, 'pt_BR')) AS preco_formatado,
    classificacao,
    lancamento
FROM livro
WHERE ativo = TRUE
  AND preco <= 80.00
  AND lancamento >= '2020-01-01'
ORDER BY preco ASC, nome ASC;


-- ============================================================================
-- CATEGORIA 2: JUNÇÕES E AGREGAÇÕES (5 CONSULTAS)
-- Requisitos: junção com 3+ tabelas, LEFT JOIN, GROUP BY e HAVING
-- ============================================================================

-- ----------------------------------------------------------------------------
-- CONSULTA 6 (Junção de cinco tabelas)
-- Pergunta de negócio: Quais livros compõem cada pedido, quem os comprou,
-- em qual loja e em que quantidade?
-- ----------------------------------------------------------------------------
SELECT
    p.id_pedido,
    c.nome AS cliente,
    l.nome AS loja,
    li.nome AS livro,
    ip.quantidade,
    ip.preco_unitario,
    (ip.quantidade * ip.preco_unitario) AS subtotal_item,
    p.status AS status_pedido,
    p.data_pedido
FROM pedidos p
INNER JOIN cliente c ON p.id_cliente = c.id_cliente
INNER JOIN loja l ON p.id_loja = l.id_loja
INNER JOIN item_pedido ip ON p.id_pedido = ip.id_pedido
INNER JOIN livro li ON ip.id_livro = li.id_livro
ORDER BY p.data_pedido DESC, p.id_pedido, li.nome;

-- ----------------------------------------------------------------------------
-- CONSULTA 7 (LEFT JOIN)
-- Pergunta de negócio: Quais clientes cadastrados nunca fizeram um pedido?
-- ----------------------------------------------------------------------------
SELECT
    c.id_cliente,
    c.nome AS cliente,
    c.email,
    c.telefone,
    c.data_cadastro
FROM cliente c
LEFT JOIN pedidos p ON c.id_cliente = p.id_cliente
WHERE p.id_pedido IS NULL
ORDER BY c.nome ASC;

-- ----------------------------------------------------------------------------
-- CONSULTA 8 (GROUP BY com COUNT, SUM e AVG)
-- Pergunta de negócio: Quantos pedidos existem por status, qual o valor total
-- de cada grupo e qual o valor médio dos pedidos?
-- ----------------------------------------------------------------------------
SELECT
    status AS status_pedido,
    COUNT(*) AS total_pedidos,
    SUM(valor_total) AS valor_total_pedidos,
    ROUND(AVG(valor_total), 2) AS ticket_medio
FROM pedidos
GROUP BY status
ORDER BY valor_total_pedidos DESC;

-- ----------------------------------------------------------------------------
-- CONSULTA 9 (GROUP BY com HAVING)
-- Pergunta de negócio: Quais gêneros geraram mais de R$ 500,00 em vendas
-- de pedidos concluídos?
-- ----------------------------------------------------------------------------
SELECT
    g.id_genero,
    g.nome AS genero,
    COUNT(DISTINCT p.id_pedido) AS pedidos_concluidos,
    SUM(ip.quantidade) AS unidades_vendidas,
    SUM(ip.quantidade * ip.preco_unitario) AS faturamento
FROM genero g
INNER JOIN livro li ON g.id_genero = li.id_genero
INNER JOIN item_pedido ip ON li.id_livro = ip.id_livro
INNER JOIN pedidos p ON ip.id_pedido = p.id_pedido
WHERE p.status = 'CONCLUIDO'
GROUP BY g.id_genero, g.nome
HAVING SUM(ip.quantidade * ip.preco_unitario) > 500.00
ORDER BY faturamento DESC;

-- ----------------------------------------------------------------------------
-- CONSULTA 10 (Junção com dados opcionais e COALESCE)
-- Pergunta de negócio: Qual é a visão dos pedidos com loja, responsável,
-- pagamento e situação do pagamento, mesmo quando esses dados não existem?
-- ----------------------------------------------------------------------------
SELECT
    p.id_pedido,
    c.nome AS cliente,
    l.nome AS loja,
    COALESCE(f.nome, 'Funcionário não atribuído') AS funcionario,
    p.valor_total,
    p.status AS status_pedido,
    COALESCE(pg.forma_pagamento, 'SEM PAGAMENTO') AS forma_pagamento,
    COALESCE(pg.status, 'SEM PAGAMENTO') AS status_pagamento,
    pg.data_pagamento
FROM pedidos p
INNER JOIN cliente c ON p.id_cliente = c.id_cliente
INNER JOIN loja l ON p.id_loja = l.id_loja
LEFT JOIN funcionario f ON p.id_funcionario = f.id_funcionario
LEFT JOIN pagamento pg ON p.id_pedido = pg.id_pedido
ORDER BY p.data_pedido DESC, p.id_pedido;


-- ============================================================================
-- CATEGORIA 3: CONSULTAS AVANÇADAS (5 CONSULTAS)
-- Requisitos: subconsulta correlacionada, EXISTS, NOT EXISTS e análise de negócio
-- ============================================================================

-- ----------------------------------------------------------------------------
-- CONSULTA 11 (Subconsulta correlacionada)
-- Pergunta de negócio: Quais livros ativos têm preço acima da média dos livros
-- ativos do mesmo gênero?
-- ----------------------------------------------------------------------------
SELECT
    li.id_livro,
    li.nome AS livro,
    g.nome AS genero,
    li.preco
FROM livro li
INNER JOIN genero g ON li.id_genero = g.id_genero
WHERE li.ativo = TRUE
  AND li.preco > (
      SELECT AVG(li2.preco)
      FROM livro li2
      WHERE li2.id_genero = li.id_genero
        AND li2.ativo = TRUE
  )
ORDER BY g.nome, li.preco DESC;

-- ----------------------------------------------------------------------------
-- CONSULTA 12 (Subconsulta com EXISTS)
-- Pergunta de negócio: Quais clientes têm ao menos um pedido concluído com
-- pagamento aprovado?
-- ----------------------------------------------------------------------------
SELECT
    c.id_cliente,
    c.nome AS cliente,
    c.cpf,
    c.email
FROM cliente c
WHERE EXISTS (
    SELECT 1
    FROM pedidos p
    INNER JOIN pagamento pg ON p.id_pedido = pg.id_pedido
    WHERE p.id_cliente = c.id_cliente
      AND p.status = 'CONCLUIDO'
      AND pg.status = 'APROVADO'
)
ORDER BY c.nome;

-- ----------------------------------------------------------------------------
-- CONSULTA 13 (Subconsulta com NOT EXISTS)
-- Pergunta de negócio: Quais livros ativos nunca foram vendidos em um pedido
-- concluído e podem precisar de divulgação ou revisão de estoque?
-- ----------------------------------------------------------------------------
SELECT
    li.id_livro,
    li.nome AS livro,
    g.nome AS genero,
    li.preco,
    li.lancamento
FROM livro li
INNER JOIN genero g ON li.id_genero = g.id_genero
WHERE li.ativo = TRUE
  AND NOT EXISTS (
      SELECT 1
      FROM item_pedido ip
      INNER JOIN pedidos p ON ip.id_pedido = p.id_pedido
      WHERE ip.id_livro = li.id_livro
        AND p.status = 'CONCLUIDO'
  )
ORDER BY li.nome;

-- ----------------------------------------------------------------------------
-- CONSULTA 14 (Subconsultas escalares para participação no faturamento)
-- Pergunta de negócio: Qual percentual do faturamento concluído total pertence
-- a cada loja?
-- ----------------------------------------------------------------------------
SELECT
    l.id_loja,
    l.nome AS loja,
    COALESCE((
        SELECT SUM(p.valor_total)
        FROM pedidos p
        WHERE p.id_loja = l.id_loja
          AND p.status = 'CONCLUIDO'
    ), 0.00) AS faturamento_concluido,
    ROUND(
        COALESCE((
            SELECT SUM(p.valor_total)
            FROM pedidos p
            WHERE p.id_loja = l.id_loja
              AND p.status = 'CONCLUIDO'
        ), 0.00)
        / NULLIF((
            SELECT SUM(p2.valor_total)
            FROM pedidos p2
            WHERE p2.status = 'CONCLUIDO'
        ), 0.00) * 100,
        2
    ) AS percentual_faturamento
FROM loja l
ORDER BY percentual_faturamento DESC, l.nome;

-- ----------------------------------------------------------------------------
-- CONSULTA 15 (Análise de relacionamento e desempenho por cliente)
-- Pergunta de negócio: Quais clientes mais compraram, quanto geraram em pedidos
-- concluídos e quantos pedidos têm em andamento ou foram cancelados?
-- ----------------------------------------------------------------------------
WITH resumo_pedidos AS (
    SELECT
        id_cliente,
        COUNT(*) AS total_pedidos,
        SUM(CASE WHEN status = 'CONCLUIDO' THEN valor_total ELSE 0 END) AS total_concluido,
        SUM(CASE WHEN status = 'EM ANDAMENTO' THEN 1 ELSE 0 END) AS pedidos_em_andamento,
        SUM(CASE WHEN status = 'CANCELADO' THEN 1 ELSE 0 END) AS pedidos_cancelados,
        ROUND(AVG(CASE WHEN status = 'CONCLUIDO' THEN valor_total END), 2) AS ticket_medio_concluido
    FROM pedidos
    GROUP BY id_cliente
)
SELECT
    c.id_cliente,
    c.nome AS cliente,
    c.ativo AS cadastro_ativo,
    COALESCE(r.total_pedidos, 0) AS total_pedidos,
    COALESCE(r.total_concluido, 0.00) AS faturamento_concluido,
    COALESCE(r.pedidos_em_andamento, 0) AS pedidos_em_andamento,
    COALESCE(r.pedidos_cancelados, 0) AS pedidos_cancelados,
    COALESCE(r.ticket_medio_concluido, 0.00) AS ticket_medio_concluido
FROM cliente c
LEFT JOIN resumo_pedidos r ON c.id_cliente = r.id_cliente
ORDER BY faturamento_concluido DESC, total_pedidos DESC, c.nome;
