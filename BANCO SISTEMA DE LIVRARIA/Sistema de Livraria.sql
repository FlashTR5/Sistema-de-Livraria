CREATE DATABASE IF NOT EXISTS livraria
DEFAULT CHARACTER SET utf8
DEFAULT COLLATE utf8_general_ci;
USE livraria;


CREATE TABLE cliente (
	id_cliente INT NOT NULL AUTO_INCREMENT,
	nome VARCHAR(120) NOT NULL,
	cpf VARCHAR(14) UNIQUE,
	email VARCHAR(300) NOT NULL,
	nasc DATE,
	telefone VARCHAR(20),
	endereco JSON,
	ativo BOOLEAN DEFAULT TRUE,
	sexo ENUM('M', 'F'),
	nacionalidade VARCHAR(20) DEFAULT 'Brasil',
	data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id_cliente)
) DEFAULT CHARSET = utf8;


CREATE TABLE loja (
	id_loja INT NOT NULL AUTO_INCREMENT,
	nome VARCHAR(120) NOT NULL,
	cnpj VARCHAR(14) UNIQUE,
	email VARCHAR(300),
	telefone VARCHAR(20),
	endereco JSON,
	ativo BOOLEAN DEFAULT TRUE,
	data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id_loja)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE autor (
	id_autor INT NOT NULL AUTO_INCREMENT,
	nome VARCHAR(120) NOT NULL,
	email VARCHAR(300) NOT NULL,
	nasc DATE,
	ativo BOOLEAN DEFAULT TRUE,
	data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id_autor)
) DEFAULT CHARSET = utf8;

CREATE TABLE genero (
	id_genero INT NOT NULL AUTO_INCREMENT,
	nome VARCHAR(120) NOT NULL,
	ativo BOOLEAN DEFAULT TRUE,
	data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id_genero)
) DEFAULT CHARSET = utf8;

CREATE TABLE editora (
	id_editora INT NOT NULL AUTO_INCREMENT,
	nome VARCHAR(120) NOT NULL,
	cnpj VARCHAR(14) UNIQUE,
	email VARCHAR(300),
	telefone VARCHAR(20),
	endereco JSON,
	ativo BOOLEAN DEFAULT TRUE,
	data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id_editora)
) DEFAULT CHARSET = utf8;

CREATE TABLE livro (
	id_livro INT NOT NULL AUTO_INCREMENT,
	id_autor INT NOT NULL,
	id_editora INT NOT NULL,
	id_genero INT NOT NULL,
	codigo_barras VARCHAR(30) UNIQUE,
	isbn VARCHAR(13) UNIQUE,
	nome VARCHAR(120) NOT NULL,
	preco DECIMAL(10, 2) NOT NULL,
	classificacao VARCHAR(20),
	lancamento DATE,
	data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
	ativo BOOLEAN DEFAULT TRUE,
	PRIMARY KEY (id_livro),
	CONSTRAINT fk_livro_genero FOREIGN KEY (id_genero) REFERENCES genero(id_genero),
	CONSTRAINT fk_livro_autor FOREIGN KEY (id_autor) REFERENCES autor(id_autor),
	CONSTRAINT fk_livro_editora FOREIGN KEY (id_editora) REFERENCES editora(id_editora)
) DEFAULT CHARSET = utf8;

CREATE TABLE funcionario (
	id_funcionario INT NOT NULL AUTO_INCREMENT,
	id_loja INT NOT NULL,
	nome VARCHAR(120) NOT NULL,
	cpf VARCHAR(14) UNIQUE,
	email VARCHAR(300) NOT NULL,
	nasc DATE,
	telefone VARCHAR(20),
	endereco JSON,
	ativo BOOLEAN DEFAULT TRUE,
	sexo ENUM('M', 'F'),
	nacionalidade VARCHAR(20) DEFAULT 'Brasil',
	data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id_funcionario),
	CONSTRAINT fk_funcionario_loja FOREIGN KEY (id_loja) REFERENCES loja(id_loja)
) DEFAULT CHARSET = utf8;

CREATE TABLE estoque (
	id_estoque INT NOT NULL AUTO_INCREMENT,
	id_loja INT NOT NULL,
	id_livro INT NOT NULL,
	quantidade INT NOT NULL DEFAULT 0,
	data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id_estoque),
	CONSTRAINT fk_estoque_loja FOREIGN KEY (id_loja) REFERENCES loja(id_loja),
	CONSTRAINT fk_estoque_livro FOREIGN KEY (id_livro) REFERENCES livro(id_livro)
) DEFAULT CHARSET = utf8;

CREATE TABLE pedidos (
	id_pedido INT NOT NULL AUTO_INCREMENT,
	id_cliente INT NOT NULL,
	id_loja INT NOT NULL,
	id_funcionario INT,
	valor_total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
	status ENUM('EM ANDAMENTO', 'CONCLUIDO', 'CANCELADO') DEFAULT 'EM ANDAMENTO',
	data_pedido DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id_pedido),
	CONSTRAINT fk_pedidos_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
	CONSTRAINT fk_pedidos_loja FOREIGN KEY (id_loja) REFERENCES loja(id_loja),
	CONSTRAINT fk_pedidos_funcionario FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario)
) DEFAULT CHARSET = utf8;

CREATE TABLE item_pedido (
	id_item INT NOT NULL AUTO_INCREMENT,
	id_pedido INT NOT NULL,
	id_livro INT NOT NULL,
	quantidade INT NOT NULL DEFAULT 1,
	preco_unitario DECIMAL(10, 2) NOT NULL,
	PRIMARY KEY (id_item),
	CONSTRAINT fk_item_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
	CONSTRAINT fk_item_livro FOREIGN KEY (id_livro) REFERENCES livro(id_livro)
) DEFAULT CHARSET = utf8;

CREATE TABLE pagamento (
	id_pagamento INT NOT NULL AUTO_INCREMENT,
	id_pedido INT NOT NULL,
	forma_pagamento ENUM('CARTAO_CREDITO', 'CARTAO_DEBITO', 'PIX', 'DINHEIRO', 'BOLETO') NOT NULL,
	valor DECIMAL(10, 2) NOT NULL,
	status ENUM('PENDENTE', 'APROVADO', 'RECUSADO', 'ESTORNADO') DEFAULT 'PENDENTE',
	data_pagamento DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id_pagamento),
	CONSTRAINT fk_pagamento_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
) DEFAULT CHARSET = utf8;




-- DROP database livraria; -- Desativado para preservar a base apos criar as tabelas.



