package br.com.controle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EstoqueTest {

    @Test
    public void testStatusEstoqueZerado() {
        Estoque est = new Estoque();
        est.setQuantidadeDisponivel(0);
        assertTrue(est.isEstoqueZerado());
        assertFalse(est.isEstoqueBaixo());
        assertEquals("ESGOTADO", est.getStatusDescricao());
    }

    @Test
    public void testStatusEstoqueBaixo() {
        Estoque est = new Estoque();
        est.setQuantidadeDisponivel(3);
        assertFalse(est.isEstoqueZerado());
        assertTrue(est.isEstoqueBaixo());
        assertEquals("BAIXO (3 un)", est.getStatusDescricao());
    }

    @Test
    public void testStatusEstoqueNormal() {
        Estoque est = new Estoque();
        est.setQuantidadeDisponivel(12);
        assertFalse(est.isEstoqueZerado());
        assertFalse(est.isEstoqueBaixo());
        assertEquals("NORMAL (12 un)", est.getStatusDescricao());
    }
}
