```text
████  ███ ████  █     ███  ███  █████ █   █ █████  ███   ███ 
█   █  █  █   █ █      █  █   █   █   █   █ █     █     █   █
████   █  ████  █      █  █   █   █   █████ ████  █     █████
█   █  █  █   █ █      █  █   █   █   █   █ █     █     █   █
████  ███ ████  █████ ███  ███    █   █   █ █████  ███  █   █

              _         _
  __   ___.--'_`.     .'_`--.___  __
 ( _`.'. -   'o` )   ( 'o`   - .`.'_ )
 _\.'_'      _.-'     `-._     `_`./_
( \`. )    //\`         '/\\    ( .'/ )
 \_`-'`---'\\__,        ,__//`---'`-'_/
  \`         `-\         /-'         '/
   `                    '   
```

# Sistema de Livraria

Aplicação desktop em Java para cadastro e consulta de clientes e funcionários de uma livraria. A interface é feita com Swing; a persistência usa JDBC e MySQL.

## Requisitos

- JDK 21
- Apache Maven
- MySQL 8 ou compatível
- NetBeans (opcional)

## Banco de dados

O banco padrão chama-se `livraria`. Para criar uma base nova, execute no MySQL:

```text
BANCO SISTEMA DE LIVRARIA/Sistema de Livraria.sql
```

Esse script cria as tabelas de clientes, lojas, funcionários, livros, estoque, pedidos, itens e pagamentos, além dos relacionamentos entre elas.

Se a base já existia antes do cadastro de funcionários, execute uma vez:

```text
BANCO SISTEMA DE LIVRARIA/atualizar_funcionario.sql
```

A migração adiciona `cargo` e `data_admissao` à tabela `funcionario`; não é necessário executá-la em uma base recém-criada pelo script principal.

As configurações de conexão ficam em `src/main/resources/database.properties`. O padrão atual é `localhost:3307`, banco `livraria`, usuário `root` e senha `root`. Altere usuário, senha, porta ou endereço conforme a instalação local do MySQL.

O cadastro de funcionário exige o ID de uma loja já cadastrada, pois a tabela `funcionario` mantém uma chave estrangeira para `loja`.

## Executar

Pelo NetBeans, abra o projeto Maven, recarregue o projeto se necessário e execute `br.com.livraria.Main`.

Pelo terminal:

```bash
mvn clean compile
mvn exec:java
```

Para gerar o JAR com dependências:

```bash
mvn package -DskipTests
```

## Funcionalidades

### Clientes

O menu superior **Clientes** abre o painel integrado nas abas **Consultar**, **Cadastrar**, **Atualizar** e **Excluir**. A consulta permite buscar por nome ou CPF. O cadastro verifica os dados e a unicidade do CPF; a atualização edita os dados de contato e endereço. A exclusão é bloqueada quando há compras para preservar o histórico.

A tela principal também mantém atalhos para as telas antigas de clientes, o painel integrado e a carga de clientes por CSV (`carga_clientes.csv`).

### Funcionários

O menu superior **Funcionários** abre as abas **Consultar**, **Cadastrar**, **Atualizar** e **Excluir**. A consulta filtra por cargo e situação. O cadastro verifica CPF único e inclui nome, loja, cargo, admissão, telefone e situação. Funcionários associados a vendas não podem ser excluídos fisicamente; podem ser inativados pela aba **Atualizar**.

### Outras telas

O código também contém telas legadas para operações de livros e classes de acesso a dados usadas por essas telas. A janela aberta pelo ponto de entrada atual é a tela principal definida em `br.com.livraria.Main`.

## Organização do código

```text
src/main/java/br/com/livraria/
  Main.java
  controller/       Coordenação das ações de clientes
  model/            Entidades Cliente, Endereco e Funcionario
  model/dao/        Acesso JDBC a clientes e funcionários
  model/service/    Carga de clientes por CSV
  model/validation/ Validações cadastrais
  view/             Painéis integrados de clientes e funcionários

src/main/java/br/com/visao/       Telas Swing legadas e janela principal
src/main/java/br/com/controle/    Entidades usadas pelas telas legadas
src/main/java/br/com/entidade/    Acesso a dados legado
src/test/java/                    Testes automatizados
BANCO SISTEMA DE LIVRARIA/        Esquema, migração e scripts de carga/consulta
```

## Testes

Execute com `mvn test`. Os testes de DAO e carga de clientes precisam de uma conexão MySQL ativa e compatível com `database.properties`; os testes de validação não dependem do banco.
