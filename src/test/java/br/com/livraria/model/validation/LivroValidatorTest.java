package br.com.livraria.model.validation;

import br.com.livraria.model.Livro;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LivroValidatorTest {

    @Test
    public void testValidarNomeObrigatorio() {
        Livro l = new Livro(null, "", "9788535902775", new BigDecimal("49.90"), 1, 1, 1, LocalDate.of(2020, 1, 1));
        ValidationException ex = assertThrows(ValidationException.class, () -> LivroValidator.validarParaCriacao(l));
        assertTrue(ex.getMessage().contains("título/nome"));
    }

    @Test
    public void testValidarNomeLimite120() {
        String nomeGigante = "A".repeat(121);
        Livro l = new Livro(null, nomeGigante, "9788535902775", new BigDecimal("49.90"), 1, 1, 1, LocalDate.of(2020, 1, 1));
        ValidationException ex = assertThrows(ValidationException.class, () -> LivroValidator.validarParaCriacao(l));
        assertTrue(ex.getMessage().contains("máximo 120"));
    }

    @Test
    public void testValidarIsbnInvalido() {
        Livro l = new Livro(null, "Livro Teste", "12345", new BigDecimal("49.90"), 1, 1, 1, LocalDate.of(2020, 1, 1));
        ValidationException ex = assertThrows(ValidationException.class, () -> LivroValidator.validarParaCriacao(l));
        assertTrue(ex.getMessage().contains("ISBN inválido"));
    }

    @Test
    public void testValidarPrecoNegativoOuZero() {
        Livro l = new Livro(null, "Livro Teste", "9788535902775", BigDecimal.ZERO, 1, 1, 1, LocalDate.of(2020, 1, 1));
        ValidationException ex = assertThrows(ValidationException.class, () -> LivroValidator.validarParaCriacao(l));
        assertTrue(ex.getMessage().contains("maior que zero"));
    }

    @Test
    public void testValidarAutorObrigatorio() {
        Livro l = new Livro(null, "Livro Teste", "9788535902775", new BigDecimal("49.90"), 0, 1, 1, LocalDate.of(2020, 1, 1));
        ValidationException ex = assertThrows(ValidationException.class, () -> LivroValidator.validarParaCriacao(l));
        assertTrue(ex.getMessage().contains("autor"));
    }

    @Test
    public void testValidarEditoraObrigatoria() {
        Livro l = new Livro(null, "Livro Teste", "9788535902775", new BigDecimal("49.90"), 1, 0, 1, LocalDate.of(2020, 1, 1));
        ValidationException ex = assertThrows(ValidationException.class, () -> LivroValidator.validarParaCriacao(l));
        assertTrue(ex.getMessage().contains("editora"));
    }

    @Test
    public void testValidarGeneroObrigatorio() {
        Livro l = new Livro(null, "Livro Teste", "9788535902775", new BigDecimal("49.90"), 1, 1, 0, LocalDate.of(2020, 1, 1));
        ValidationException ex = assertThrows(ValidationException.class, () -> LivroValidator.validarParaCriacao(l));
        assertTrue(ex.getMessage().contains("gênero"));
    }

    @Test
    public void testParsePrecoFormatoBrasileiro() {
        assertEquals(new BigDecimal("49.90"), LivroValidator.parsePreco("49,90"));
        assertEquals(new BigDecimal("1234.50"), LivroValidator.parsePreco("R$ 1.234,50"));
        assertEquals(new BigDecimal("15.00"), LivroValidator.parsePreco("15"));
    }

    @Test
    public void testParseDataLancamento() {
        LocalDate d1 = LivroValidator.parseDataLancamento("2024");
        assertEquals(LocalDate.of(2024, 1, 1), d1);

        LocalDate d2 = LivroValidator.parseDataLancamento("15/05/2021");
        assertEquals(LocalDate.of(2021, 5, 15), d2);
    }

    @Test
    public void testValidacaoLivroValidoComSucesso() {
        Livro l = new Livro(null, "Dom Casmurro", "9788535902775", new BigDecimal("39.90"), 1, 1, 1, LocalDate.of(1899, 1, 1));
        assertDoesNotThrow(() -> LivroValidator.validarParaCriacao(l));
    }
}
