package br.com.controle;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoTest {

    @Test
    public void testSubtotalItemPedido() {
        ItemPedido item = new ItemPedido();
        item.setIdLivro(1);
        item.setTituloLivro("Clean Code");
        item.setQuantidade(3);
        item.setPrecoUnitario(new BigDecimal("89.90"));

        BigDecimal esperado = new BigDecimal("269.70");
        assertEquals(esperado, item.getSubtotal());
    }

    @Test
    public void testCalculoTotalPedidoMultiplosItens() {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(10);
        pedido.setIdFuncionario(5);
        pedido.setFormaPagamento("PIX");

        ItemPedido item1 = new ItemPedido(1, "Clean Code", "9780132350884", 2, new BigDecimal("80.00"));
        ItemPedido item2 = new ItemPedido(2, "Refactoring", "9780201485677", 1, new BigDecimal("120.50"));

        pedido.adicionarItem(item1);
        pedido.adicionarItem(item2);

        assertEquals(2, pedido.getItens().size());
        assertEquals(3, pedido.getQuantidadeItens()); // 2 + 1 = 3 unidades

        // Total: (2 * 80.00) + (1 * 120.50) = 160.00 + 120.50 = 280.50
        assertEquals(new BigDecimal("280.50"), pedido.getValorTotal());
    }

    @Test
    public void testRemoverItemERecalcularTotal() {
        Pedido pedido = new Pedido();
        ItemPedido item1 = new ItemPedido(1, "Livro A", "111", 1, new BigDecimal("50.00"));
        ItemPedido item2 = new ItemPedido(2, "Livro B", "222", 2, new BigDecimal("25.00"));

        pedido.adicionarItem(item1);
        pedido.adicionarItem(item2);
        assertEquals(new BigDecimal("100.00"), pedido.getValorTotal());

        // Remove item 1
        pedido.removerItem(item1);
        assertEquals(1, pedido.getItens().size());
        assertEquals(2, pedido.getQuantidadeItens());
        assertEquals(new BigDecimal("50.00"), pedido.getValorTotal());
    }

    @Test
    public void testStatusEFormaPagamentoPadrao() {
        Pedido pedido = new Pedido();
        assertEquals("CONCLUIDO", pedido.getStatus());
        assertEquals("DINHEIRO", pedido.getFormaPagamento());

        pedido.setStatus("CANCELADO");
        assertEquals("CANCELADO", pedido.getStatus());
    }
}
