package br.com.entidade;

import br.com.controle.Cliente;
import br.com.controle.Estoque;
import br.com.controle.Funcionario;
import br.com.controle.ItemPedido;
import br.com.controle.Livro;
import br.com.controle.Pedido;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ManterPedidoIntegrationTest {

    private static boolean bancoDisponivel = false;
    private static int idClienteTeste = 0;
    private static int idFuncionarioTeste = 0;
    private static int idLivroTeste = 0;

    @BeforeAll
    public static void setUp() {
        try {
            DAO dao = new DAO();
            dao.abrirBanco();
            if (dao.con != null && !dao.con.isClosed()) {
                bancoDisponivel = true;

                try (Statement stmt = dao.con.createStatement()) {
                    // Garante Loja
                    stmt.executeUpdate("INSERT INTO loja (id_loja, nome, email, ativo) " +
                            "SELECT 1, 'Livraria Matriz', 'loja@livraria.com', TRUE " +
                            "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM loja WHERE id_loja = 1)");

                    // Garante Cliente Teste
                    stmt.executeUpdate("INSERT INTO cliente (nome, cpf, email) " +
                            "SELECT 'Cliente Teste Vendas', '999.888.777-66', 'cliente.teste@email.com' " +
                            "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE cpf = '999.888.777-66')");

                    // Garante Funcionário Teste
                    stmt.executeUpdate("INSERT INTO funcionario (id_loja, nome, cpf, email, cargo) " +
                            "SELECT 1, 'Operador Teste Caixa', '888.777.666-55', 'caixa.teste@email.com', 'Operador Caixa' " +
                            "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM funcionario WHERE cpf = '888.777.666-55')");

                    // Garante Gênero e Editora para o Livro Teste
                    stmt.executeUpdate("INSERT INTO genero (nome) SELECT 'Ficcao Teste' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM genero WHERE nome = 'Ficcao Teste')");
                    stmt.executeUpdate("INSERT INTO editora (nome) SELECT 'Editora Teste' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM editora WHERE nome = 'Editora Teste')");
                    stmt.executeUpdate("INSERT INTO autor (nome, email) SELECT 'Autor Teste', 'autor@teste.com' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM autor WHERE nome = 'Autor Teste')");

                    // Garante Livro Teste
                    stmt.executeUpdate("INSERT INTO livro (id_autor, id_editora, id_genero, isbn, nome, titulo, preco) " +
                            "SELECT (SELECT id_autor FROM autor LIMIT 1), " +
                            "       (SELECT id_editora FROM editora LIMIT 1), " +
                            "       (SELECT id_genero FROM genero LIMIT 1), " +
                            "       '9788599999999', 'Livro Teste Vendas ACID', 'Livro Teste Vendas ACID', 99.90 " +
                            "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM livro WHERE isbn = '9788599999999')");

                    // Captura os IDs para o teste
                    var rsCli = stmt.executeQuery("SELECT id_cliente FROM cliente WHERE cpf = '999.888.777-66'");
                    if (rsCli.next()) idClienteTeste = rsCli.getInt(1);

                    var rsFunc = stmt.executeQuery("SELECT id_funcionario FROM funcionario WHERE cpf = '888.777.666-55'");
                    if (rsFunc.next()) idFuncionarioTeste = rsFunc.getInt(1);

                    var rsLivro = stmt.executeQuery("SELECT id_livro FROM livro WHERE isbn = '9788599999999'");
                    if (rsLivro.next()) idLivroTeste = rsLivro.getInt(1);

                    // Garante Estoque inicial de 20 unidades para o Livro Teste
                    stmt.executeUpdate("DELETE FROM estoque WHERE id_livro = " + idLivroTeste);
                    stmt.executeUpdate("INSERT INTO estoque (id_loja, id_livro, quantidade, localizacao_prateleira) " +
                            "VALUES (1, " + idLivroTeste + ", 20, 'Prateleira A-1')");
                }
                dao.fecharBanco();
            }
        } catch (Exception e) {
            bancoDisponivel = false;
        }
    }

    @Test
    public void testTransacaoVendaEstornoEEstoque() throws Exception {
        Assumptions.assumeTrue(bancoDisponivel && idClienteTeste > 0 && idFuncionarioTeste > 0 && idLivroTeste > 0,
                "MySQL offline ou dados de teste indisponíveis - teste pulado.");

        ManterPedido manterPedido = new ManterPedido();
        ManterEstoque manterEstoque = new ManterEstoque();

        // 1. Verifica saldo de estoque pré-venda (deve ser 20)
        Estoque estAntes = new Estoque();
        estAntes.setIdLivro(idLivroTeste);
        manterEstoque.PesquisarRegistro(estAntes);
        assertEquals(20, estAntes.getQuantidadeDisponivel());

        // 2. Realiza a Venda de 5 unidades via ManterPedido.inserir
        Pedido p = new Pedido();
        p.setIdCliente(idClienteTeste);
        p.setIdFuncionario(idFuncionarioTeste);
        p.setIdLoja(1);
        p.setFormaPagamento("PIX");
        p.setNomeFuncionario("Operador Teste Caixa");

        ItemPedido item = new ItemPedido(idLivroTeste, "Livro Teste Vendas ACID", "9788599999999", 5, new BigDecimal("99.90"));
        p.adicionarItem(item);

        manterPedido.inserir(p);
        assertTrue(p.getCodigo() > 0, "Código do pedido gerado deve ser maior que 0");

        // 3. Verifica se o estoque foi baixado para 15 (20 - 5 = 15)
        Estoque estPosVenda = new Estoque();
        estPosVenda.setIdLivro(idLivroTeste);
        manterEstoque.PesquisarRegistro(estPosVenda);
        assertEquals(15, estPosVenda.getQuantidadeDisponivel(), "O estoque físico deve ter sido decrementado automaticamente em 5");

        // 4. Consulta a Venda via Relatório com INNER JOIN
        ArrayList<ManterPedido.LinhaRelatorioVenda> relatorio = manterPedido.pesquisarVendasComDetalhes("Livro Teste Vendas ACID", "CONCLUIDO");
        assertFalse(relatorio.isEmpty(), "Relatório de vendas com INNER JOIN deve retornar a venda registrada");
        ManterPedido.LinhaRelatorioVenda linha = relatorio.get(0);
        assertEquals(p.getCodigo(), linha.idPedido);
        assertEquals(5, linha.quantidade);
        assertEquals("CONCLUIDO", linha.status);
        assertNotNull(linha.nomeCliente);
        assertNotNull(linha.nomeFuncionario);

        // 5. Cancela a Venda e verifica o Estorno no Estoque (15 + 5 = 20)
        manterPedido.cancelarVenda(p.getCodigo());

        Estoque estPosCancelamento = new Estoque();
        estPosCancelamento.setIdLivro(idLivroTeste);
        manterEstoque.PesquisarRegistro(estPosCancelamento);
        assertEquals(20, estPosCancelamento.getQuantidadeDisponivel(), "O estoque deve retornar a 20 após o estorno");

        // 6. Confirma que o status do pedido foi atualizado para CANCELADO
        Pedido pCancelado = new Pedido();
        pCancelado.setCodigo(p.getCodigo());
        manterPedido.PesquisarRegistro(pCancelado);
        assertEquals("CANCELADO", pCancelado.getStatus());
    }
}
