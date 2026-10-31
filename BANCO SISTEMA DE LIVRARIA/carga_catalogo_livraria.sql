-- Carga ampliada do catálogo da livraria: 1 loja, 15 gêneros, 5 editoras, 300 autores, 500 livros e 500 estoques.
-- Títulos, autores, editoras, ISBNs e anos vêm do Open Library; 10 edições também foram conferidas em catálogos oficiais.
-- Preços, estoque, contatos, CPFs e CNPJs são dados de demonstração; os documentos são fictícios.
-- Editoras consultadas: https://openlibrary.org/search?publisher=Rocco | Companhia das Letras | Zahar | Editora 34 | Sextante
-- Referências oficiais das 10 edições iniciais: https://rocco.com.br/produto/a-hora-da-estrela-edicao-comemorativa/ ; https://rocco.com.br/produto/harry-potter-e-a-pedra-filosofal/ ; https://www.companhiadasletras.com.br/livro/9788535933390/ ; https://www.companhiadasletras.com.br/livro/9788535911695/capitaes-da-areia
USE livraria;
SET NAMES utf8;
START TRANSACTION;

-- Retira apenas as duas filiais e os funcionários/estoques criados pela versão anterior desta carga, se não tiverem outros vínculos.
DELETE FROM estoque WHERE id_loja IN (SELECT id_loja FROM loja WHERE nome IN ('Livraria Estacao das Letras', 'Livraria Horizonte')) AND id_livro IN (SELECT id_livro FROM livro WHERE isbn IN ('9786555320350', '9786555323948', '9786555322132', '9786555326758', '9788532531735', '9788532511010', '9788532531780', '9788535933390', '9788535939378', '9788535911695'));
DELETE FROM funcionario WHERE cpf IN ('000.000.000-13', '000.000.000-14', '000.000.000-15', '000.000.000-16') AND id_loja IN (SELECT id_loja FROM loja WHERE nome IN ('Livraria Estacao das Letras', 'Livraria Horizonte')) AND NOT EXISTS (SELECT 1 FROM pedidos p WHERE p.id_funcionario = funcionario.id_funcionario);
DELETE FROM loja WHERE nome IN ('Livraria Estacao das Letras', 'Livraria Horizonte') AND NOT EXISTS (SELECT 1 FROM pedidos p WHERE p.id_loja = loja.id_loja) AND NOT EXISTS (SELECT 1 FROM estoque e WHERE e.id_loja = loja.id_loja) AND NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.id_loja = loja.id_loja);

-- Uma única loja física usada pelos funcionários e pelo estoque.
INSERT INTO loja (nome, cnpj, email, telefone, endereco, ativo) SELECT 'Livraria de Demonstracao - Carga', '00000000000100', 'loja.carga@gmail.com', '+55 (11) 3222-0100', JSON_OBJECT('logradouro', 'Rua Augusta', 'numero', 1500, 'bairro', 'Consolacao', 'cidade', 'Sao Paulo', 'estado', 'SP', 'cep', '01304-001'), TRUE FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM loja WHERE nome = 'Livraria de Demonstracao - Carga');
UPDATE loja SET cnpj = COALESCE(cnpj, '00000000000100'), telefone = '+55 (11) 3222-0100', endereco = JSON_OBJECT('logradouro', 'Rua Augusta', 'numero', 1500, 'bairro', 'Consolacao', 'cidade', 'Sao Paulo', 'estado', 'SP', 'cep', '01304-001'), ativo = TRUE WHERE nome = 'Livraria de Demonstracao - Carga' AND email = 'loja.carga@gmail.com';

-- 15 gêneros.
INSERT INTO genero (nome, ativo)
SELECT d.nome, TRUE FROM (
SELECT 'Romance' AS nome
UNION ALL SELECT 'Ficcao Brasileira'
UNION ALL SELECT 'Contos'
UNION ALL SELECT 'Fantasia'
UNION ALL SELECT 'Literatura Infantojuvenil'
UNION ALL SELECT 'Ficcao Cientifica'
UNION ALL SELECT 'Suspense e Misterio'
UNION ALL SELECT 'Terror'
UNION ALL SELECT 'Poesia'
UNION ALL SELECT 'Biografia e Memorias'
UNION ALL SELECT 'Historia'
UNION ALL SELECT 'Autoajuda'
UNION ALL SELECT 'Negocios e Economia'
UNION ALL SELECT 'Literatura Religiosa'
UNION ALL SELECT 'Ficcao Geral'
) AS d WHERE NOT EXISTS (SELECT 1 FROM genero g WHERE g.nome = d.nome);

-- 5 editoras reais; CNPJs e contatos abaixo são fictícios.
INSERT INTO editora (nome, cnpj, email, telefone, ativo)
SELECT d.nome, d.cnpj, d.email, d.telefone, TRUE FROM (
SELECT 'Editora Rocco' AS nome, '00000000000301' AS cnpj, 'cadastro.editora.rocco@gmail.com' AS email, '+55 (21) 3525-2000' AS telefone
UNION ALL SELECT 'Companhia das Letras' AS nome, '00000000000302' AS cnpj, 'cadastro.editora.companhia@gmail.com' AS email, '+55 (11) 3707-3500' AS telefone
UNION ALL SELECT 'Zahar' AS nome, '00000000000303' AS cnpj, 'cadastro.editora.zahar@gmail.com' AS email, '+55 (21) 2537-4100' AS telefone
UNION ALL SELECT 'Editora 34' AS nome, '00000000000304' AS cnpj, 'cadastro.editora.34@gmail.com' AS email, '+55 (11) 3111-0500' AS telefone
UNION ALL SELECT 'Sextante' AS nome, '00000000000305' AS cnpj, 'cadastro.editora.sextante@gmail.com' AS email, '+55 (21) 2538-4100' AS telefone
) AS d WHERE NOT EXISTS (SELECT 1 FROM editora e WHERE e.nome = d.nome);

-- 300 autores reais. Os e-mails são endereços fictícios de cadastro, não contatos pessoais.
INSERT INTO autor (nome, email, ativo)
SELECT d.nome, d.email, TRUE FROM (
SELECT 'Clarice Lispector' AS nome, 'cadastro.autor.001@gmail.com' AS email
UNION ALL SELECT 'J.K. Rowling' AS nome, 'cadastro.autor.002@gmail.com' AS email
UNION ALL SELECT 'Jeferson Tenório' AS nome, 'cadastro.autor.003@gmail.com' AS email
UNION ALL SELECT 'Jorge Amado' AS nome, 'cadastro.autor.004@gmail.com' AS email
UNION ALL SELECT 'Paulo Coelho' AS nome, 'cadastro.autor.005@gmail.com' AS email
UNION ALL SELECT 'Roberto da Matta' AS nome, 'cadastro.autor.006@gmail.com' AS email
UNION ALL SELECT 'Lygia Fagundes Telles' AS nome, 'cadastro.autor.007@gmail.com' AS email
UNION ALL SELECT 'Neil Gaiman' AS nome, 'cadastro.autor.008@gmail.com' AS email
UNION ALL SELECT 'Thalita Reboucas' AS nome, 'cadastro.autor.009@gmail.com' AS email
UNION ALL SELECT 'Katherine Applegate' AS nome, 'cadastro.autor.010@gmail.com' AS email
UNION ALL SELECT 'John Grisham' AS nome, 'cadastro.autor.011@gmail.com' AS email
UNION ALL SELECT 'Suzanne Collins' AS nome, 'cadastro.autor.012@gmail.com' AS email
UNION ALL SELECT 'Joel Rufino dos Santos' AS nome, 'cadastro.autor.013@gmail.com' AS email
UNION ALL SELECT 'Silviano Santiago' AS nome, 'cadastro.autor.014@gmail.com' AS email
UNION ALL SELECT 'Betto Frei' AS nome, 'cadastro.autor.015@gmail.com' AS email
UNION ALL SELECT 'Anne Rice' AS nome, 'cadastro.autor.016@gmail.com' AS email
UNION ALL SELECT 'Braulio Tavares' AS nome, 'cadastro.autor.017@gmail.com' AS email
UNION ALL SELECT 'Waly Salomão' AS nome, 'cadastro.autor.018@gmail.com' AS email
UNION ALL SELECT 'Deepak Chopra' AS nome, 'cadastro.autor.019@gmail.com' AS email
UNION ALL SELECT 'Christopher Paolini' AS nome, 'cadastro.autor.020@gmail.com' AS email
UNION ALL SELECT 'João Batista Melo' AS nome, 'cadastro.autor.021@gmail.com' AS email
UNION ALL SELECT 'Wanderley Guilherme dos Santos' AS nome, 'cadastro.autor.022@gmail.com' AS email
UNION ALL SELECT 'Ruy Tapioca' AS nome, 'cadastro.autor.023@gmail.com' AS email
UNION ALL SELECT 'Bernardo Ajzenberg' AS nome, 'cadastro.autor.024@gmail.com' AS email
UNION ALL SELECT 'Flávio Carneiro' AS nome, 'cadastro.autor.025@gmail.com' AS email
UNION ALL SELECT 'Gore Vidal' AS nome, 'cadastro.autor.026@gmail.com' AS email
UNION ALL SELECT 'Adriana Lisboa' AS nome, 'cadastro.autor.027@gmail.com' AS email
UNION ALL SELECT 'Marina Colasanti' AS nome, 'cadastro.autor.028@gmail.com' AS email
UNION ALL SELECT 'Clarissa Pinkola Estés' AS nome, 'cadastro.autor.029@gmail.com' AS email
UNION ALL SELECT 'Jonathan Safran Foer' AS nome, 'cadastro.autor.030@gmail.com' AS email
UNION ALL SELECT 'Austin Kleon' AS nome, 'cadastro.autor.031@gmail.com' AS email
UNION ALL SELECT 'Nilton Bonder' AS nome, 'cadastro.autor.032@gmail.com' AS email
UNION ALL SELECT 'José Saramago' AS nome, 'cadastro.autor.033@gmail.com' AS email
UNION ALL SELECT 'Drauzio Varella' AS nome, 'cadastro.autor.034@gmail.com' AS email
UNION ALL SELECT 'Sigmund Freud' AS nome, 'cadastro.autor.035@gmail.com' AS email
UNION ALL SELECT 'Rubem Fonseca' AS nome, 'cadastro.autor.036@gmail.com' AS email
UNION ALL SELECT 'Carl Sagan' AS nome, 'cadastro.autor.037@gmail.com' AS email
UNION ALL SELECT 'Lilia Moritz Schwarcz' AS nome, 'cadastro.autor.038@gmail.com' AS email
UNION ALL SELECT 'Erico Veríssimo' AS nome, 'cadastro.autor.039@gmail.com' AS email
UNION ALL SELECT 'Jô Soares' AS nome, 'cadastro.autor.040@gmail.com' AS email
UNION ALL SELECT 'João José Reis' AS nome, 'cadastro.autor.041@gmail.com' AS email
UNION ALL SELECT 'Yuval Noah Harari' AS nome, 'cadastro.autor.042@gmail.com' AS email
UNION ALL SELECT 'Ailton Krenak' AS nome, 'cadastro.autor.043@gmail.com' AS email
UNION ALL SELECT 'Fernando Morais' AS nome, 'cadastro.autor.044@gmail.com' AS email
UNION ALL SELECT 'Afonso Cruz' AS nome, 'cadastro.autor.045@gmail.com' AS email
UNION ALL SELECT 'Carlos Drummond de Andrade' AS nome, 'cadastro.autor.046@gmail.com' AS email
UNION ALL SELECT 'Ana Maria Miranda' AS nome, 'cadastro.autor.047@gmail.com' AS email
UNION ALL SELECT 'Mia Couto' AS nome, 'cadastro.autor.048@gmail.com' AS email
UNION ALL SELECT 'Thomas Mann' AS nome, 'cadastro.autor.049@gmail.com' AS email
UNION ALL SELECT 'Moacyr Scliar' AS nome, 'cadastro.autor.050@gmail.com' AS email
UNION ALL SELECT 'Philip Roth' AS nome, 'cadastro.autor.051@gmail.com' AS email
UNION ALL SELECT 'Zuenir Ventura' AS nome, 'cadastro.autor.052@gmail.com' AS email
UNION ALL SELECT 'Raphael Montes' AS nome, 'cadastro.autor.053@gmail.com' AS email
UNION ALL SELECT 'Patti Smith' AS nome, 'cadastro.autor.054@gmail.com' AS email
UNION ALL SELECT 'Sérgio Sant''Anna' AS nome, 'cadastro.autor.055@gmail.com' AS email
UNION ALL SELECT 'Oliver Sacks' AS nome, 'cadastro.autor.056@gmail.com' AS email
UNION ALL SELECT 'Carlos Heitor Cony' AS nome, 'cadastro.autor.057@gmail.com' AS email
UNION ALL SELECT 'David Graeber' AS nome, 'cadastro.autor.058@gmail.com' AS email
UNION ALL SELECT 'Zygmunt Bauman' AS nome, 'cadastro.autor.059@gmail.com' AS email
UNION ALL SELECT 'Flávia Lins e Silva' AS nome, 'cadastro.autor.060@gmail.com' AS email
UNION ALL SELECT 'Jonathan C. Young' AS nome, 'cadastro.autor.061@gmail.com' AS email
UNION ALL SELECT 'Arthur Conan Doyle' AS nome, 'cadastro.autor.062@gmail.com' AS email
UNION ALL SELECT 'Ian Stewart' AS nome, 'cadastro.autor.063@gmail.com' AS email
UNION ALL SELECT 'Danilo Marcondes' AS nome, 'cadastro.autor.064@gmail.com' AS email
UNION ALL SELECT 'Leonard Mlodinow' AS nome, 'cadastro.autor.065@gmail.com' AS email
UNION ALL SELECT 'John Lukacs' AS nome, 'cadastro.autor.066@gmail.com' AS email
UNION ALL SELECT 'Gilberto Velho' AS nome, 'cadastro.autor.067@gmail.com' AS email
UNION ALL SELECT 'Slavoj Žižek' AS nome, 'cadastro.autor.068@gmail.com' AS email
UNION ALL SELECT 'Jacques Lacan' AS nome, 'cadastro.autor.069@gmail.com' AS email
UNION ALL SELECT 'Stefan Zweig' AS nome, 'cadastro.autor.070@gmail.com' AS email
UNION ALL SELECT 'Will Gompertz' AS nome, 'cadastro.autor.071@gmail.com' AS email
UNION ALL SELECT 'Sonia Sant''Anna' AS nome, 'cadastro.autor.072@gmail.com' AS email
UNION ALL SELECT 'Sófocles' AS nome, 'cadastro.autor.073@gmail.com' AS email
UNION ALL SELECT 'Paul Strathern' AS nome, 'cadastro.autor.074@gmail.com' AS email
UNION ALL SELECT 'Reza Aslan' AS nome, 'cadastro.autor.075@gmail.com' AS email
UNION ALL SELECT 'Anthony Giddens' AS nome, 'cadastro.autor.076@gmail.com' AS email
UNION ALL SELECT 'Rudyard Kipling' AS nome, 'cadastro.autor.077@gmail.com' AS email
UNION ALL SELECT 'Victor Hugo' AS nome, 'cadastro.autor.078@gmail.com' AS email
UNION ALL SELECT 'Projeto Memória das Organizações Globo (Brazil)' AS nome, 'cadastro.autor.079@gmail.com' AS email
UNION ALL SELECT 'Celso Castro' AS nome, 'cadastro.autor.080@gmail.com' AS email
UNION ALL SELECT 'Juan David Nasio' AS nome, 'cadastro.autor.081@gmail.com' AS email
UNION ALL SELECT 'H. G. Wells' AS nome, 'cadastro.autor.082@gmail.com' AS email
UNION ALL SELECT 'Anthony Browne' AS nome, 'cadastro.autor.083@gmail.com' AS email
UNION ALL SELECT 'Charles J. Wheelan' AS nome, 'cadastro.autor.084@gmail.com' AS email
UNION ALL SELECT 'Sam Kean' AS nome, 'cadastro.autor.085@gmail.com' AS email
UNION ALL SELECT 'Elisabeth Roudinesco' AS nome, 'cadastro.autor.086@gmail.com' AS email
UNION ALL SELECT 'L. A. García-Roza' AS nome, 'cadastro.autor.087@gmail.com' AS email
UNION ALL SELECT 'José Ramos Tinhorão' AS nome, 'cadastro.autor.088@gmail.com' AS email
UNION ALL SELECT 'Marcelo Mirisola' AS nome, 'cadastro.autor.089@gmail.com' AS email
UNION ALL SELECT 'Luiz Carlos Bresser Pereira' AS nome, 'cadastro.autor.090@gmail.com' AS email
UNION ALL SELECT 'Beatriz Bracher' AS nome, 'cadastro.autor.091@gmail.com' AS email
UNION ALL SELECT 'Лев Толстой' AS nome, 'cadastro.autor.092@gmail.com' AS email
UNION ALL SELECT 'Alberto Alexandre Martins' AS nome, 'cadastro.autor.093@gmail.com' AS email
UNION ALL SELECT 'Lúcio Kowarick' AS nome, 'cadastro.autor.094@gmail.com' AS email
UNION ALL SELECT 'Evandro Ferreira' AS nome, 'cadastro.autor.095@gmail.com' AS email
UNION ALL SELECT 'Antonio Risério' AS nome, 'cadastro.autor.096@gmail.com' AS email
UNION ALL SELECT 'Aracy A. Amaral' AS nome, 'cadastro.autor.097@gmail.com' AS email
UNION ALL SELECT 'Jairo Severiano' AS nome, 'cadastro.autor.098@gmail.com' AS email
UNION ALL SELECT 'Zuza Homem de Mello' AS nome, 'cadastro.autor.099@gmail.com' AS email
UNION ALL SELECT 'Gilles Deleuze' AS nome, 'cadastro.autor.100@gmail.com' AS email
UNION ALL SELECT 'Alfredo Bosi' AS nome, 'cadastro.autor.101@gmail.com' AS email
UNION ALL SELECT 'Isaias Pessotti' AS nome, 'cadastro.autor.102@gmail.com' AS email
UNION ALL SELECT 'Nelson Ascher' AS nome, 'cadastro.autor.103@gmail.com' AS email
UNION ALL SELECT 'Vários Autores' AS nome, 'cadastro.autor.104@gmail.com' AS email
UNION ALL SELECT 'Antonio Arnoni Prado' AS nome, 'cadastro.autor.105@gmail.com' AS email
UNION ALL SELECT 'Maria Cecília Gomes dos Reis' AS nome, 'cadastro.autor.106@gmail.com' AS email
UNION ALL SELECT 'NIKOLAI LESKOV' AS nome, 'cadastro.autor.107@gmail.com' AS email
UNION ALL SELECT 'Jorge Caldeira' AS nome, 'cadastro.autor.108@gmail.com' AS email
UNION ALL SELECT 'Fabio Weintraub' AS nome, 'cadastro.autor.109@gmail.com' AS email
UNION ALL SELECT 'Fabrício Corsaletti' AS nome, 'cadastro.autor.110@gmail.com' AS email
UNION ALL SELECT 'Dan Greenburg' AS nome, 'cadastro.autor.111@gmail.com' AS email
UNION ALL SELECT 'Edimilson de Almeida Pereira' AS nome, 'cadastro.autor.112@gmail.com' AS email
UNION ALL SELECT 'Richard G. Parker' AS nome, 'cadastro.autor.113@gmail.com' AS email
UNION ALL SELECT 'Régis R. Bonvicino' AS nome, 'cadastro.autor.114@gmail.com' AS email
UNION ALL SELECT 'Augusto Cury' AS nome, 'cadastro.autor.115@gmail.com' AS email
UNION ALL SELECT 'Leonardo Boff' AS nome, 'cadastro.autor.116@gmail.com' AS email
UNION ALL SELECT 'Brian Weiss' AS nome, 'cadastro.autor.117@gmail.com' AS email
UNION ALL SELECT 'Bradley Trevor Greive' AS nome, 'cadastro.autor.118@gmail.com' AS email
UNION ALL SELECT 'Rufus Butler Seder' AS nome, 'cadastro.autor.119@gmail.com' AS email
UNION ALL SELECT 'José Duarte' AS nome, 'cadastro.autor.120@gmail.com' AS email
UNION ALL SELECT 'Mosab Hassan Yousef' AS nome, 'cadastro.autor.121@gmail.com' AS email
UNION ALL SELECT 'Gustavo Cerbasi' AS nome, 'cadastro.autor.122@gmail.com' AS email
UNION ALL SELECT 'Hugh Prather' AS nome, 'cadastro.autor.123@gmail.com' AS email
UNION ALL SELECT 'Pedro N. Miranda M.' AS nome, 'cadastro.autor.124@gmail.com' AS email
UNION ALL SELECT 'David Goggins' AS nome, 'cadastro.autor.125@gmail.com' AS email
UNION ALL SELECT 'Lincoln Peirce' AS nome, 'cadastro.autor.126@gmail.com' AS email
UNION ALL SELECT 'Ana Claudia Quintana Arantes' AS nome, 'cadastro.autor.127@gmail.com' AS email
UNION ALL SELECT 'Brené Brown' AS nome, 'cadastro.autor.128@gmail.com' AS email
UNION ALL SELECT 'Rhonda Byrne' AS nome, 'cadastro.autor.129@gmail.com' AS email
UNION ALL SELECT 'Bertrand Russell' AS nome, 'cadastro.autor.130@gmail.com' AS email
UNION ALL SELECT 'Ken Follett' AS nome, 'cadastro.autor.131@gmail.com' AS email
UNION ALL SELECT 'Hannah Luce / Robin Gaby Fisher' AS nome, 'cadastro.autor.132@gmail.com' AS email
UNION ALL SELECT 'Johanna Basford' AS nome, 'cadastro.autor.133@gmail.com' AS email
UNION ALL SELECT 'Margot Morrell' AS nome, 'cadastro.autor.134@gmail.com' AS email
UNION ALL SELECT 'Rose Marie Muraro' AS nome, 'cadastro.autor.135@gmail.com' AS email
UNION ALL SELECT 'António Vasconcelos Raposo' AS nome, 'cadastro.autor.136@gmail.com' AS email
UNION ALL SELECT 'Bhagwan Rajneesh' AS nome, 'cadastro.autor.137@gmail.com' AS email
UNION ALL SELECT 'Marie Kondo' AS nome, 'cadastro.autor.138@gmail.com' AS email
UNION ALL SELECT 'Ernie J. Zelinski' AS nome, 'cadastro.autor.139@gmail.com' AS email
UNION ALL SELECT 'Affonso Romano de Sant''Anna' AS nome, 'cadastro.autor.140@gmail.com' AS email
UNION ALL SELECT 'Aline Bei' AS nome, 'cadastro.autor.141@gmail.com' AS email
UNION ALL SELECT 'Amyr Klink' AS nome, 'cadastro.autor.142@gmail.com' AS email
UNION ALL SELECT 'Andre Barcinski' AS nome, 'cadastro.autor.143@gmail.com' AS email
UNION ALL SELECT 'Carlos Calado' AS nome, 'cadastro.autor.144@gmail.com' AS email
UNION ALL SELECT 'Carlos Eduardo de Magalhães' AS nome, 'cadastro.autor.145@gmail.com' AS email
UNION ALL SELECT 'Celso Furtado' AS nome, 'cadastro.autor.146@gmail.com' AS email
UNION ALL SELECT 'Chico Buarque' AS nome, 'cadastro.autor.147@gmail.com' AS email
UNION ALL SELECT 'Chimamanda Ngozi Adichie' AS nome, 'cadastro.autor.148@gmail.com' AS email
UNION ALL SELECT 'Cristovão Tezza' AS nome, 'cadastro.autor.149@gmail.com' AS email
UNION ALL SELECT 'Darcy Ribeiro' AS nome, 'cadastro.autor.150@gmail.com' AS email
UNION ALL SELECT 'Dreyfus, Dominique.' AS nome, 'cadastro.autor.151@gmail.com' AS email
UNION ALL SELECT 'Eric Hobsbawm' AS nome, 'cadastro.autor.152@gmail.com' AS email
UNION ALL SELECT 'Fernanda Torres' AS nome, 'cadastro.autor.153@gmail.com' AS email
UNION ALL SELECT 'Flávio dos Santos Gomes' AS nome, 'cadastro.autor.154@gmail.com' AS email
UNION ALL SELECT 'Franz Kafka' AS nome, 'cadastro.autor.155@gmail.com' AS email
UNION ALL SELECT 'Gilda de Mello e. Souza' AS nome, 'cadastro.autor.156@gmail.com' AS email
UNION ALL SELECT 'Haroldo de Campos' AS nome, 'cadastro.autor.157@gmail.com' AS email
UNION ALL SELECT 'Italo Calvino' AS nome, 'cadastro.autor.158@gmail.com' AS email
UNION ALL SELECT 'John Boyne' AS nome, 'cadastro.autor.159@gmail.com' AS email
UNION ALL SELECT 'José Eli da Veiga' AS nome, 'cadastro.autor.160@gmail.com' AS email
UNION ALL SELECT 'Jostein Gaarder' AS nome, 'cadastro.autor.161@gmail.com' AS email
UNION ALL SELECT 'Laura de Mello e. Souza' AS nome, 'cadastro.autor.162@gmail.com' AS email
UNION ALL SELECT 'Lima Barreto' AS nome, 'cadastro.autor.163@gmail.com' AS email
UNION ALL SELECT 'Lourenço Mutarelli' AS nome, 'cadastro.autor.164@gmail.com' AS email
UNION ALL SELECT 'Luís Antônio Giron' AS nome, 'cadastro.autor.165@gmail.com' AS email
UNION ALL SELECT 'Luiz Costa Lima' AS nome, 'cadastro.autor.166@gmail.com' AS email
UNION ALL SELECT 'Marcelo Gleiser' AS nome, 'cadastro.autor.167@gmail.com' AS email
UNION ALL SELECT 'Margaret Atwood' AS nome, 'cadastro.autor.168@gmail.com' AS email
UNION ALL SELECT 'Milton Hatoum' AS nome, 'cadastro.autor.169@gmail.com' AS email
UNION ALL SELECT 'Patrícia Melo' AS nome, 'cadastro.autor.170@gmail.com' AS email
UNION ALL SELECT 'Paulo Leminski' AS nome, 'cadastro.autor.171@gmail.com' AS email
UNION ALL SELECT 'Pierre Bourdieu' AS nome, 'cadastro.autor.172@gmail.com' AS email
UNION ALL SELECT 'Richard Dawkins' AS nome, 'cadastro.autor.173@gmail.com' AS email
UNION ALL SELECT 'Robert Greene' AS nome, 'cadastro.autor.174@gmail.com' AS email
UNION ALL SELECT 'Roberto M. Moura' AS nome, 'cadastro.autor.175@gmail.com' AS email
UNION ALL SELECT 'Rosiska Darcy de Oliveira' AS nome, 'cadastro.autor.176@gmail.com' AS email
UNION ALL SELECT 'Ruy Castro' AS nome, 'cadastro.autor.177@gmail.com' AS email
UNION ALL SELECT 'Salman Rushdie' AS nome, 'cadastro.autor.178@gmail.com' AS email
UNION ALL SELECT 'Sandra Lauderdale Graham' AS nome, 'cadastro.autor.179@gmail.com' AS email
UNION ALL SELECT 'Toni Morrison' AS nome, 'cadastro.autor.180@gmail.com' AS email
UNION ALL SELECT '[HENFIL' AS nome, 'cadastro.autor.181@gmail.com' AS email
UNION ALL SELECT '0' AS nome, 'cadastro.autor.182@gmail.com' AS email
UNION ALL SELECT '3' AS nome, 'cadastro.autor.183@gmail.com' AS email
UNION ALL SELECT 'Abdulrazak Gurnah' AS nome, 'cadastro.autor.184@gmail.com' AS email
UNION ALL SELECT 'Adam Grant' AS nome, 'cadastro.autor.185@gmail.com' AS email
UNION ALL SELECT 'Adam Kucharski' AS nome, 'cadastro.autor.186@gmail.com' AS email
UNION ALL SELECT 'Adam Rutherford' AS nome, 'cadastro.autor.187@gmail.com' AS email
UNION ALL SELECT 'Adriana Lunardi' AS nome, 'cadastro.autor.188@gmail.com' AS email
UNION ALL SELECT 'Adriana Victor' AS nome, 'cadastro.autor.189@gmail.com' AS email
UNION ALL SELECT 'Adriano Silva' AS nome, 'cadastro.autor.190@gmail.com' AS email
UNION ALL SELECT 'Aída Marques' AS nome, 'cadastro.autor.191@gmail.com' AS email
UNION ALL SELECT 'Aidan Macfarlane' AS nome, 'cadastro.autor.192@gmail.com' AS email
UNION ALL SELECT 'Akiva Goldsman' AS nome, 'cadastro.autor.193@gmail.com' AS email
UNION ALL SELECT 'Alain De Botton' AS nome, 'cadastro.autor.194@gmail.com' AS email
UNION ALL SELECT 'Albert Einstein' AS nome, 'cadastro.autor.195@gmail.com' AS email
UNION ALL SELECT 'Alberto Dines' AS nome, 'cadastro.autor.196@gmail.com' AS email
UNION ALL SELECT 'Alberto Manguel' AS nome, 'cadastro.autor.197@gmail.com' AS email
UNION ALL SELECT 'Alberto Martins' AS nome, 'cadastro.autor.198@gmail.com' AS email
UNION ALL SELECT 'Alejandro Zambra' AS nome, 'cadastro.autor.199@gmail.com' AS email
UNION ALL SELECT 'Alessandro Pinzani' AS nome, 'cadastro.autor.200@gmail.com' AS email
UNION ALL SELECT 'Alessio Palmero Aprosio' AS nome, 'cadastro.autor.201@gmail.com' AS email
UNION ALL SELECT 'Alex Bellos' AS nome, 'cadastro.autor.202@gmail.com' AS email
UNION ALL SELECT 'Alexandra Robbins' AS nome, 'cadastro.autor.203@gmail.com' AS email
UNION ALL SELECT 'Alexandre Rampazo' AS nome, 'cadastro.autor.204@gmail.com' AS email
UNION ALL SELECT 'Alfred Lansing' AS nome, 'cadastro.autor.205@gmail.com' AS email
UNION ALL SELECT 'Alfredo Sirkis' AS nome, 'cadastro.autor.206@gmail.com' AS email
UNION ALL SELECT 'Alice Walker' AS nome, 'cadastro.autor.207@gmail.com' AS email
UNION ALL SELECT 'Alina Tugend' AS nome, 'cadastro.autor.208@gmail.com' AS email
UNION ALL SELECT 'Allan Pease' AS nome, 'cadastro.autor.209@gmail.com' AS email
UNION ALL SELECT 'Allan Percy' AS nome, 'cadastro.autor.210@gmail.com' AS email
UNION ALL SELECT 'Amilcar Bettega' AS nome, 'cadastro.autor.211@gmail.com' AS email
UNION ALL SELECT 'Amy E. Herman' AS nome, 'cadastro.autor.212@gmail.com' AS email
UNION ALL SELECT 'Ana Paula Maia' AS nome, 'cadastro.autor.213@gmail.com' AS email
UNION ALL SELECT 'Ana Vicentini de Azevedo' AS nome, 'cadastro.autor.214@gmail.com' AS email
UNION ALL SELECT 'André Diniz' AS nome, 'cadastro.autor.215@gmail.com' AS email
UNION ALL SELECT 'André Freire' AS nome, 'cadastro.autor.216@gmail.com' AS email
UNION ALL SELECT 'André Lara Resende' AS nome, 'cadastro.autor.217@gmail.com' AS email
UNION ALL SELECT 'André Vianco' AS nome, 'cadastro.autor.218@gmail.com' AS email
UNION ALL SELECT 'Andréa del Fuego' AS nome, 'cadastro.autor.219@gmail.com' AS email
UNION ALL SELECT 'Andres Mcconnon / Aili Mcconnon' AS nome, 'cadastro.autor.220@gmail.com' AS email
UNION ALL SELECT 'Andrew Solomon' AS nome, 'cadastro.autor.221@gmail.com' AS email
UNION ALL SELECT 'Anela Mendes de Almeida' AS nome, 'cadastro.autor.222@gmail.com' AS email
UNION ALL SELECT 'Angela das Neves' AS nome, 'cadastro.autor.223@gmail.com' AS email
UNION ALL SELECT 'Angela Mendes de Almeida' AS nome, 'cadastro.autor.224@gmail.com' AS email
UNION ALL SELECT 'Angélica Freitas' AS nome, 'cadastro.autor.225@gmail.com' AS email
UNION ALL SELECT 'Ann Macfarlane Aidan E Mcpherson' AS nome, 'cadastro.autor.226@gmail.com' AS email
UNION ALL SELECT 'Anne Frank e Karolien van Eck' AS nome, 'cadastro.autor.227@gmail.com' AS email
UNION ALL SELECT 'Anne Morrow Lindbergh' AS nome, 'cadastro.autor.228@gmail.com' AS email
UNION ALL SELECT 'Annie Leonard' AS nome, 'cadastro.autor.229@gmail.com' AS email
UNION ALL SELECT 'Annita Costa Malufe' AS nome, 'cadastro.autor.230@gmail.com' AS email
UNION ALL SELECT 'António Brito' AS nome, 'cadastro.autor.231@gmail.com' AS email
UNION ALL SELECT 'Antonio Britto' AS nome, 'cadastro.autor.232@gmail.com' AS email
UNION ALL SELECT 'Antônio Bulhões' AS nome, 'cadastro.autor.233@gmail.com' AS email
UNION ALL SELECT 'Antonio Candido' AS nome, 'cadastro.autor.234@gmail.com' AS email
UNION ALL SELECT 'Antonio Fagundes' AS nome, 'cadastro.autor.235@gmail.com' AS email
UNION ALL SELECT 'Antonio Prata' AS nome, 'cadastro.autor.236@gmail.com' AS email
UNION ALL SELECT 'Antonio Quinet' AS nome, 'cadastro.autor.237@gmail.com' AS email
UNION ALL SELECT 'Antonio R. Damasio' AS nome, 'cadastro.autor.238@gmail.com' AS email
UNION ALL SELECT 'Antônio Sérgio Guimarães' AS nome, 'cadastro.autor.239@gmail.com' AS email
UNION ALL SELECT 'Arley Pereira' AS nome, 'cadastro.autor.240@gmail.com' AS email
UNION ALL SELECT 'Arthur Dapieve' AS nome, 'cadastro.autor.241@gmail.com' AS email
UNION ALL SELECT 'Artur da Távola' AS nome, 'cadastro.autor.242@gmail.com' AS email
UNION ALL SELECT 'Arundhati Roy' AS nome, 'cadastro.autor.243@gmail.com' AS email
UNION ALL SELECT 'Augusto Boal' AS nome, 'cadastro.autor.244@gmail.com' AS email
UNION ALL SELECT 'Augusto Jorge Cury' AS nome, 'cadastro.autor.245@gmail.com' AS email
UNION ALL SELECT 'Autran Dourado' AS nome, 'cadastro.autor.246@gmail.com' AS email
UNION ALL SELECT 'Awam Amkpa' AS nome, 'cadastro.autor.247@gmail.com' AS email
UNION ALL SELECT 'Ayn Rand' AS nome, 'cadastro.autor.248@gmail.com' AS email
UNION ALL SELECT 'Ayrton Mugnaini Jr.' AS nome, 'cadastro.autor.249@gmail.com' AS email
UNION ALL SELECT 'Barbara Pease' AS nome, 'cadastro.autor.250@gmail.com' AS email
UNION ALL SELECT 'Bee Wilson' AS nome, 'cadastro.autor.251@gmail.com' AS email
UNION ALL SELECT 'Benedict Anderson' AS nome, 'cadastro.autor.252@gmail.com' AS email
UNION ALL SELECT 'Benedito Nunes' AS nome, 'cadastro.autor.253@gmail.com' AS email
UNION ALL SELECT 'Bernard Lewis' AS nome, 'cadastro.autor.254@gmail.com' AS email
UNION ALL SELECT 'Bernardina Botelho de Magalhães' AS nome, 'cadastro.autor.255@gmail.com' AS email
UNION ALL SELECT 'Bernardinho' AS nome, 'cadastro.autor.256@gmail.com' AS email
UNION ALL SELECT 'Bernardo Carvalho' AS nome, 'cadastro.autor.257@gmail.com' AS email
UNION ALL SELECT 'Bernardo Pereira de Vasconcelos' AS nome, 'cadastro.autor.258@gmail.com' AS email
UNION ALL SELECT 'Bernardo Sorj' AS nome, 'cadastro.autor.259@gmail.com' AS email
UNION ALL SELECT 'Bessel van der Kolk' AS nome, 'cadastro.autor.260@gmail.com' AS email
UNION ALL SELECT 'Bianca Freire-Medeiros' AS nome, 'cadastro.autor.261@gmail.com' AS email
UNION ALL SELECT 'Bill Bryson' AS nome, 'cadastro.autor.262@gmail.com' AS email
UNION ALL SELECT 'Bill Burnett' AS nome, 'cadastro.autor.263@gmail.com' AS email
UNION ALL SELECT 'Bill Gates' AS nome, 'cadastro.autor.264@gmail.com' AS email
UNION ALL SELECT 'Bob Nelson' AS nome, 'cadastro.autor.265@gmail.com' AS email
UNION ALL SELECT 'Bráulio Bessa' AS nome, 'cadastro.autor.266@gmail.com' AS email
UNION ALL SELECT 'Brenda Rickman Vantrease' AS nome, 'cadastro.autor.267@gmail.com' AS email
UNION ALL SELECT 'Brendan I. Koerner' AS nome, 'cadastro.autor.268@gmail.com' AS email
UNION ALL SELECT 'Brian Tracy' AS nome, 'cadastro.autor.269@gmail.com' AS email
UNION ALL SELECT 'Bruce Fink' AS nome, 'cadastro.autor.270@gmail.com' AS email
UNION ALL SELECT 'Bruno Brum' AS nome, 'cadastro.autor.271@gmail.com' AS email
UNION ALL SELECT 'Bruno Latour' AS nome, 'cadastro.autor.272@gmail.com' AS email
UNION ALL SELECT 'C. Baxter Kruger' AS nome, 'cadastro.autor.273@gmail.com' AS email
UNION ALL SELECT 'Caetano Veloso' AS nome, 'cadastro.autor.274@gmail.com' AS email
UNION ALL SELECT 'Caetano W. Galindo' AS nome, 'cadastro.autor.275@gmail.com' AS email
UNION ALL SELECT 'Caio Fernando Abreu' AS nome, 'cadastro.autor.276@gmail.com' AS email
UNION ALL SELECT 'Cairu, José da Silva Lisboa Visconde de' AS nome, 'cadastro.autor.277@gmail.com' AS email
UNION ALL SELECT 'Caleb A. Scharf' AS nome, 'cadastro.autor.278@gmail.com' AS email
UNION ALL SELECT 'Camila Sosa Villada' AS nome, 'cadastro.autor.279@gmail.com' AS email
UNION ALL SELECT 'Carl L. Hart' AS nome, 'cadastro.autor.280@gmail.com' AS email
UNION ALL SELECT 'Carlos Albuquerque' AS nome, 'cadastro.autor.281@gmail.com' AS email
UNION ALL SELECT 'Carlos Fausto' AS nome, 'cadastro.autor.282@gmail.com' AS email
UNION ALL SELECT 'Carlos Guilherme Mota' AS nome, 'cadastro.autor.283@gmail.com' AS email
UNION ALL SELECT 'Carlos Mendes de Sousa' AS nome, 'cadastro.autor.284@gmail.com' AS email
UNION ALL SELECT 'Carola Saavedra' AS nome, 'cadastro.autor.285@gmail.com' AS email
UNION ALL SELECT 'Carolina Maria de Jesus' AS nome, 'cadastro.autor.286@gmail.com' AS email
UNION ALL SELECT 'Carolina Moreyra / Odilon Moraes' AS nome, 'cadastro.autor.287@gmail.com' AS email
UNION ALL SELECT 'Caroline Kepnes' AS nome, 'cadastro.autor.288@gmail.com' AS email
UNION ALL SELECT 'César Aira' AS nome, 'cadastro.autor.289@gmail.com' AS email
UNION ALL SELECT 'Chacal' AS nome, 'cadastro.autor.290@gmail.com' AS email
UNION ALL SELECT 'Charles Ferguson' AS nome, 'cadastro.autor.291@gmail.com' AS email
UNION ALL SELECT 'Charlie Mackesy' AS nome, 'cadastro.autor.292@gmail.com' AS email
UNION ALL SELECT 'Che rie Carter-Scott' AS nome, 'cadastro.autor.293@gmail.com' AS email
UNION ALL SELECT 'Chico Mattoso' AS nome, 'cadastro.autor.294@gmail.com' AS email
UNION ALL SELECT 'Christian Ingrao' AS nome, 'cadastro.autor.295@gmail.com' AS email
UNION ALL SELECT 'Christina Kallas' AS nome, 'cadastro.autor.296@gmail.com' AS email
UNION ALL SELECT 'Christopher Corr Stephen Davies' AS nome, 'cadastro.autor.297@gmail.com' AS email
UNION ALL SELECT 'Cida Bento' AS nome, 'cadastro.autor.298@gmail.com' AS email
UNION ALL SELECT 'Ciro Biderman' AS nome, 'cadastro.autor.299@gmail.com' AS email
UNION ALL SELECT 'Clara Mafra' AS nome, 'cadastro.autor.300@gmail.com' AS email
) AS d WHERE NOT EXISTS (SELECT 1 FROM autor a WHERE a.nome = d.nome);

-- 6 funcionários fictícios vinculados à única loja.
INSERT INTO funcionario (id_loja, nome, cpf, email, nasc, telefone, endereco, ativo, sexo, nacionalidade) SELECT l.id_loja, 'Ana Costa', '000.000.000-11', 'ana.costa.livraria@gmail.com', '1992-04-12', '+55 (11) 99910-0101', JSON_OBJECT('logradouro', 'Rua Augusta', 'numero', 1200, 'bairro', 'Consolacao', 'cidade', 'Sao Paulo', 'estado', 'SP', 'cep', '01304-001'), TRUE, 'F', 'Brasil' FROM loja l WHERE l.nome = 'Livraria de Demonstracao - Carga' AND NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.cpf = '000.000.000-11');
INSERT INTO funcionario (id_loja, nome, cpf, email, nasc, telefone, endereco, ativo, sexo, nacionalidade) SELECT l.id_loja, 'Bruno Silva', '000.000.000-12', 'bruno.silva.livraria@gmail.com', '1988-09-25', '+55 (11) 99910-0102', JSON_OBJECT('logradouro', 'Rua Augusta', 'numero', 1200, 'bairro', 'Consolacao', 'cidade', 'Sao Paulo', 'estado', 'SP', 'cep', '01304-001'), TRUE, 'M', 'Brasil' FROM loja l WHERE l.nome = 'Livraria de Demonstracao - Carga' AND NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.cpf = '000.000.000-12');
INSERT INTO funcionario (id_loja, nome, cpf, email, nasc, telefone, endereco, ativo, sexo, nacionalidade) SELECT l.id_loja, 'Camila Mendes', '000.000.000-13', 'camila.mendes.livraria@gmail.com', '1995-02-08', '+55 (11) 99910-0103', JSON_OBJECT('logradouro', 'Rua Augusta', 'numero', 1200, 'bairro', 'Consolacao', 'cidade', 'Sao Paulo', 'estado', 'SP', 'cep', '01304-001'), TRUE, 'F', 'Brasil' FROM loja l WHERE l.nome = 'Livraria de Demonstracao - Carga' AND NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.cpf = '000.000.000-13');
INSERT INTO funcionario (id_loja, nome, cpf, email, nasc, telefone, endereco, ativo, sexo, nacionalidade) SELECT l.id_loja, 'Rafael Almeida', '000.000.000-14', 'rafael.almeida.livraria@gmail.com', '1990-11-19', '+55 (11) 99910-0104', JSON_OBJECT('logradouro', 'Rua Augusta', 'numero', 1200, 'bairro', 'Consolacao', 'cidade', 'Sao Paulo', 'estado', 'SP', 'cep', '01304-001'), TRUE, 'M', 'Brasil' FROM loja l WHERE l.nome = 'Livraria de Demonstracao - Carga' AND NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.cpf = '000.000.000-14');
INSERT INTO funcionario (id_loja, nome, cpf, email, nasc, telefone, endereco, ativo, sexo, nacionalidade) SELECT l.id_loja, 'Juliana Rocha', '000.000.000-15', 'juliana.rocha.livraria@gmail.com', '1993-06-03', '+55 (11) 99910-0105', JSON_OBJECT('logradouro', 'Rua Augusta', 'numero', 1200, 'bairro', 'Consolacao', 'cidade', 'Sao Paulo', 'estado', 'SP', 'cep', '01304-001'), TRUE, 'F', 'Brasil' FROM loja l WHERE l.nome = 'Livraria de Demonstracao - Carga' AND NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.cpf = '000.000.000-15');
INSERT INTO funcionario (id_loja, nome, cpf, email, nasc, telefone, endereco, ativo, sexo, nacionalidade) SELECT l.id_loja, 'Marcos Lima', '000.000.000-16', 'marcos.lima.livraria@gmail.com', '1987-01-30', '+55 (11) 99910-0106', JSON_OBJECT('logradouro', 'Rua Augusta', 'numero', 1200, 'bairro', 'Consolacao', 'cidade', 'Sao Paulo', 'estado', 'SP', 'cep', '01304-001'), TRUE, 'M', 'Brasil' FROM loja l WHERE l.nome = 'Livraria de Demonstracao - Carga' AND NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.cpf = '000.000.000-16');

-- 500 livros únicos, ligados a autores, editoras e gêneros por nome.
INSERT INTO livro (id_autor, id_editora, id_genero, codigo_barras, isbn, nome, preco, classificacao, lancamento, ativo)
SELECT a.id_autor, e.id_editora, g.id_genero, d.isbn, d.isbn, d.titulo, d.preco, d.classificacao, d.lancamento, TRUE
FROM (
SELECT '9786555320350' AS isbn, 'A hora da estrela' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Romance' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2020-11-16' AS lancamento
UNION ALL SELECT '9786555323948' AS isbn, 'A paixão segundo G. H.' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Romance' AS genero, 124.90 AS preco, 'Adulto' AS classificacao, '2024-01-26' AS lancamento
UNION ALL SELECT '9786555322132' AS isbn, 'Laços de família' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Contos' AS genero, 79.90 AS preco, 'Adulto' AS classificacao, '2022-03-25' AS lancamento
UNION ALL SELECT '9786555326758' AS isbn, 'Água viva' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Ficcao Brasileira' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '2026-05-08' AS lancamento
UNION ALL SELECT '9788532531735' AS isbn, 'Felicidade clandestina' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Contos' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2020-03-10' AS lancamento
UNION ALL SELECT '9788532511010' AS isbn, 'Harry Potter e a Pedra Filosofal' AS titulo, 'J.K. Rowling' AS autor, 'Editora Rocco' AS editora, 'Fantasia' AS genero, 54.90 AS preco, 'Infantojuvenil' AS classificacao, '2000-04-07' AS lancamento
UNION ALL SELECT '9788532531780' AS isbn, 'Harry Potter e o Prisioneiro de Azkaban' AS titulo, 'J.K. Rowling' AS autor, 'Editora Rocco' AS editora, 'Fantasia' AS genero, 69.90 AS preco, 'Infantojuvenil' AS classificacao, '2020-04-30' AS lancamento
UNION ALL SELECT '9788535933390' AS isbn, 'O avesso da pele' AS titulo, 'Jeferson Tenório' AS autor, 'Companhia das Letras' AS editora, 'Romance' AS genero, 79.90 AS preco, 'Adulto' AS classificacao, '2020-08-10' AS lancamento
UNION ALL SELECT '9788535939378' AS isbn, 'De onde eles vêm' AS titulo, 'Jeferson Tenório' AS autor, 'Companhia das Letras' AS editora, 'Romance' AS genero, 79.90 AS preco, 'Adulto' AS classificacao, '2024-10-25' AS lancamento
UNION ALL SELECT '9788535911695' AS isbn, 'Capitães da Areia' AS titulo, 'Jorge Amado' AS autor, 'Companhia das Letras' AS editora, 'Romance' AS genero, 79.90 AS preco, 'Adulto' AS classificacao, '2008-03-10' AS lancamento
UNION ALL SELECT '9788575425770' AS isbn, 'O Aleph' AS titulo, 'Paulo Coelho' AS autor, 'Sextante' AS editora, 'Autoajuda' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788532502018' AS isbn, 'O que faz o brasil, Brasil?' AS titulo, 'Roberto da Matta' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1986-01-01' AS lancamento
UNION ALL SELECT '9781564787842' AS isbn, 'The girl in the photograph' AS titulo, 'Lygia Fagundes Telles' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Brasileira' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '1973-01-01' AS lancamento
UNION ALL SELECT '9788374809290' AS isbn, 'The Graveyard Book' AS titulo, 'Neil Gaiman' AS autor, 'Editora Rocco' AS editora, 'Literatura Infantojuvenil' AS genero, 59.90 AS preco, 'Infantojuvenil' AS classificacao, '2008-01-01' AS lancamento
UNION ALL SELECT '9788532522580' AS isbn, 'Uma Fada Veio Me Visitar' AS titulo, 'Thalita Reboucas' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9789500421683' AS isbn, 'Animorphs - The Reaction' AS titulo, 'Katherine Applegate' AS autor, 'Editora Rocco' AS editora, 'Literatura Infantojuvenil' AS genero, 44.90 AS preco, 'Infantojuvenil' AS classificacao, '1997-01-01' AS lancamento
UNION ALL SELECT '9780385521109' AS isbn, 'The Innocent Man' AS titulo, 'John Grisham' AS autor, 'Editora Rocco' AS editora, 'Biografia e Memorias' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9780702340581' AS isbn, 'Sunrise on the Reaping' AS titulo, 'Suzanne Collins' AS autor, 'Editora Rocco' AS editora, 'Literatura Infantojuvenil' AS genero, 69.90 AS preco, 'Infantojuvenil' AS classificacao, '2025-01-01' AS lancamento
UNION ALL SELECT '9788532511409' AS isbn, 'Quando eu voltei, tive uma surpresa' AS titulo, 'Joel Rufino dos Santos' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788571640245' AS isbn, 'Nas malhas da letra' AS titulo, 'Silviano Santiago' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1989-01-01' AS lancamento
UNION ALL SELECT '9788532524867' AS isbn, 'Um homem chamado Jesus' AS titulo, 'Betto Frei' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788498722321' AS isbn, 'Blackwood Farm' AS titulo, 'Anne Rice' AS autor, 'Editora Rocco' AS editora, 'Terror' AS genero, 89.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788573261004' AS isbn, 'A pedra do meio-dia, ou, Artur e Isadora' AS titulo, 'Bráulio Tavares' AS autor, 'Editora 34' AS editora, 'Literatura Infantojuvenil' AS genero, 34.90 AS preco, 'Infantojuvenil' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788573260182' AS isbn, 'Algaravias; Câmara de ecos' AS titulo, 'Waly Salomão' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '1996-01-01' AS lancamento
UNION ALL SELECT '9788575422137' AS isbn, 'Uma Boa Noite De Sono' AS titulo, 'Deepak Chopra' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9783426227367' AS isbn, 'To Sleep in a Sea of Stars' AS titulo, 'Christopher Paolini' AS autor, 'Editora Rocco' AS editora, 'Ficcao Cientifica' AS genero, 114.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788532505927' AS isbn, 'As baleias do Saguenay' AS titulo, 'João Batista Melo' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9788532522412' AS isbn, 'O paradoxo de Rousseau' AS titulo, 'Wanderley Guilherme dos Santos' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788532510280' AS isbn, 'A república dos bugres' AS titulo, 'Ruy Tapioca' AS autor, 'Editora Rocco' AS editora, 'Historia' AS genero, 79.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788532513496' AS isbn, 'A gaiola de Faraday' AS titulo, 'Bernardo Ajzenberg' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9788532520630' AS isbn, 'A confissão' AS titulo, 'Flávio Carneiro' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9780434829651' AS isbn, 'Kalki' AS titulo, 'Gore Vidal' AS autor, 'Editora Rocco' AS editora, 'Fantasia' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '1978-01-01' AS lancamento
UNION ALL SELECT '9788532517913' AS isbn, 'Caligrafias' AS titulo, 'Adriana Lisboa' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788532506153' AS isbn, 'Eu sei, mas não devia' AS titulo, 'Marina Colasanti' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1996-01-01' AS lancamento
UNION ALL SELECT '9788532521507' AS isbn, 'Ciranda das Mulheres Sabias' AS titulo, 'Clarissa Pinkola Estés' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788532520562' AS isbn, 'Extremamente Alto & Extremamente Perto' AS titulo, 'Jonathan Safran Foer' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9788532528421' AS isbn, 'Roube Como Um Artista' AS titulo, 'Austin Kleon' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788532508768' AS isbn, 'A alma imoral' AS titulo, 'Nilton Bonder' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788535907254' AS isbn, 'Intermitencias da Morte, As' AS titulo, 'José Saramago' AS autor, 'Companhia das Letras' AS editora, 'Romance' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9788571648975' AS isbn, 'Estação Carandiru' AS titulo, 'Drauzio Varella' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788535918717' AS isbn, 'Psicologia das Massas e Análise do Eu e Outros Textos. 1920-1923' AS titulo, 'Sigmund Freud' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9788571640320' AS isbn, 'O cobrador contos' AS titulo, 'Rubem Fonseca' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1989-01-01' AS lancamento
UNION ALL SELECT '9781531888121' AS isbn, 'Pale Blue Dot' AS titulo, 'Carl Sagan' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '1994-01-01' AS lancamento
UNION ALL SELECT '9788575429969' AS isbn, 'A Batalha do Avaí' AS titulo, 'Lilia Moritz Schwarcz' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788535905977' AS isbn, 'Ana Terra' AS titulo, 'Erico Veríssimo' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9789722324045' AS isbn, 'O homem que matou Getúlio Vargas' AS titulo, 'Jô Soares' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788535912869' AS isbn, 'Domingos Sodré, um sacerdote africano' AS titulo, 'João José Reis' AS autor, 'Companhia das Letras' AS editora, 'Biografia e Memorias' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '2008-01-01' AS lancamento
UNION ALL SELECT '9781716994982' AS isbn, 'Sapiens' AS titulo, 'Yuval Noah Harari' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9788535933697' AS isbn, 'A Vida Nao e Util' AS titulo, 'Ailton Krenak' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788535900743' AS isbn, 'Corações sujos' AS titulo, 'Fernando Morais' AS autor, 'Companhia das Letras' AS editora, 'Terror' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9789896651275' AS isbn, 'Nem Todas as Baleias Voam' AS titulo, 'Afonso Cruz' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788535922837' AS isbn, 'Alguma Poesia' AS titulo, 'Carlos Drummond de Andrade' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9789722008143' AS isbn, 'Boca do inferno' AS titulo, 'Ana Maria Miranda' AS autor, 'Companhia das Letras' AS editora, 'Suspense e Misterio' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '1989-01-01' AS lancamento
UNION ALL SELECT '9789024796052' AS isbn, 'Terra Sonambula (Uma Terra sem amos)' AS titulo, 'Mia Couto' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1992-01-01' AS lancamento
UNION ALL SELECT '9788537812013' AS isbn, 'Travessia Maritima Com Dom Quixote' AS titulo, 'Thomas Mann' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788571649378' AS isbn, 'A mulher que escreveu a Bíblia' AS titulo, 'Moacyr Scliar' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788571646506' AS isbn, 'Sabbath''s Theater' AS titulo, 'Philip Roth' AS autor, 'Companhia das Letras' AS editora, 'Autoajuda' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9788535904482' AS isbn, 'Chico Mendes, crime e castigo' AS titulo, 'Zuenir Ventura' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2003-01-01' AS lancamento
UNION ALL SELECT '9781250442413' AS isbn, 'Jantar Secreto' AS titulo, 'Raphael Montes' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9780062109385' AS isbn, 'Just Kids' AS titulo, 'Patti Smith' AS autor, 'Companhia das Letras' AS editora, 'Poesia' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9788571646537' AS isbn, 'Um crime delicado' AS titulo, 'Sérgio Sant''Anna' AS autor, 'Companhia das Letras' AS editora, 'Romance' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1997-01-01' AS lancamento
UNION ALL SELECT '9781447264057' AS isbn, 'On the Move' AS titulo, 'Oliver Sacks' AS autor, 'Companhia das Letras' AS editora, 'Biografia e Memorias' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9788535900200' AS isbn, 'Pilatos' AS titulo, 'Carlos Heitor Cony' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9781802061574' AS isbn, 'Pirate Enlightenment' AS titulo, 'David Graeber' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2023-01-01' AS lancamento
UNION ALL SELECT '9788571109933' AS isbn, 'Tempos líquidos' AS titulo, 'Zygmunt Bauman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788566642346' AS isbn, 'Diário de Pilar no Egito' AS titulo, 'Flávia Lins e Silva' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788573263596' AS isbn, 'Morte De Ivan Ilitch, A' AS titulo, 'Jonathan C. Young' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788537814635' AS isbn, 'O Signo dos Quatro' AS titulo, 'Arthur Conan Doyle' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9781846685323' AS isbn, 'In Pursuit of the Unknown' AS titulo, 'Ian Stewart' AS autor, 'Zahar' AS editora, 'Historia' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9788571104051' AS isbn, 'Iniciação À História Da Filosofia' AS titulo, 'Danilo Marcondes' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1997-01-01' AS lancamento
UNION ALL SELECT '9780141026473' AS isbn, 'The Drunkard''s Walk' AS titulo, 'Leonard Mlodinow' AS autor, 'Zahar' AS editora, 'Autoajuda' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2008-01-01' AS lancamento
UNION ALL SELECT '9788571106123' AS isbn, 'Cinco Dias em Londres' AS titulo, 'John Lukacs' AS autor, 'Zahar' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9788537810286' AS isbn, 'Um antropólogo na cidade' AS titulo, 'Gilberto Velho' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9783103973341' AS isbn, 'The courage of hopelessness' AS titulo, 'Slavoj Žižek' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788571104433' AS isbn, 'Escritos' AS titulo, 'Jacques Lacan' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788537813522' AS isbn, 'Autobiografia' AS titulo, 'Stefan Zweig' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788430617616' AS isbn, 'Think Like an Artist' AS titulo, 'Will Gompertz' AS autor, 'Zahar' AS editora, 'Autoajuda' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9788571106024' AS isbn, 'Barões e escravos do café' AS titulo, 'Sonia Sant''Anna' AS autor, 'Zahar' AS editora, 'Literatura Infantojuvenil' AS genero, 44.90 AS preco, 'Infantojuvenil' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9788537817360' AS isbn, 'Edipo Rei' AS titulo, 'Sófocles' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2018-01-01' AS lancamento
UNION ALL SELECT '9788571104518' AS isbn, 'Sócrates: 469 a.c.- 399 a.c.: em 90 minutos' AS titulo, 'Paul Strathern' AS autor, 'Zahar' AS editora, 'Historia' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788537817650' AS isbn, 'Deus. Uma Historia Humana' AS titulo, 'Reza Aslan' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2018-01-01' AS lancamento
UNION ALL SELECT '9788537802618' AS isbn, 'The politics of climate change' AS titulo, 'Anthony Giddens' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788537815199' AS isbn, 'Os Livros da Selva. Contos de Mowgli e Outras Histórias' AS titulo, 'Rudyard Kipling' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788537810880' AS isbn, 'Corcunda de Notre Dame - Ed. Comentada e Ilustrada' AS titulo, 'Victor Hugo' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788537802021' AS isbn, 'Guia ilustrado TV Globo' AS titulo, 'Projeto Memória das Organizações Globo (Brazil)' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788571105348' AS isbn, 'A proclamação da república' AS titulo, 'Celso Castro' AS autor, 'Zahar' AS editora, 'Historia' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788537810750' AS isbn, 'Por Que Repetimos Os Mesmos Erros' AS titulo, 'Juan David Nasio' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9788537815861' AS isbn, 'O Homem Invisível' AS titulo, 'H. G. Wells' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9780385408585' AS isbn, 'Voices in the Park' AS titulo, 'Anthony Browne' AS autor, 'Zahar' AS editora, 'Literatura Infantojuvenil' AS genero, 29.90 AS preco, 'Infantojuvenil' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788537815120' AS isbn, 'Estatística' AS titulo, 'Charles J. Wheelan' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9786056355929' AS isbn, 'The Disappearing Spoon' AS titulo, 'Sam Kean' AS autor, 'Zahar' AS editora, 'Literatura Infantojuvenil' AS genero, 64.90 AS preco, 'Infantojuvenil' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788571105409' AS isbn, 'Por que a Psicanálise?' AS titulo, 'Elisabeth Roudinesco' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788571100039' AS isbn, 'Freud e o inconsciente' AS titulo, 'L. A. García-Roza' AS autor, 'Zahar' AS editora, 'Autoajuda' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788573260946' AS isbn, 'História social da música popular brasileira' AS titulo, 'José Ramos Tinhorão' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788573262827' AS isbn, 'Bangalô' AS titulo, 'Marcelo Mirisola' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2003-01-01' AS lancamento
UNION ALL SELECT '9788511090031' AS isbn, 'Economia brasileira' AS titulo, 'Luiz Carlos Bresser Pereira' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1982-01-01' AS lancamento
UNION ALL SELECT '9788573265309' AS isbn, 'Garimpo' AS titulo, 'Beatriz Bracher' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788573266801' AS isbn, 'O mestre e Margarida' AS titulo, 'Михаил Афанасьевич Булгаков' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788573263282' AS isbn, 'A história dos ossos' AS titulo, 'Alberto Alexandre Martins' AS autor, 'Editora 34' AS editora, 'Romance' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9788573261639' AS isbn, 'Escritos urbanos' AS titulo, 'Lúcio Kowarick' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788573262896' AS isbn, 'Erefuê' AS titulo, 'Evandro Ferreira' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788573263855' AS isbn, 'A utopia brasileira e os movimentos negros' AS titulo, 'Antonio Risério' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788573263640' AS isbn, 'Textos do Trópico de Capricórnio' AS titulo, 'Aracy A. Amaral' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9788573263961' AS isbn, 'Uma história da música popular brasileira' AS titulo, 'Jairo Severiano' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '2008-01-01' AS lancamento
UNION ALL SELECT '9788573262728' AS isbn, 'A era dos festivais' AS titulo, 'Zuza Homem de Mello' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 79.90 AS preco, 'Adulto' AS classificacao, '2003-01-01' AS lancamento
UNION ALL SELECT '9788573260175' AS isbn, 'Mille plateaux' AS titulo, 'Gilles Deleuze' AS autor, 'Editora 34' AS editora, 'Autoajuda' AS genero, 89.90 AS preco, 'Adulto' AS classificacao, '1980-01-01' AS lancamento
UNION ALL SELECT '9788508029198' AS isbn, 'Céu, inferno' AS titulo, 'Alfredo Bosi' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '1988-01-01' AS lancamento
UNION ALL SELECT '9788573260397' AS isbn, 'O século dos manicômios' AS titulo, 'Isaias Pessotti' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '1996-01-01' AS lancamento
UNION ALL SELECT '9788573260403' AS isbn, 'Algo de sol' AS titulo, 'Nelson Ascher' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '1996-01-01' AS lancamento
UNION ALL SELECT '9788543108667' AS isbn, 'O Grande Livro de Ciencias do Manual do Mundo' AS titulo, 'Vários Autores' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788573264425' AS isbn, 'Itinerário de uma falsa vanguarda' AS titulo, 'Antonio Arnoni Prado' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788573264135' AS isbn, 'O mundo segundo Laura Ni' AS titulo, 'Maria Cecília Gomes dos Reis' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2008-01-01' AS lancamento
UNION ALL SELECT '9788573264302' AS isbn, 'Lady Macbeth do Distrito de Mtzensk' AS titulo, 'NIKOLAI LESKOV' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788573262605' AS isbn, 'Ronaldo' AS titulo, 'Jorge Caldeira' AS autor, 'Editora 34' AS editora, 'Biografia e Memorias' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9788573263886' AS isbn, 'Baque' AS titulo, 'Fabio Weintraub' AS autor, 'Editora 34' AS editora, 'Poesia' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788573265736' AS isbn, 'Ela me dá capim e eu zurro' AS titulo, 'Fabrício Corsaletti' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788585490379' AS isbn, 'O manual da mãe judia' AS titulo, 'Dan Greenburg' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '1994-01-01' AS lancamento
UNION ALL SELECT '9788573266689' AS isbn, 'Qvasi' AS titulo, 'Edimilson de Almeida Pereira' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788573261417' AS isbn, 'Saúde, desenvolvimento e política' AS titulo, 'Richard G. Parker' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788573261424' AS isbn, 'Céu-eclipse' AS titulo, 'Régis R. Bonvicino' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788575421215' AS isbn, 'Seja Lider De Si Mesmo - Augusto Cury' AS titulo, 'Augusto Cury' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788586796852' AS isbn, 'Espiritualidade' AS titulo, 'Leonardo Boff' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9788586796036' AS isbn, 'Meditando Com Brian Weiss - C/ Cd' AS titulo, 'Brian Weiss' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788575426456' AS isbn, 'Obrigado Por Existir' AS titulo, 'Bradley Trevor Greive' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9788575425640' AS isbn, 'Mexa-Se!' AS titulo, 'Rufus Butler Seder' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9789898093882' AS isbn, 'História do jazz' AS titulo, 'José Duarte' AS autor, 'Sextante' AS editora, 'Historia' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9781850789857' AS isbn, 'Son of Hamas' AS titulo, 'Mosab Hassan Yousef' AS autor, 'Sextante' AS editora, 'Biografia e Memorias' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788543103167' AS isbn, 'Dinheiro. Os Segredos de Quem Tem. Como Conquistar e Manter Sua Independência Financeira' AS titulo, 'Gustavo Cerbasi' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788575420478' AS isbn, 'Nao Leve A Vida Tao A Serio' AS titulo, 'Hugh Prather' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2003-01-01' AS lancamento
UNION ALL SELECT '9789800738252' AS isbn, 'Las trampas del dinero' AS titulo, 'Pedro N. Miranda M.' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1996-01-01' AS lancamento
UNION ALL SELECT '9786555646139' AS isbn, 'Nada pode me ferir' AS titulo, 'David Goggins' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2023-01-01' AS lancamento
UNION ALL SELECT '9788543100135' AS isbn, 'Nate Bota Pra Quebrar (Em Portugues do Brasil)' AS titulo, 'Lincoln Peirce' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788544104408' AS isbn, 'A Morte É Um Dia que Vale a Pena Viver' AS titulo, 'Ana Claudia Quintana Arantes' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9781592408412' AS isbn, 'Daring Greatly' AS titulo, 'Brené Brown' AS autor, 'Sextante' AS editora, 'Autoajuda' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9788575429846' AS isbn, 'A Magia (Em Portugues do Brasil)' AS titulo, 'Rhonda Byrne' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788575420201' AS isbn, 'O Elogio ao Ocio' AS titulo, 'Bertrand Russell' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9788599296851' AS isbn, 'Queda de gigantes' AS titulo, 'Ken Follett' AS autor, 'Sextante' AS editora, 'Historia' AS genero, 119.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788543101811' AS isbn, 'Campo dos Milagres (Em Portugues do Brasil)' AS titulo, 'Hannah Luce / Robin Gaby Fisher' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9788543101958' AS isbn, 'Floresta Encantada (Em Portugues do Brasil)' AS titulo, 'Johanna Basford' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9788575420706' AS isbn, 'Shackleton uma lição de coragem' AS titulo, 'Margot Morrell' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2003-01-01' AS lancamento
UNION ALL SELECT '9788575420188' AS isbn, 'Feminino e masculino' AS titulo, 'Rose Marie Muraro' AS autor, 'Sextante' AS editora, 'Autoajuda' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9789720071415' AS isbn, 'Até ao fim' AS titulo, 'António Vasconcelos Raposo' AS autor, 'Sextante' AS editora, 'Historia' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9788575420225' AS isbn, 'Aprendendo a silenciar a mente' AS titulo, 'Bhagwan Rajneesh' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9781607747307' AS isbn, 'The Life-Changing Magic of Tidying Up' AS titulo, 'Marie Kondo' AS autor, 'Sextante' AS editora, 'Negocios e Economia' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788575422748' AS isbn, 'O Sucesso e mais simples do que voce pensa' AS titulo, 'Ernie J. Zelinski' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788532511911' AS isbn, 'Barroco' AS titulo, 'Affonso Romano de Sant''Anna' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788569020233' AS isbn, 'O Peso do Passaro Morto' AS titulo, 'Aline Bei' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788571642829' AS isbn, 'Paratii' AS titulo, 'Amyr Klink' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1992-01-01' AS lancamento
UNION ALL SELECT '9788573260922' AS isbn, 'Maldito' AS titulo, 'Andre Barcinski' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788573260816' AS isbn, 'Tropicália' AS titulo, 'Carlos Calado' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '1997-01-01' AS lancamento
UNION ALL SELECT '9788532509147' AS isbn, 'Mera fotografia' AS titulo, 'Carlos Eduardo de Magalhães' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1990-01-01' AS lancamento
UNION ALL SELECT '9788573262223' AS isbn, 'A grande esperança em Celso Furtado' AS titulo, 'Celso Furtado' AS autor, 'Editora 34' AS editora, 'Biografia e Memorias' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9788535914115' AS isbn, 'Leite derramado' AS titulo, 'Chico Buarque' AS autor, 'Companhia das Letras' AS editora, 'Romance' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9780008115289' AS isbn, 'We Should All Be Feminists' AS titulo, 'Chimamanda Ngozi Adichie' AS autor, 'Companhia das Letras' AS editora, 'Biografia e Memorias' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788532508546' AS isbn, 'Breve espaço entre cor e sombra' AS titulo, 'Cristovão Tezza' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788571644519' AS isbn, 'O povo brasileiro' AS titulo, 'Darcy Ribeiro' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9788573261486' AS isbn, 'O violão vadio de Baden Powell' AS titulo, 'Dreyfus, Dominique.' AS autor, 'Editora 34' AS editora, 'Biografia e Memorias' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788571644687' AS isbn, 'Era Dos Extremos' AS titulo, 'Eric Hobsbawm' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788535923599' AS isbn, 'Fim' AS titulo, 'Fernanda Torres' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Brasileira' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788571108769' AS isbn, 'Negros e política (1888-1937)' AS titulo, 'Flávio dos Santos Gomes' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9788573262735' AS isbn, 'O Desaparecido ou Amerika' AS titulo, 'Franz Kafka' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2003-01-01' AS lancamento
UNION ALL SELECT '9788573262766' AS isbn, 'O tupi e o alaúde' AS titulo, 'Gilda de Mello e. Souza' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1979-01-01' AS lancamento
UNION ALL SELECT '9788573263008' AS isbn, 'Galáxias' AS titulo, 'Haroldo de Campos' AS autor, 'Editora 34' AS editora, 'Poesia' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '1984-01-01' AS lancamento
UNION ALL SELECT '9788535928846' AS isbn, 'As Cidades Invisiveis' AS titulo, 'Italo Calvino' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788535925265' AS isbn, 'This House is Haunted' AS titulo, 'John Boyne' AS autor, 'Companhia das Letras' AS editora, 'Terror' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788573265187' AS isbn, 'A desgovernança mundial da sustentabilidade' AS titulo, 'José Eli da Veiga' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788571644755' AS isbn, 'O mundo de Sofia' AS titulo, 'Jostein Gaarder' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 84.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9788585095048' AS isbn, 'O diabo e a Terra de Santa Cruz' AS titulo, 'Laura de Mello e. Souza' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '1986-01-01' AS lancamento
UNION ALL SELECT '9788573264944' AS isbn, 'Lima Barreto' AS titulo, 'Lima Barreto' AS autor, 'Editora 34' AS editora, 'Biografia e Memorias' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1986-01-01' AS lancamento
UNION ALL SELECT '9788535919189' AS isbn, 'O cheiro do ralo' AS titulo, 'Lourenço Mutarelli' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9788573261042' AS isbn, 'Ensaio de ponto' AS titulo, 'Luís Antônio Giron' AS autor, 'Editora 34' AS editora, 'Romance' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788532504142' AS isbn, 'Limites da voz' AS titulo, 'Luiz Costa Lima' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1993-01-01' AS lancamento
UNION ALL SELECT '9788571646773' AS isbn, 'A dancÌ§a do universo' AS titulo, 'Marcelo Gleiser' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9780771009433' AS isbn, 'The Testaments' AS titulo, 'Margaret Atwood' AS autor, 'Editora Rocco' AS editora, 'Literatura Infantojuvenil' AS genero, 74.90 AS preco, 'Infantojuvenil' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788535900132' AS isbn, 'Dois irmãos' AS titulo, 'Milton Hatoum' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788532525604' AS isbn, 'Ladrão de cadáveres' AS titulo, 'Patrícia Melo' AS autor, 'Editora Rocco' AS editora, 'Ficcao Brasileira' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788573261301' AS isbn, 'Envie meu dicionário' AS titulo, 'Paulo Leminski' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788571104112' AS isbn, 'Sobre a televisão' AS titulo, 'Pierre Bourdieu' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1997-01-01' AS lancamento
UNION ALL SELECT '9782738112439' AS isbn, 'The Selfish Gene' AS titulo, 'Richard Dawkins' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '1976-01-01' AS lancamento
UNION ALL SELECT '9788467037593' AS isbn, 'The Art of Seduction' AS titulo, 'Robert Greene' AS autor, 'Editora Rocco' AS editora, 'Historia' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9788585061432' AS isbn, 'Carnaval' AS titulo, 'Roberto M. Moura' AS autor, 'Zahar' AS editora, 'Historia' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1986-01-01' AS lancamento
UNION ALL SELECT '9788532511430' AS isbn, 'A dama e o unicórnio' AS titulo, 'Rosiska Darcy de Oliveira' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788571649538' AS isbn, 'Ela é carioca' AS titulo, 'Ruy Castro' AS autor, 'Companhia das Letras' AS editora, 'Biografia e Memorias' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9782266074971' AS isbn, 'The Moor''s Last Sigh' AS titulo, 'Salman Rushdie' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9788535907346' AS isbn, 'Caetana diz não' AS titulo, 'Sandra Lauderdale Graham' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9780307941138' AS isbn, 'The Bluest Eye' AS titulo, 'Toni Morrison' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1970-01-01' AS lancamento
UNION ALL SELECT '9788573260236' AS isbn, 'Urubu e o flamengo' AS titulo, '[HENFIL' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1996-01-01' AS lancamento
UNION ALL SELECT '9788599296547' AS isbn, '501 Grandes Artistas' AS titulo, '0' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788586796777' AS isbn, 'Como proteger seus filhos' AS titulo, '3' AS autor, 'Sextante' AS editora, 'Literatura Infantojuvenil' AS genero, 49.90 AS preco, 'Infantojuvenil' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9781526615862' AS isbn, 'Afterlives' AS titulo, 'Abdulrazak Gurnah' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2020-01-01' AS lancamento
UNION ALL SELECT '9780297869825' AS isbn, 'Give and Take' AS titulo, 'Adam Grant' AS autor, 'Sextante' AS editora, 'Negocios e Economia' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9781781255476' AS isbn, 'The perfect bet' AS titulo, 'Adam Kucharski' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9781617230059' AS isbn, 'Creation' AS titulo, 'Adam Rutherford' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788532514448' AS isbn, 'Vésperas' AS titulo, 'Adriana Lunardi' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9788571109896' AS isbn, 'Ariano Suassuna' AS titulo, 'Adriana Victor' AS autor, 'Zahar' AS editora, 'Biografia e Memorias' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788532530080' AS isbn, 'Ansiedade Corporativa' AS titulo, 'Adriano Silva' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9788532521149' AS isbn, 'Idéias em movimento' AS titulo, 'Aída Marques' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9780192860835' AS isbn, 'The diary of a teenage health freak' AS titulo, 'Aidan Macfarlane' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1987-01-01' AS lancamento
UNION ALL SELECT '9788599296042' AS isbn, 'O Codigo da Vinci Roteiro Ilustrado' AS titulo, 'Akiva Goldsman' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9788532513168' AS isbn, 'As consolações da filosofia' AS titulo, 'Alain De Botton' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9788537800522' AS isbn, 'The evolution of physics' AS titulo, 'Albert Einstein' AS autor, 'Zahar' AS editora, 'Historia' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '1938-01-01' AS lancamento
UNION ALL SELECT '9788573261929' AS isbn, 'Histórias do poder' AS titulo, 'Alberto Dines' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788984070288' AS isbn, 'A history of reading' AS titulo, 'Alberto Manguel' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '1996-01-01' AS lancamento
UNION ALL SELECT '9788573264289' AS isbn, 'Uma noite em cinco atos' AS titulo, 'Alberto Martins' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788433998934' AS isbn, 'Poeta chileno' AS titulo, 'Alejandro Zambra' AS autor, 'Companhia das Letras' AS editora, 'Romance' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '2020-01-01' AS lancamento
UNION ALL SELECT '9788571108011' AS isbn, 'Maquiavel e o Príncipe' AS titulo, 'Alessandro Pinzani' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788537813935' AS isbn, 'Pinoquio No Pais dos Paradoxos' AS titulo, 'Alessio Palmero Aprosio' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9789896572259' AS isbn, 'Alex''s Adventures In Numberland' AS titulo, 'Alex Bellos' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 79.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788575421314' AS isbn, 'A crise dos 25' AS titulo, 'Alexandra Robbins' AS autor, 'Sextante' AS editora, 'Literatura Infantojuvenil' AS genero, 44.90 AS preco, 'Infantojuvenil' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788562500763' AS isbn, 'A cor de Coraline' AS titulo, 'Alexandre Rampazo' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788575421383' AS isbn, 'A incrível viagem de Shackleton' AS titulo, 'Alfred Lansing' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788524905513' AS isbn, 'Meio ambiente no século 21' AS titulo, 'Alfredo Sirkis' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9781417632824' AS isbn, 'In love & trouble' AS titulo, 'Alice Walker' AS autor, 'Editora Rocco' AS editora, 'Contos' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1973-01-01' AS lancamento
UNION ALL SELECT '9788537805954' AS isbn, 'Sem Medo de Errar - Better By Mistake' AS titulo, 'Alina Tugend' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9788586796524' AS isbn, 'Por que os homens fazem sexo e as mulheres fazem amor?' AS titulo, 'Allan Pease' AS autor, 'Sextante' AS editora, 'Autoajuda' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788575426432' AS isbn, 'Nietzsche Para Estressados' AS titulo, 'Allan Percy' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9788535902396' AS isbn, 'Deixe o quarto como está, ou, Estudos para a composição do cansaço' AS titulo, 'Amilcar Bettega' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788537815991' AS isbn, 'Inteligencia Visual' AS titulo, 'Amy E. Herman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788535930672' AS isbn, 'Enterre Seus Mortos' AS titulo, 'Ana Paula Maia' AS autor, 'Companhia das Letras' AS editora, 'Suspense e Misterio' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2018-01-01' AS lancamento
UNION ALL SELECT '9788571107755' AS isbn, 'Mito e psicanálise' AS titulo, 'Ana Vicentini de Azevedo' AS autor, 'Zahar' AS editora, 'Autoajuda' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788571108974' AS isbn, 'Almanaque do samba' AS titulo, 'André Diniz' AS autor, 'Zahar' AS editora, 'Historia' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9789898093738' AS isbn, 'Para uma melhoria da representação política' AS titulo, 'André Freire' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2008-01-01' AS lancamento
UNION ALL SELECT '9788535926248' AS isbn, 'Devagar e Simples' AS titulo, 'André Lara Resende' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9788532526441' AS isbn, 'O caso Laura' AS titulo, 'André Vianco' AS autor, 'Editora Rocco' AS editora, 'Romance' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9786559213481' AS isbn, 'A pediatra' AS titulo, 'Andréa del Fuego' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2021-01-01' AS lancamento
UNION ALL SELECT '9788537809174' AS isbn, 'Leao da Toscana' AS titulo, 'Andres Mcconnon / Aili Mcconnon' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9781481440929' AS isbn, 'Far From the Tree' AS titulo, 'Andrew Solomon' AS autor, 'Companhia das Letras' AS editora, 'Literatura Infantojuvenil' AS genero, 99.90 AS preco, 'Infantojuvenil' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9789728111014' AS isbn, 'O gosto do pecado' AS titulo, 'Anela Mendes de Almeida' AS autor, 'Editora Rocco' AS editora, 'Historia' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1994-01-01' AS lancamento
UNION ALL SELECT '9788535930160' AS isbn, '50 poemas de revolta' AS titulo, 'Angela das Neves' AS autor, 'Companhia das Letras' AS editora, 'Poesia' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788532501165' AS isbn, 'O gosto do pecado' AS titulo, 'Angela Mendes de Almeida' AS autor, 'Editora Rocco' AS editora, 'Historia' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1992-01-01' AS lancamento
UNION ALL SELECT '9788540502512' AS isbn, 'Um útero é do tamanho de um punho' AS titulo, 'Angélica Freitas' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9788585490133' AS isbn, 'Diário De Susie, O' AS titulo, 'Ann Macfarlane Aidan E Mcpherson' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788537819166' AS isbn, 'Querida Kitty - Ediçao bolso de luxo - Um romance epistolar' AS titulo, 'Anne Frank e Karolien van Eck' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788575425138' AS isbn, 'Presente do mar' AS titulo, 'Anne Morrow Lindbergh' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788537807286' AS isbn, 'Historia das Coisas' AS titulo, 'Annie Leonard' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9788573264142' AS isbn, 'Como se caísse devagar' AS titulo, 'Annita Costa Malufe' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2008-01-01' AS lancamento
UNION ALL SELECT '9789898093905' AS isbn, 'O céu não pode esperar' AS titulo, 'António Brito' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9789898093400' AS isbn, 'Olhos de cacador' AS titulo, 'Antonio Britto' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788575420591' AS isbn, 'Diário da cidade amada' AS titulo, 'Antônio Bulhões' AS autor, 'Sextante' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2003-01-01' AS lancamento
UNION ALL SELECT '9788585490058' AS isbn, 'Ficção e confissão' AS titulo, 'Antonio Candido' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1992-01-01' AS lancamento
UNION ALL SELECT '9786555641011' AS isbn, 'Tem Um Livro Aqui Que Voce Vai Gostar' AS titulo, 'Antonio Fagundes' AS autor, 'Sextante' AS editora, 'Ficcao Brasileira' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788573264548' AS isbn, 'Meio intelectual, meio de esquerda' AS titulo, 'Antonio Prata' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788537814710' AS isbn, 'Édipo. Ao Pé da Letra' AS titulo, 'Antonio Quinet' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9788535922004' AS isbn, 'O Erro de Descartes' AS titulo, 'Antonio R. Damasio' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9786555250701' AS isbn, 'Modernidades negras' AS titulo, 'Antônio Sérgio Guimarães' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2021-01-01' AS lancamento
UNION ALL SELECT '9788573265392' AS isbn, 'Inezita Barroso' AS titulo, 'Arley Pereira' AS autor, 'Editora 34' AS editora, 'Biografia e Memorias' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788573260083' AS isbn, 'Brock' AS titulo, 'Arthur Dapieve' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9788586796173' AS isbn, '40 anos de bossa nova' AS titulo, 'Artur da Távola' AS autor, 'Sextante' AS editora, 'Historia' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788571648081' AS isbn, 'O Deus das Pequenas Coisas' AS titulo, 'Arundhati Roy' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9786555250398' AS isbn, 'Teatro legislativo' AS titulo, 'Augusto Boal' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2020-01-01' AS lancamento
UNION ALL SELECT '9788575420386' AS isbn, 'Revolucione sua qualidade de vida' AS titulo, 'Augusto Jorge Cury' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9788532516336' AS isbn, 'Solidão solitude' AS titulo, 'Autran Dourado' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9789896761752' AS isbn, 'Africa see you see me' AS titulo, 'Awam Amkpa' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9788599296837' AS isbn, 'Box A Revolta de Atlas - 3 Volumes' AS titulo, 'Ayn Rand' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 149.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788573262537' AS isbn, 'Adoniran' AS titulo, 'Ayrton Mugnaini Jr.' AS autor, 'Editora 34' AS editora, 'Biografia e Memorias' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9788575422441' AS isbn, 'Como Conquistar As Pessoas (Em Portugues do Brasil)' AS titulo, 'Barbara Pease' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9788537812068' AS isbn, 'Pense No Garfo' AS titulo, 'Bee Wilson' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9780805271775' AS isbn, 'Imagined communities' AS titulo, 'Benedict Anderson' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1983-01-01' AS lancamento
UNION ALL SELECT '9788571106659' AS isbn, 'Heidegger & Ser e Tempo' AS titulo, 'Benedito Nunes' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9788571106611' AS isbn, 'O que deu errado no Oriente Médio?' AS titulo, 'Bernard Lewis' AS autor, 'Zahar' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9788537801192' AS isbn, 'O diário de Bernardina' AS titulo, 'Bernardina Botelho de Magalhães' AS autor, 'Zahar' AS editora, 'Biografia e Memorias' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788575422427' AS isbn, 'Transformando suor em ouro' AS titulo, 'Bernardinho' AS autor, 'Sextante' AS editora, 'Biografia e Memorias' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9788535902877' AS isbn, 'Nove noites' AS titulo, 'Bernardo Carvalho' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9788573261547' AS isbn, 'Bernardo Pereira de Vasconcelos' AS titulo, 'Bernardo Pereira de Vasconcelos' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788571105485' AS isbn, 'A nova sociedade brasileira' AS titulo, 'Bernardo Sorj' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9780143127741' AS isbn, 'The Body Keeps the Score' AS titulo, 'Bessel van der Kolk' AS autor, 'Sextante' AS editora, 'Autoajuda' AS genero, 79.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788571108486' AS isbn, 'O Rio de Janeiro que Hollywood inventou' AS titulo, 'Bianca Freire-Medeiros' AS autor, 'Zahar' AS editora, 'Historia' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9781473508156' AS isbn, 'The Body' AS titulo, 'Bill Bryson' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9781473522312' AS isbn, 'Design your life' AS titulo, 'Bill Burnett' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788401025167' AS isbn, 'How to Avoid a Climate Disaster' AS titulo, 'Bill Gates' AS autor, 'Companhia das Letras' AS editora, 'Negocios e Economia' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788575423158' AS isbn, '1001 Maneiras de Premiar Seus Colaboradores (Em Portugues do Brasil)' AS titulo, 'Bob Nelson' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788543108070' AS isbn, 'Um carinho na alma' AS titulo, 'Bráulio Bessa' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788599296103' AS isbn, 'O Mestre Das Iluminuras' AS titulo, 'Brenda Rickman Vantrease' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9782253093077' AS isbn, 'The skies belong to us' AS titulo, 'Brendan I. Koerner' AS autor, 'Zahar' AS editora, 'Biografia e Memorias' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788575425107' AS isbn, 'Leis Universais do Sucesso' AS titulo, 'Brian Tracy' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788537817353' AS isbn, 'Introducao Clínica a Psicanalise Lacaniana' AS titulo, 'Bruce Fink' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2018-01-01' AS lancamento
UNION ALL SELECT '9788573267273' AS isbn, 'Tudo pronto para o fim do mundo' AS titulo, 'Bruno Brum' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788585490386' AS isbn, 'Jamais fomos modernos' AS titulo, 'Bruno Latour' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788575426982' AS isbn, 'De volta à Cabana' AS titulo, 'C. Baxter Kruger' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9788571647121' AS isbn, 'Verdade tropical' AS titulo, 'Caetano Veloso' AS autor, 'Companhia das Letras' AS editora, 'Biografia e Memorias' AS genero, 84.90 AS preco, 'Adulto' AS classificacao, '1997-01-01' AS lancamento
UNION ALL SELECT '9786559213535' AS isbn, 'Latim em pó' AS titulo, 'Caetano W. Galindo' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2023-01-01' AS lancamento
UNION ALL SELECT '9788535932669' AS isbn, 'Morangos Mofados' AS titulo, 'Caio Fernando Abreu' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1983-01-01' AS lancamento
UNION ALL SELECT '9788573261998' AS isbn, 'José da Silva Lisboa, Visconde de Cairu' AS titulo, 'Cairu, José da Silva Lisboa Visconde de' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9788537815700' AS isbn, 'Segredos da Gravidade' AS titulo, 'Caleb A. Scharf' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788490668061' AS isbn, 'las malas' AS titulo, 'Camila Sosa Villada' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2020-01-01' AS lancamento
UNION ALL SELECT '9780141967523' AS isbn, 'High price' AS titulo, 'Carl L. Hart' AS autor, 'Zahar' AS editora, 'Biografia e Memorias' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788573260762' AS isbn, 'O eterno verão do reggae' AS titulo, 'Carlos Albuquerque' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1997-01-01' AS lancamento
UNION ALL SELECT '9788571105430' AS isbn, 'Os  índios antes do Brasil' AS titulo, 'Carlos Fausto' AS autor, 'Zahar' AS editora, 'Historia' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788573264050' AS isbn, 'Ideologia da cultura brasileira (1933-1974)' AS titulo, 'Carlos Guilherme Mota' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '1980-01-01' AS lancamento
UNION ALL SELECT '9788532528346' AS isbn, 'Clarice Lispector' AS titulo, 'Carlos Mendes de Sousa' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9788535913040' AS isbn, 'Flores azuis' AS titulo, 'Carola Saavedra' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2008-01-01' AS lancamento
UNION ALL SELECT '9788535930702' AS isbn, 'Casa de alvenaria' AS titulo, 'Carolina Maria de Jesus' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2021-01-01' AS lancamento
UNION ALL SELECT '9788566642315' AS isbn, 'La e Aqui' AS titulo, 'Carolina Moreyra / Odilon Moraes' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9788532530943' AS isbn, 'Voce' AS titulo, 'Caroline Kepnes' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2018-01-01' AS lancamento
UNION ALL SELECT '9788532530493' AS isbn, 'Os Fantasmas' AS titulo, 'César Aira' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788573266290' AS isbn, 'Tudo (e mais um pouco)' AS titulo, 'Chacal' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788537810170' AS isbn, 'Sequestro da America' AS titulo, 'Charles Ferguson' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9781786144577' AS isbn, 'Boy, the Mole, the Fox and the Horse' AS titulo, 'Charlie Mackesy' AS autor, 'Sextante' AS editora, 'Autoajuda' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788532513281' AS isbn, 'Se a sucesso e  um jogo, estas sa o as regras' AS titulo, 'Che rie Carter-Scott' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9788573263831' AS isbn, 'Longe de Ramiro' AS titulo, 'Chico Mattoso' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788537814062' AS isbn, 'Crer e Destruir' AS titulo, 'Christian Ingrao' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9781137338105' AS isbn, 'Inside The Writers'' Room' AS titulo, 'Christina Kallas' AS autor, 'Zahar' AS editora, 'Biografia e Memorias' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788566642353' AS isbn, 'Não Derrame o Leite! - Volume 1' AS titulo, 'Christopher Corr Stephen Davies' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9786559212323' AS isbn, 'O pacto da branquitude' AS titulo, 'Cida Bento' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2022-01-01' AS lancamento
UNION ALL SELECT '9788573260465' AS isbn, 'Conversas com economistas brasileiros' AS titulo, 'Ciro Biderman' AS autor, 'Editora 34' AS editora, 'Negocios e Economia' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '1996-01-01' AS lancamento
UNION ALL SELECT '9788571105935' AS isbn, 'Os evangélicos' AS titulo, 'Clara Mafra' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9780761178972' AS isbn, 'Show Your Work!' AS titulo, 'Austin Kleon' AS autor, 'Editora Rocco' AS editora, 'Negocios e Economia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788543109206' AS isbn, 'Mude Seus Horarios Mude Sua Vida' AS titulo, 'Jonathan C. Young' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788573261172' AS isbn, 'Artes plásticas na Semana de 22' AS titulo, 'Aracy A. Amaral' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '1970-01-01' AS lancamento
UNION ALL SELECT '9788537801550' AS isbn, 'O Andar Do Bebado' AS titulo, 'Jonathan C. Young' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9786559210411' AS isbn, 'Pequena coreografia do adeus' AS titulo, 'Aline Bei' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788561384968' AS isbn, 'De amor e amizade' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Literatura Infantojuvenil' AS genero, 39.90 AS preco, 'Infantojuvenil' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788575420164' AS isbn, 'VOCE E INSUBSTITUIVEL - portuguese' AS titulo, 'Augusto Cury' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9788573265866' AS isbn, 'A construção política do Brasil' AS titulo, 'Luiz Carlos Bresser Pereira' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788537818008' AS isbn, 'Como as Democracias Morrem' AS titulo, 'Jonathan C. Young' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2018-01-01' AS lancamento
UNION ALL SELECT '9781416594796' AS isbn, 'The Greatest Show on Earth' AS titulo, 'Richard Dawkins' AS autor, 'Companhia das Letras' AS editora, 'Literatura Religiosa' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788500019531' AS isbn, 'Segredo, O (volume 1)' AS titulo, 'Rhonda Byrne' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9786555641233' AS isbn, 'A Unica Coisa - A verdade surpreendentemente simples por tras de resultados extraordinarios' AS titulo, 'Jonathan C. Young' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788573261653' AS isbn, 'As festas no Brasil Colonial' AS titulo, 'José Ramos Tinhorão' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788537801710' AS isbn, 'Um Estudo em Vermelho - Coleçøo Clássicos Zahar' AS titulo, 'Arthur Conan Doyle' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788535925661' AS isbn, 'Brasil' AS titulo, 'Lilia Moritz Schwarcz' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 94.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9786555320091' AS isbn, 'Todas As Cartas' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 124.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9789871406173' AS isbn, 'O Código da Inteligencia' AS titulo, 'Augusto Cury' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788573262797' AS isbn, 'Desenvolvimento e crise no Brasil' AS titulo, 'Luiz Carlos Bresser Pereira' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1970-01-01' AS lancamento
UNION ALL SELECT '9788537810873' AS isbn, 'Um Estudo em Vermelho' AS titulo, 'Arthur Conan Doyle' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9786050937626' AS isbn, 'Dias Perfeitos' AS titulo, 'Raphael Montes' AS autor, 'Companhia das Letras' AS editora, 'Suspense e Misterio' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788532531803' AS isbn, 'Harry Potter e a Ordem da Fenix - Edicao Comemorativa dos 20 anos da Colecao Harry Potter' AS titulo, 'J. K. Rowling' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788575421499' AS isbn, 'Nunca desista de seus sonhos' AS titulo, 'Augusto Cury' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788573261141' AS isbn, 'Reforma do Estado para a cidadania' AS titulo, 'Luiz Carlos Bresser Pereira' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788571106925' AS isbn, 'O Que Einstein Disse A Seu Cozinheiro - Vol 1 (Em Portugues do Brasil)' AS titulo, 'Jonathan C. Young' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2003-01-01' AS lancamento
UNION ALL SELECT '9788535922233' AS isbn, 'Toda poesia' AS titulo, 'Paulo Leminski' AS autor, 'Companhia das Letras' AS editora, 'Poesia' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788532515360' AS isbn, 'Onze minutos' AS titulo, 'Paulo Coelho' AS autor, 'Editora Rocco' AS editora, 'Ficcao Brasileira' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2003-01-01' AS lancamento
UNION ALL SELECT '9788575420850' AS isbn, 'Pais brilhantes, professores fascinantes' AS titulo, 'Augusto Cury' AS autor, 'Sextante' AS editora, 'Literatura Infantojuvenil' AS genero, 49.90 AS preco, 'Infantojuvenil' AS classificacao, '2003-01-01' AS lancamento
UNION ALL SELECT '9788573262186' AS isbn, 'Cultura popular' AS titulo, 'José Ramos Tinhorão' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9789876128247' AS isbn, 'Diário de Pilar na Grécia' AS titulo, 'Flávia Lins e Silva' AS autor, 'Zahar' AS editora, 'Literatura Infantojuvenil' AS genero, 44.90 AS preco, 'Infantojuvenil' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788571646858' AS isbn, 'A metamorfose' AS titulo, 'Franz Kafka' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788532530172' AS isbn, 'Animais Fantasticos & Onde Habitam' AS titulo, 'J. K. Rowling' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788575421628' AS isbn, 'Futuro da Humanidade : A Saga de Marco Polo' AS titulo, 'Augusto Cury' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9788573264722' AS isbn, 'O Duplo' AS titulo, 'Фёдор Михайлович Достоевский' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788537800669' AS isbn, 'Vida para consumo' AS titulo, 'Zygmunt Bauman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2008-01-01' AS lancamento
UNION ALL SELECT '9788535912883' AS isbn, 'O homem duplicado' AS titulo, 'José Saramago' AS autor, 'Companhia das Letras' AS editora, 'Autoajuda' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2008-01-01' AS lancamento
UNION ALL SELECT '9788532531797' AS isbn, 'Harry Potter e o Calice de Fogo - Edicao Comemorativa dos 20 anos da Colecao Harry Potter' AS titulo, 'J. K. Rowling' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788575422304' AS isbn, 'O mestre dos mestres' AS titulo, 'Augusto Cury' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9788573264098' AS isbn, 'Irmaos Karamazov, Os' AS titulo, 'Jonathan C. Young' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788566642568' AS isbn, 'Diário de Pilar na China' AS titulo, 'Flávia Lins e Silva' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788535930047' AS isbn, 'Guerra e Paz' AS titulo, 'Лев Толстой' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9786075276915' AS isbn, 'The 48 Laws of Power' AS titulo, 'Robert Greene' AS autor, 'Editora Rocco' AS editora, 'Historia' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788575422335' AS isbn, 'Mestre Inesquecivel, O (C. Analise E Inteligencia De Cristo)' AS titulo, 'Augusto Cury' AS autor, 'Sextante' AS editora, 'Autoajuda' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9788573264906' AS isbn, 'A cidade no Brasil' AS titulo, 'Antonio Risério' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9788566642209' AS isbn, 'Diario de Pilar Em Machu Picchu' AS titulo, 'Flávia Lins e Silva' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788571640085' AS isbn, 'O ano da morte de Ricardo Reis' AS titulo, 'José Saramago' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9780356130323' AS isbn, 'The Vampire Lestat' AS titulo, 'Anne Rice' AS autor, 'Editora Rocco' AS editora, 'Literatura Infantojuvenil' AS genero, 89.90 AS preco, 'Infantojuvenil' AS classificacao, '1986-01-01' AS lancamento
UNION ALL SELECT '9788575422564' AS isbn, 'Os Segredos do Pai-nosso - Um Estudo Psicológico da Oração Mais Conhecida do Mundo' AS titulo, 'Augusto Cury' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9788573261387' AS isbn, 'A nação mercantilista' AS titulo, 'Jorge Caldeira' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 69.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788566642414' AS isbn, 'Diário de Pilar na África' AS titulo, 'Flávia Lins e Silva' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9788535928204' AS isbn, 'A Montanha Mágica' AS titulo, 'Thomas Mann' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9782843372650' AS isbn, 'Maktub' AS titulo, 'Paulo Coelho' AS autor, 'Editora Rocco' AS editora, 'Contos' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1994-01-01' AS lancamento
UNION ALL SELECT '9788543106830' AS isbn, 'Como se relacionar bem usando a Comunicacao Nao Violenta' AS titulo, 'Jonathan C. Young' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2018-01-01' AS lancamento
UNION ALL SELECT '9788573266139' AS isbn, 'A Canção no Tempo. 85 Anos de Músicas Brasileiras. 1901-1957 - Volume 1' AS titulo, 'Jairo Severiano' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9788571105980' AS isbn, 'Modernidade li quida' AS titulo, 'Jonathan C. Young' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9781843432326' AS isbn, 'Ensaio sobre a lucidez' AS titulo, 'José Saramago' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788532500984' AS isbn, 'O dom supremo' AS titulo, 'Paulo Coelho' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788575421536' AS isbn, 'Como ser feliz apesar de tudo' AS titulo, 'Hugh Prather' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9788521300922' AS isbn, 'Arte e meio artístico' AS titulo, 'Aracy A. Amaral' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '1982-01-01' AS lancamento
UNION ALL SELECT '9788566642001' AS isbn, 'Detetives do Predio Azul' AS titulo, 'Flávia Lins e Silva' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788571644823' AS isbn, 'O xangô de Baker Street' AS titulo, 'Jô Soares' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9788499184364' AS isbn, 'InterWorld' AS titulo, 'Neil Gaiman' AS autor, 'Editora Rocco' AS editora, 'Literatura Infantojuvenil' AS genero, 49.90 AS preco, 'Infantojuvenil' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788575420560' AS isbn, 'Dez Leis para Ser Feliz - Ferramentas para Se Apaixonar Pela Vida' AS titulo, 'Augusto Cury' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2003-01-01' AS lancamento
UNION ALL SELECT '9788573263046' AS isbn, 'Não falei' AS titulo, 'Beatriz Bracher' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9780307472250' AS isbn, 'Subliminal' AS titulo, 'Leonard Mlodinow' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9788535932195' AS isbn, 'Sobre o Autoritarismo Brasileiro' AS titulo, 'Lilia Moritz Schwarcz' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9782890316089' AS isbn, 'Short stories' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Biografia e Memorias' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1981-01-01' AS lancamento
UNION ALL SELECT '9788543105048' AS isbn, 'Transformando o Sofrimento em Alegria. Construa Relacionamentos Íntimos e Harmoniosos' AS titulo, 'Jonathan C. Young' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788573263220' AS isbn, 'Zaratempô!' AS titulo, 'Evandro Ferreira' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9788537816103' AS isbn, 'Estranhos à nossa porta' AS titulo, 'Zygmunt Bauman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788535925197' AS isbn, 'Correr' AS titulo, 'Drauzio Varella' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9781845764661' AS isbn, 'Stardust' AS titulo, 'Neil Gaiman' AS autor, 'Editora Rocco' AS editora, 'Literatura Infantojuvenil' AS genero, 49.90 AS preco, 'Infantojuvenil' AS classificacao, '1997-01-01' AS lancamento
UNION ALL SELECT '9783257243482' AS isbn, 'Adultério' AS titulo, 'Paulo Coelho' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788573262971' AS isbn, 'Domingos Caldas Barbosa : o poeta da viola, da modinha e do lundu' AS titulo, 'José Ramos Tinhorão' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788537818428' AS isbn, 'O Homem Invisivel - edicao bolso de luxo' AS titulo, 'H. G. Wells' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788571643291' AS isbn, 'O espetáculo das raças' AS titulo, 'Lilia Moritz Schwarcz' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '1993-01-01' AS lancamento
UNION ALL SELECT '9788495825773' AS isbn, 'The Wolves in the Walls' AS titulo, 'Neil Gaiman' AS autor, 'Editora Rocco' AS editora, 'Literatura Infantojuvenil' AS genero, 34.90 AS preco, 'Infantojuvenil' AS classificacao, '2003-01-01' AS lancamento
UNION ALL SELECT '9788543103631' AS isbn, 'Mais forte do que nunca' AS titulo, 'Brené Brown' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788585490362' AS isbn, 'Como enlouquecer um homen--' AS titulo, 'Braulio Tavares' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '1994-01-01' AS lancamento
UNION ALL SELECT '9788537817124' AS isbn, 'Retrotopia' AS titulo, 'Zygmunt Bauman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788535919530' AS isbn, 'Clara dos Anjos' AS titulo, 'Lima Barreto' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9786555320206' AS isbn, 'Um sopro de vida' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Ficcao Brasileira' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1974-01-01' AS lancamento
UNION ALL SELECT '9788586796975' AS isbn, 'Querida mamãe' AS titulo, 'Bradley Trevor Greive' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9788573260724' AS isbn, 'Música popular' AS titulo, 'José Ramos Tinhorão' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1966-01-01' AS lancamento
UNION ALL SELECT '9788566642643' AS isbn, 'Diário De Pilar Na Amazônia' AS titulo, 'Flávia Lins e Silva' AS autor, 'Zahar' AS editora, 'Literatura Infantojuvenil' AS genero, 39.90 AS preco, 'Infantojuvenil' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788571648371' AS isbn, 'As barbas do imperador' AS titulo, 'Lilia Moritz Schwarcz' AS autor, 'Companhia das Letras' AS editora, 'Biografia e Memorias' AS genero, 94.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9789727116799' AS isbn, 'O Zahir' AS titulo, 'Paulo Coelho' AS autor, 'Editora Rocco' AS editora, 'Ficcao Brasileira' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9786555647297' AS isbn, 'Nunca é hora de parar' AS titulo, 'David Goggins' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2023-01-01' AS lancamento
UNION ALL SELECT '9780061918278' AS isbn, 'Семейное счастие' AS titulo, 'Лев Толстой' AS autor, 'Editora 34' AS editora, 'Biografia e Memorias' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1973-01-01' AS lancamento
UNION ALL SELECT '9788571103894' AS isbn, 'The Seminar of Jacques Lacan: Book I' AS titulo, 'Jacques Lacan' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '1986-01-01' AS lancamento
UNION ALL SELECT '9788535906097' AS isbn, 'Olhai os lírios do campo' AS titulo, 'Erico Veríssimo' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1960-01-01' AS lancamento
UNION ALL SELECT '9788579803833' AS isbn, 'Fala Serio Mae (Em Portugues do Brasil)' AS titulo, 'Thalita Reboucas' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788543107455' AS isbn, 'A Riqueza da Vida Simples' AS titulo, 'Gustavo Cerbasi' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788585490027' AS isbn, 'Qu''est-ce que la philosophie?' AS titulo, 'Gilles Deleuze' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1991-01-01' AS lancamento
UNION ALL SELECT '9788571104389' AS isbn, 'Cultura' AS titulo, 'Jonathan C. Young' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788535918724' AS isbn, 'O Eu e o Id. "Autobiografia" e Outros Textos. 1923-1925' AS titulo, 'Sigmund Freud' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9789727110100' AS isbn, 'O diário de um mago' AS titulo, 'Paulo Coelho' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1986-01-01' AS lancamento
UNION ALL SELECT '9786555642421' AS isbn, 'Pra Vida Toda Valer a Pena Viver' AS titulo, 'Ana Claudia Quintana Arantes' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2021-01-01' AS lancamento
UNION ALL SELECT '9788573262278' AS isbn, 'O azul do filho morto' AS titulo, 'Marcelo Mirisola' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9788537810323' AS isbn, 'Sobre Educacao e Juventude' AS titulo, 'Zygmunt Bauman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788535921694' AS isbn, 'Carcereiros' AS titulo, 'Drauzio Varella' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9788532510693' AS isbn, 'InvenÃ§Ã£o e memÃ³ria' AS titulo, 'Lygia Fagundes Telles' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788575422434' AS isbn, 'Eliminando O Estresse' AS titulo, 'Brian Weiss' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9788573261721' AS isbn, 'O herói devolvido' AS titulo, 'Marcelo Mirisola' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788537813584' AS isbn, 'Notre-Dame de Paris' AS titulo, 'Victor Hugo' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9782267023831' AS isbn, 'Home' AS titulo, 'Toni Morrison' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9788532530240' AS isbn, 'Todos os contos' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 89.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788586796500' AS isbn, 'Muitas Vidas - Muitos Mestres' AS titulo, 'Brian Weiss' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788573260038' AS isbn, 'O sujeito ao lado' AS titulo, 'Carlos Eduardo de Magalhães' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9788537807255' AS isbn, 'Aventuras de Sherlock Holmes' AS titulo, 'Arthur Conan Doyle' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9788535911947' AS isbn, 'Billions and billions' AS titulo, 'Carl Sagan' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '1997-01-01' AS lancamento
UNION ALL SELECT '9780316369152' AS isbn, 'Very good lives' AS titulo, 'J. K. Rowling' AS autor, 'Editora Rocco' AS editora, 'Autoajuda' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9788575429716' AS isbn, 'Nate da Um Show (Em Portugues do Brasil)' AS titulo, 'Lincoln Peirce' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788573263275' AS isbn, 'Os sons que vêm da rua' AS titulo, 'José Ramos Tinhorão' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9788537817810' AS isbn, 'Nascidos em Tempos Liquidos' AS titulo, 'Zygmunt Bauman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2018-01-01' AS lancamento
UNION ALL SELECT '9788580860351' AS isbn, 'O continente vol. 1' AS titulo, 'Erico Veríssimo' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788579800511' AS isbn, 'Ela Disse, Ele Disse' AS titulo, 'Thalita Rebouças' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788586796333' AS isbn, 'Oração de São Francisco' AS titulo, 'Leonardo Boff' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788573263824' AS isbn, 'Música nas veias' AS titulo, 'Zuza Homem de Mello' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 64.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788537801185' AS isbn, 'A arte da vida' AS titulo, 'Zygmunt Bauman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788535901801' AS isbn, 'Uma janela em Copacabana' AS titulo, 'L. A. García-Roza' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2001-01-01' AS lancamento
UNION ALL SELECT '9780062194817' AS isbn, 'The Graveyard Book (Graphic Novel), Volume 1' AS titulo, 'Neil Gaiman' AS autor, 'Editora Rocco' AS editora, 'Literatura Infantojuvenil' AS genero, 44.90 AS preco, 'Infantojuvenil' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788543104355' AS isbn, 'O homem mais inteligente da história' AS titulo, 'Augusto Cury' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788573265989' AS isbn, 'O samba agora vai ..' AS titulo, 'José Ramos Tinhorão' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1969-01-01' AS lancamento
UNION ALL SELECT '9780241965993' AS isbn, 'What are you looking at?' AS titulo, 'Will Gompertz' AS autor, 'Zahar' AS editora, 'Historia' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9788535930146' AS isbn, 'O livro de Jô' AS titulo, 'Jô Soares' AS autor, 'Companhia das Letras' AS editora, 'Biografia e Memorias' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9788532508515' AS isbn, 'Gargantas abertas' AS titulo, 'Marina Colasanti' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788575425619' AS isbn, 'Filho do Hamas' AS titulo, 'Mosab Hassan Yousef' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788573267419' AS isbn, 'Trabalho e vadiagem' AS titulo, 'Lúcio Kowarick' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1987-01-01' AS lancamento
UNION ALL SELECT '9788571104952' AS isbn, 'Globalização' AS titulo, 'Zygmunt Bauman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788535932416' AS isbn, 'Ideias para Adiar o Fim do Mundo' AS titulo, 'Ailton Krenak' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788532504821' AS isbn, 'Em liberdade' AS titulo, 'Silviano Santiago' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1981-01-01' AS lancamento
UNION ALL SELECT '9786555643817' AS isbn, 'Filosofia - Um guia grafico (Em Portugues do Brasil)' AS titulo, 'Jonathan C. Young' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788573266375' AS isbn, 'Uma História Desagradável' AS titulo, 'Jonathan C. Young' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788537801222' AS isbn, 'Confiança e medo na cidade' AS titulo, 'Zygmunt Bauman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9780857523488' AS isbn, 'The Heart''s Invisible Furies' AS titulo, 'John Boyne' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 94.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9786555322262' AS isbn, 'Quase de verdade' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 29.90 AS preco, 'Adulto' AS classificacao, '1978-01-01' AS lancamento
UNION ALL SELECT '9788543104447' AS isbn, 'Flor do Dia. Mensagens de Amor e Autoconhecimento' AS titulo, 'Jonathan C. Young' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788573261868' AS isbn, 'Crocodilo, O' AS titulo, 'Jonathan C. Young' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788537800485' AS isbn, 'Medo líquido' AS titulo, 'Zygmunt Bauman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2008-01-01' AS lancamento
UNION ALL SELECT '9788535929041' AS isbn, 'Prisioneiras' AS titulo, 'Drauzio Varella' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2017-01-01' AS lancamento
UNION ALL SELECT '9786555323863' AS isbn, 'The Ballad of Songbirds and Snakes' AS titulo, 'Suzanne Collins' AS autor, 'Editora Rocco' AS editora, 'Literatura Infantojuvenil' AS genero, 79.90 AS preco, 'Infantojuvenil' AS classificacao, '2020-01-01' AS lancamento
UNION ALL SELECT '9788586796258' AS isbn, 'A Divina Sabedoria Dos Mestres' AS titulo, 'Brian Weiss' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788573265552' AS isbn, 'Música com Z' AS titulo, 'Zuza Homem de Mello' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 79.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788571108394' AS isbn, 'Como o Futebol Explica o Mundo' AS titulo, 'Jonathan C. Young' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9788571645660' AS isbn, 'Desmundo' AS titulo, 'Ana Maria Miranda' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1996-01-01' AS lancamento
UNION ALL SELECT '9788532501196' AS isbn, 'As valkírias' AS titulo, 'Paulo Coelho' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1992-01-01' AS lancamento
UNION ALL SELECT '9789800728246' AS isbn, 'Venezuela invadida' AS titulo, 'Pedro N. Miranda M.' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9788573260069' AS isbn, 'O manuscrito de Mediavilla' AS titulo, 'Isaias Pessotti' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9788537814536' AS isbn, 'O Lobo do Mar' AS titulo, 'Jonathan C. Young' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1900-01-01' AS lancamento
UNION ALL SELECT '9788571649941' AS isbn, 'O livro das religiões' AS titulo, 'Jostein Gaarder' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788532520456' AS isbn, 'Correio feminino' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9788575422311' AS isbn, 'Analise da Inteligencia de Cristo Vol. 3 : O Mestre da Vida' AS titulo, 'Jonathan C. Young' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2006-01-01' AS lancamento
UNION ALL SELECT '9788573263992' AS isbn, 'A invenção do mundo pelo Deus-curumim' AS titulo, 'Braulio Tavares' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2008-01-01' AS lancamento
UNION ALL SELECT '9788537817407' AS isbn, 'Economia. O que E Para que Serve Como Funciona' AS titulo, 'Charles J. Wheelan' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2018-01-01' AS lancamento
UNION ALL SELECT '9780345409461' AS isbn, 'The Demon-Haunted World' AS titulo, 'Carl Sagan' AS autor, 'Companhia das Letras' AS editora, 'Autoajuda' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9788532528025' AS isbn, 'Clarice na cabeceira' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2012-01-01' AS lancamento
UNION ALL SELECT '9788575422656' AS isbn, 'Querida Mamae - Edicao de Bolso' AS titulo, 'Bradley Trevor Greive' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788573264401' AS isbn, 'A música popular que surge na Era da Revolução' AS titulo, 'José Ramos Tinhorão' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788537815717' AS isbn, 'Sigmund Freud na Sua Época e em Nosso Tempo' AS titulo, 'Elisabeth Roudinesco' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2016-01-01' AS lancamento
UNION ALL SELECT '9788535932218' AS isbn, 'Freud - A interpretacao dos sonhos 1900 - Obras completas volume 4' AS titulo, 'Sigmund Freud' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2019-01-01' AS lancamento
UNION ALL SELECT '9788532518989' AS isbn, 'Aprendendo a viver' AS titulo, 'Clarice Lispector' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9788575421079' AS isbn, 'O Senhor é meu pastor' AS titulo, 'Leonardo Boff' AS autor, 'Sextante' AS editora, 'Literatura Religiosa' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788573265323' AS isbn, 'Entre a literatura e a história' AS titulo, 'Alfredo Bosi' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '2013-01-01' AS lancamento
UNION ALL SELECT '9788571100817' AS isbn, 'A trilogia tebana' AS titulo, 'Sófocles' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1990-01-01' AS lancamento
UNION ALL SELECT '9788535905342' AS isbn, 'Por Um Fio' AS titulo, 'Drauzio Varella' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9788532514004' AS isbn, 'Durante aquele estranho chá' AS titulo, 'Lygia Fagundes Telles' AS autor, 'Editora Rocco' AS editora, 'Biografia e Memorias' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2002-01-01' AS lancamento
UNION ALL SELECT '9788586796685' AS isbn, 'Tempo de transcendência' AS titulo, 'Leonardo Boff' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '2000-01-01' AS lancamento
UNION ALL SELECT '9788573260243' AS isbn, 'Ossos de borboleta' AS titulo, 'Régis R. Bonvicino' AS autor, 'Editora 34' AS editora, 'Ficcao Geral' AS genero, 39.90 AS preco, 'Adulto' AS classificacao, '1996-01-01' AS lancamento
UNION ALL SELECT '9788571105201' AS isbn, 'Textos Básicos De Filosofia' AS titulo, 'Danilo Marcondes' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788535917369' AS isbn, 'O alufá Rufino' AS titulo, 'João José Reis' AS autor, 'Companhia das Letras' AS editora, 'Biografia e Memorias' AS genero, 74.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9786555320503' AS isbn, 'Você sabe com quem está falando?' AS titulo, 'Roberto da Matta' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2020-01-01' AS lancamento
UNION ALL SELECT '9788575424551' AS isbn, 'E Gol! (Em Portugues do Brasil)' AS titulo, 'Rufus Butler Seder' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788573260120' AS isbn, 'Vida e mimesis' AS titulo, 'Luiz Costa Lima' AS autor, 'Editora 34' AS editora, 'Historia' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '1995-01-01' AS lancamento
UNION ALL SELECT '9788537802434' AS isbn, 'Como Ler Lacan' AS titulo, 'Slavoj Žižek' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788571645967' AS isbn, 'Liberdade por um fio' AS titulo, 'João José Reis' AS autor, 'Companhia das Letras' AS editora, 'Historia' AS genero, 79.90 AS preco, 'Adulto' AS classificacao, '1996-01-01' AS lancamento
UNION ALL SELECT '9788532508478' AS isbn, 'Lábia' AS titulo, 'Waly Salomão' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 34.90 AS preco, 'Adulto' AS classificacao, '1998-01-01' AS lancamento
UNION ALL SELECT '9788575423400' AS isbn, 'GALOPE!' AS titulo, 'RUFUS BUTLER SEDER' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788573260342' AS isbn, 'Vida do viajante' AS titulo, 'Dreyfus, Dominique.' AS autor, 'Editora 34' AS editora, 'Biografia e Memorias' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '1996-01-01' AS lancamento
UNION ALL SELECT '9788537811566' AS isbn, 'Vigilancia Liquida' AS titulo, 'Zygmunt Bauman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2014-01-01' AS lancamento
UNION ALL SELECT '9788571644038' AS isbn, 'Cidade partida' AS titulo, 'Zuenir Ventura' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '1994-01-01' AS lancamento
UNION ALL SELECT '9788532517906' AS isbn, 'No princípio, era a roda' AS titulo, 'Roberto M. Moura' AS autor, 'Editora Rocco' AS editora, 'Historia' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2004-01-01' AS lancamento
UNION ALL SELECT '9789896760113' AS isbn, 'João na terra do jaze' AS titulo, 'José Duarte' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2010-01-01' AS lancamento
UNION ALL SELECT '9788573261561' AS isbn, 'Sepultura' AS titulo, 'André Barcinski' AS autor, 'Editora 34' AS editora, 'Biografia e Memorias' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1999-01-01' AS lancamento
UNION ALL SELECT '9788537801178' AS isbn, 'Mania de matemática 2' AS titulo, 'Ian Stewart' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2009-01-01' AS lancamento
UNION ALL SELECT '9788571643956' AS isbn, 'O selvagem da ópera' AS titulo, 'Rubem Fonseca' AS autor, 'Companhia das Letras' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '1994-01-01' AS lancamento
UNION ALL SELECT '9788532512949' AS isbn, 'Coleção Harry Potter' AS titulo, 'J. K. Rowling' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2015-01-01' AS lancamento
UNION ALL SELECT '9788575421819' AS isbn, 'Etica da vida' AS titulo, 'Leonardo Boff' AS autor, 'Sextante' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9788573263312' AS isbn, 'Contando histórias em versos' AS titulo, 'Braulio Tavares' AS autor, 'Editora 34' AS editora, 'Poesia' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9788537806814' AS isbn, '44 Cartas do Mundo Liquido Moderno - 44 Letters Fr' AS titulo, 'Zygmunt Bauman' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9786559212927' AS isbn, 'Lula' AS titulo, 'Fernando Morais' AS autor, 'Companhia das Letras' AS editora, 'Biografia e Memorias' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2021-01-01' AS lancamento
UNION ALL SELECT '9788532518309' AS isbn, 'Meus contos esquecidos' AS titulo, 'Lygia Fagundes Telles' AS autor, 'Editora Rocco' AS editora, 'Ficcao Geral' AS genero, 54.90 AS preco, 'Adulto' AS classificacao, '2005-01-01' AS lancamento
UNION ALL SELECT '9789896760472' AS isbn, 'Jazzé e outras músicas' AS titulo, 'José Duarte' AS autor, 'Sextante' AS editora, 'Historia' AS genero, 44.90 AS preco, 'Adulto' AS classificacao, '2011-01-01' AS lancamento
UNION ALL SELECT '9788573263756' AS isbn, 'Macroeconomia da estagnação' AS titulo, 'Luiz Carlos Bresser Pereira' AS autor, 'Editora 34' AS editora, 'Negocios e Economia' AS genero, 59.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
UNION ALL SELECT '9788571109674' AS isbn, 'Textos básicos de ética' AS titulo, 'Danilo Marcondes' AS autor, 'Zahar' AS editora, 'Ficcao Geral' AS genero, 49.90 AS preco, 'Adulto' AS classificacao, '2007-01-01' AS lancamento
) AS d
INNER JOIN autor a ON a.nome = d.autor
INNER JOIN editora e ON e.nome = d.editora
INNER JOIN genero g ON g.nome = d.genero
WHERE NOT EXISTS (SELECT 1 FROM livro li WHERE li.isbn = d.isbn);

-- 500 posições de estoque, todas para a única loja; quantidades variam por título.
INSERT INTO estoque (id_loja, id_livro, quantidade)
SELECT l.id_loja, li.id_livro, d.quantidade
FROM (
SELECT '9786555320350' AS isbn, 6 AS quantidade
UNION ALL SELECT '9786555323948' AS isbn, 24 AS quantidade
UNION ALL SELECT '9786555322132' AS isbn, 19 AS quantidade
UNION ALL SELECT '9786555326758' AS isbn, 25 AS quantidade
UNION ALL SELECT '9788532531735' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788532511010' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788532531780' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788535933390' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788535939378' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788535911695' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788575425770' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788532502018' AS isbn, 13 AS quantidade
UNION ALL SELECT '9781564787842' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788374809290' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788532522580' AS isbn, 8 AS quantidade
UNION ALL SELECT '9789500421683' AS isbn, 2 AS quantidade
UNION ALL SELECT '9780385521109' AS isbn, 6 AS quantidade
UNION ALL SELECT '9780702340581' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788532511409' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788571640245' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788532524867' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788498722321' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788573261004' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788573260182' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788575422137' AS isbn, 9 AS quantidade
UNION ALL SELECT '9783426227367' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788532505927' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788532522412' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788532510280' AS isbn, 3 AS quantidade
UNION ALL SELECT '9788532513496' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788532520630' AS isbn, 4 AS quantidade
UNION ALL SELECT '9780434829651' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788532517913' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788532506153' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788532521507' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788532520562' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788532528421' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788532508768' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788535907254' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788571648975' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788535918717' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788571640320' AS isbn, 15 AS quantidade
UNION ALL SELECT '9781531888121' AS isbn, 25 AS quantidade
UNION ALL SELECT '9788575429969' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788535905977' AS isbn, 10 AS quantidade
UNION ALL SELECT '9789722324045' AS isbn, 3 AS quantidade
UNION ALL SELECT '9788535912869' AS isbn, 17 AS quantidade
UNION ALL SELECT '9781716994982' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788535933697' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788535900743' AS isbn, 18 AS quantidade
UNION ALL SELECT '9789896651275' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788535922837' AS isbn, 18 AS quantidade
UNION ALL SELECT '9789722008143' AS isbn, 25 AS quantidade
UNION ALL SELECT '9789024796052' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788537812013' AS isbn, 6 AS quantidade
UNION ALL SELECT '9788571649378' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788571646506' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788535904482' AS isbn, 18 AS quantidade
UNION ALL SELECT '9781250442413' AS isbn, 17 AS quantidade
UNION ALL SELECT '9780062109385' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788571646537' AS isbn, 15 AS quantidade
UNION ALL SELECT '9781447264057' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788535900200' AS isbn, 16 AS quantidade
UNION ALL SELECT '9781802061574' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788571109933' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788566642346' AS isbn, 6 AS quantidade
UNION ALL SELECT '9788573263596' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788537814635' AS isbn, 22 AS quantidade
UNION ALL SELECT '9781846685323' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788571104051' AS isbn, 18 AS quantidade
UNION ALL SELECT '9780141026473' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788571106123' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788537810286' AS isbn, 4 AS quantidade
UNION ALL SELECT '9783103973341' AS isbn, 25 AS quantidade
UNION ALL SELECT '9788571104433' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788537813522' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788430617616' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788571106024' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788537817360' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788571104518' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788537817650' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788537802618' AS isbn, 6 AS quantidade
UNION ALL SELECT '9788537815199' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788537810880' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788537802021' AS isbn, 6 AS quantidade
UNION ALL SELECT '9788571105348' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788537810750' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788537815861' AS isbn, 8 AS quantidade
UNION ALL SELECT '9780385408585' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788537815120' AS isbn, 13 AS quantidade
UNION ALL SELECT '9786056355929' AS isbn, 3 AS quantidade
UNION ALL SELECT '9788571105409' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788571100039' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788573260946' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788573262827' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788511090031' AS isbn, 6 AS quantidade
UNION ALL SELECT '9788573265309' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788573266801' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788573263282' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788573261639' AS isbn, 6 AS quantidade
UNION ALL SELECT '9788573262896' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788573263855' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788573263640' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788573263961' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788573262728' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788573260175' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788508029198' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788573260397' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788573260403' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788543108667' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788573264425' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788573264135' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788573264302' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788573262605' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788573263886' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788573265736' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788585490379' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788573266689' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788573261417' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788573261424' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788575421215' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788586796852' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788586796036' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788575426456' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788575425640' AS isbn, 17 AS quantidade
UNION ALL SELECT '9789898093882' AS isbn, 2 AS quantidade
UNION ALL SELECT '9781850789857' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788543103167' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788575420478' AS isbn, 24 AS quantidade
UNION ALL SELECT '9789800738252' AS isbn, 5 AS quantidade
UNION ALL SELECT '9786555646139' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788543100135' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788544104408' AS isbn, 14 AS quantidade
UNION ALL SELECT '9781592408412' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788575429846' AS isbn, 25 AS quantidade
UNION ALL SELECT '9788575420201' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788599296851' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788543101811' AS isbn, 20 AS quantidade
UNION ALL SELECT '9788543101958' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788575420706' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788575420188' AS isbn, 22 AS quantidade
UNION ALL SELECT '9789720071415' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788575420225' AS isbn, 8 AS quantidade
UNION ALL SELECT '9781607747307' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788575422748' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788532511911' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788569020233' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788571642829' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788573260922' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788573260816' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788532509147' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788573262223' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788535914115' AS isbn, 4 AS quantidade
UNION ALL SELECT '9780008115289' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788532508546' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788571644519' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788573261486' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788571644687' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788535923599' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788571108769' AS isbn, 25 AS quantidade
UNION ALL SELECT '9788573262735' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788573262766' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788573263008' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788535928846' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788535925265' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788573265187' AS isbn, 6 AS quantidade
UNION ALL SELECT '9788571644755' AS isbn, 25 AS quantidade
UNION ALL SELECT '9788585095048' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788573264944' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788535919189' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788573261042' AS isbn, 3 AS quantidade
UNION ALL SELECT '9788532504142' AS isbn, 3 AS quantidade
UNION ALL SELECT '9788571646773' AS isbn, 22 AS quantidade
UNION ALL SELECT '9780771009433' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788535900132' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788532525604' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788573261301' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788571104112' AS isbn, 5 AS quantidade
UNION ALL SELECT '9782738112439' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788467037593' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788585061432' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788532511430' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788571649538' AS isbn, 9 AS quantidade
UNION ALL SELECT '9782266074971' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788535907346' AS isbn, 20 AS quantidade
UNION ALL SELECT '9780307941138' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788573260236' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788599296547' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788586796777' AS isbn, 20 AS quantidade
UNION ALL SELECT '9781526615862' AS isbn, 9 AS quantidade
UNION ALL SELECT '9780297869825' AS isbn, 9 AS quantidade
UNION ALL SELECT '9781781255476' AS isbn, 16 AS quantidade
UNION ALL SELECT '9781617230059' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788532514448' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788571109896' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788532530080' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788532521149' AS isbn, 16 AS quantidade
UNION ALL SELECT '9780192860835' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788599296042' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788532513168' AS isbn, 20 AS quantidade
UNION ALL SELECT '9788537800522' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788573261929' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788984070288' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788573264289' AS isbn, 20 AS quantidade
UNION ALL SELECT '9788433998934' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788571108011' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788537813935' AS isbn, 23 AS quantidade
UNION ALL SELECT '9789896572259' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788575421314' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788562500763' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788575421383' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788524905513' AS isbn, 7 AS quantidade
UNION ALL SELECT '9781417632824' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788537805954' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788586796524' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788575426432' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788535902396' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788537815991' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788535930672' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788571107755' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788571108974' AS isbn, 23 AS quantidade
UNION ALL SELECT '9789898093738' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788535926248' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788532526441' AS isbn, 6 AS quantidade
UNION ALL SELECT '9786559213481' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788537809174' AS isbn, 13 AS quantidade
UNION ALL SELECT '9781481440929' AS isbn, 6 AS quantidade
UNION ALL SELECT '9789728111014' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788535930160' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788532501165' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788540502512' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788585490133' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788537819166' AS isbn, 20 AS quantidade
UNION ALL SELECT '9788575425138' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788537807286' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788573264142' AS isbn, 6 AS quantidade
UNION ALL SELECT '9789898093905' AS isbn, 19 AS quantidade
UNION ALL SELECT '9789898093400' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788575420591' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788585490058' AS isbn, 12 AS quantidade
UNION ALL SELECT '9786555641011' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788573264548' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788537814710' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788535922004' AS isbn, 20 AS quantidade
UNION ALL SELECT '9786555250701' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788573265392' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788573260083' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788586796173' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788571648081' AS isbn, 20 AS quantidade
UNION ALL SELECT '9786555250398' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788575420386' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788532516336' AS isbn, 20 AS quantidade
UNION ALL SELECT '9789896761752' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788599296837' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788573262537' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788575422441' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788537812068' AS isbn, 12 AS quantidade
UNION ALL SELECT '9780805271775' AS isbn, 25 AS quantidade
UNION ALL SELECT '9788571106659' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788571106611' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788537801192' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788575422427' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788535902877' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788573261547' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788571105485' AS isbn, 23 AS quantidade
UNION ALL SELECT '9780143127741' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788571108486' AS isbn, 13 AS quantidade
UNION ALL SELECT '9781473508156' AS isbn, 22 AS quantidade
UNION ALL SELECT '9781473522312' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788401025167' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788575423158' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788543108070' AS isbn, 6 AS quantidade
UNION ALL SELECT '9788599296103' AS isbn, 5 AS quantidade
UNION ALL SELECT '9782253093077' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788575425107' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788537817353' AS isbn, 3 AS quantidade
UNION ALL SELECT '9788573267273' AS isbn, 20 AS quantidade
UNION ALL SELECT '9788585490386' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788575426982' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788571647121' AS isbn, 5 AS quantidade
UNION ALL SELECT '9786559213535' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788535932669' AS isbn, 6 AS quantidade
UNION ALL SELECT '9788573261998' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788537815700' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788490668061' AS isbn, 16 AS quantidade
UNION ALL SELECT '9780141967523' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788573260762' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788571105430' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788573264050' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788532528346' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788535913040' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788535930702' AS isbn, 20 AS quantidade
UNION ALL SELECT '9788566642315' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788532530943' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788532530493' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788573266290' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788537810170' AS isbn, 7 AS quantidade
UNION ALL SELECT '9781786144577' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788532513281' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788573263831' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788537814062' AS isbn, 16 AS quantidade
UNION ALL SELECT '9781137338105' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788566642353' AS isbn, 19 AS quantidade
UNION ALL SELECT '9786559212323' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788573260465' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788571105935' AS isbn, 25 AS quantidade
UNION ALL SELECT '9780761178972' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788543109206' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788573261172' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788537801550' AS isbn, 20 AS quantidade
UNION ALL SELECT '9786559210411' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788561384968' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788575420164' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788573265866' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788537818008' AS isbn, 22 AS quantidade
UNION ALL SELECT '9781416594796' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788500019531' AS isbn, 20 AS quantidade
UNION ALL SELECT '9786555641233' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788573261653' AS isbn, 6 AS quantidade
UNION ALL SELECT '9788537801710' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788535925661' AS isbn, 23 AS quantidade
UNION ALL SELECT '9786555320091' AS isbn, 15 AS quantidade
UNION ALL SELECT '9789871406173' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788573262797' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788537810873' AS isbn, 17 AS quantidade
UNION ALL SELECT '9786050937626' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788532531803' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788575421499' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788573261141' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788571106925' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788535922233' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788532515360' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788575420850' AS isbn, 3 AS quantidade
UNION ALL SELECT '9788573262186' AS isbn, 23 AS quantidade
UNION ALL SELECT '9789876128247' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788571646858' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788532530172' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788575421628' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788573264722' AS isbn, 6 AS quantidade
UNION ALL SELECT '9788537800669' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788535912883' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788532531797' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788575422304' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788573264098' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788566642568' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788535930047' AS isbn, 7 AS quantidade
UNION ALL SELECT '9786075276915' AS isbn, 3 AS quantidade
UNION ALL SELECT '9788575422335' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788573264906' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788566642209' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788571640085' AS isbn, 9 AS quantidade
UNION ALL SELECT '9780356130323' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788575422564' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788573261387' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788566642414' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788535928204' AS isbn, 3 AS quantidade
UNION ALL SELECT '9782843372650' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788543106830' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788573266139' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788571105980' AS isbn, 13 AS quantidade
UNION ALL SELECT '9781843432326' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788532500984' AS isbn, 3 AS quantidade
UNION ALL SELECT '9788575421536' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788521300922' AS isbn, 3 AS quantidade
UNION ALL SELECT '9788566642001' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788571644823' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788499184364' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788575420560' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788573263046' AS isbn, 23 AS quantidade
UNION ALL SELECT '9780307472250' AS isbn, 25 AS quantidade
UNION ALL SELECT '9788535932195' AS isbn, 13 AS quantidade
UNION ALL SELECT '9782890316089' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788543105048' AS isbn, 3 AS quantidade
UNION ALL SELECT '9788573263220' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788537816103' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788535925197' AS isbn, 23 AS quantidade
UNION ALL SELECT '9781845764661' AS isbn, 15 AS quantidade
UNION ALL SELECT '9783257243482' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788573262971' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788537818428' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788571643291' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788495825773' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788543103631' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788585490362' AS isbn, 25 AS quantidade
UNION ALL SELECT '9788537817124' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788535919530' AS isbn, 21 AS quantidade
UNION ALL SELECT '9786555320206' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788586796975' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788573260724' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788566642643' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788571648371' AS isbn, 17 AS quantidade
UNION ALL SELECT '9789727116799' AS isbn, 12 AS quantidade
UNION ALL SELECT '9786555647297' AS isbn, 15 AS quantidade
UNION ALL SELECT '9780061918278' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788571103894' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788535906097' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788579803833' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788543107455' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788585490027' AS isbn, 25 AS quantidade
UNION ALL SELECT '9788571104389' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788535918724' AS isbn, 20 AS quantidade
UNION ALL SELECT '9789727110100' AS isbn, 8 AS quantidade
UNION ALL SELECT '9786555642421' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788573262278' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788537810323' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788535921694' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788532510693' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788575422434' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788573261721' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788537813584' AS isbn, 16 AS quantidade
UNION ALL SELECT '9782267023831' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788532530240' AS isbn, 25 AS quantidade
UNION ALL SELECT '9788586796500' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788573260038' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788537807255' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788535911947' AS isbn, 7 AS quantidade
UNION ALL SELECT '9780316369152' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788575429716' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788573263275' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788537817810' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788580860351' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788579800511' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788586796333' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788573263824' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788537801185' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788535901801' AS isbn, 2 AS quantidade
UNION ALL SELECT '9780062194817' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788543104355' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788573265989' AS isbn, 19 AS quantidade
UNION ALL SELECT '9780241965993' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788535930146' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788532508515' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788575425619' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788573267419' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788571104952' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788535932416' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788532504821' AS isbn, 22 AS quantidade
UNION ALL SELECT '9786555643817' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788573266375' AS isbn, 3 AS quantidade
UNION ALL SELECT '9788537801222' AS isbn, 18 AS quantidade
UNION ALL SELECT '9780857523488' AS isbn, 22 AS quantidade
UNION ALL SELECT '9786555322262' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788543104447' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788573261868' AS isbn, 20 AS quantidade
UNION ALL SELECT '9788537800485' AS isbn, 24 AS quantidade
UNION ALL SELECT '9788535929041' AS isbn, 18 AS quantidade
UNION ALL SELECT '9786555323863' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788586796258' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788573265552' AS isbn, 21 AS quantidade
UNION ALL SELECT '9788571108394' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788571645660' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788532501196' AS isbn, 13 AS quantidade
UNION ALL SELECT '9789800728246' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788573260069' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788537814536' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788571649941' AS isbn, 2 AS quantidade
UNION ALL SELECT '9788532520456' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788575422311' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788573263992' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788537817407' AS isbn, 23 AS quantidade
UNION ALL SELECT '9780345409461' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788532528025' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788575422656' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788573264401' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788537815717' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788535932218' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788532518989' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788575421079' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788573265323' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788571100817' AS isbn, 15 AS quantidade
UNION ALL SELECT '9788535905342' AS isbn, 10 AS quantidade
UNION ALL SELECT '9788532514004' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788586796685' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788573260243' AS isbn, 23 AS quantidade
UNION ALL SELECT '9788571105201' AS isbn, 7 AS quantidade
UNION ALL SELECT '9788535917369' AS isbn, 22 AS quantidade
UNION ALL SELECT '9786555320503' AS isbn, 11 AS quantidade
UNION ALL SELECT '9788575424551' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788573260120' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788537802434' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788571645967' AS isbn, 9 AS quantidade
UNION ALL SELECT '9788532508478' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788575423400' AS isbn, 14 AS quantidade
UNION ALL SELECT '9788573260342' AS isbn, 4 AS quantidade
UNION ALL SELECT '9788537811566' AS isbn, 22 AS quantidade
UNION ALL SELECT '9788571644038' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788532517906' AS isbn, 8 AS quantidade
UNION ALL SELECT '9789896760113' AS isbn, 12 AS quantidade
UNION ALL SELECT '9788573261561' AS isbn, 19 AS quantidade
UNION ALL SELECT '9788537801178' AS isbn, 17 AS quantidade
UNION ALL SELECT '9788571643956' AS isbn, 13 AS quantidade
UNION ALL SELECT '9788532512949' AS isbn, 16 AS quantidade
UNION ALL SELECT '9788575421819' AS isbn, 18 AS quantidade
UNION ALL SELECT '9788573263312' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788537806814' AS isbn, 20 AS quantidade
UNION ALL SELECT '9786559212927' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788532518309' AS isbn, 4 AS quantidade
UNION ALL SELECT '9789896760472' AS isbn, 5 AS quantidade
UNION ALL SELECT '9788573263756' AS isbn, 8 AS quantidade
UNION ALL SELECT '9788571109674' AS isbn, 3 AS quantidade
) AS d
INNER JOIN loja l ON l.nome = 'Livraria de Demonstracao - Carga'
INNER JOIN livro li ON li.isbn = d.isbn
WHERE NOT EXISTS (SELECT 1 FROM estoque es WHERE es.id_loja = l.id_loja AND es.id_livro = li.id_livro);

COMMIT;
