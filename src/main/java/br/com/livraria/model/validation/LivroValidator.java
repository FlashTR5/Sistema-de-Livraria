package br.com.livraria.model.validation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;

/**
 * Validações de negócio do Livro (mesmo papel do ClienteValidator).
 * Todas as falhas lançam ValidationException com a mensagem para o usuário.
 */
public class LivroValidator {

    private LivroValidator() {
    }

    /** Remove espaços e hífens e deixa em maiúsculo (o X do ISBN-10). */
    public static String normalizarIsbn(String isbn) {
        return isbn == null ? "" : isbn.replaceAll("[\\s-]", "").toUpperCase();
    }

    public static void validarTitulo(String titulo) throws ValidationException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new ValidationException("Informe o título do livro.");
        }
        if (titulo.trim().length() > 200) {
            throw new ValidationException("O título deve ter no máximo 200 caracteres.");
        }
    }

    public static void validarIsbn(String isbn) throws ValidationException {
        String n = normalizarIsbn(isbn);
        if (!n.matches("\\d{13}") && !n.matches("\\d{9}[\\dX]")) {
            throw new ValidationException("ISBN inválido. Informe 10 ou 13 dígitos (ex: 9788535902775).");
        }
    }

    public static void validarPreco(BigDecimal preco) throws ValidationException {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("O preço de venda deve ser maior que zero.");
        }
    }

    public static void validarAno(int ano) throws ValidationException {
        if (ano < 1450 || ano > Year.now().getValue() + 1) {
            throw new ValidationException("Ano de publicação inválido.");
        }
    }

    public static void validarEditora(int idEditora) throws ValidationException {
        if (idEditora <= 0) {
            throw new ValidationException("Selecione a editora.");
        }
    }

    public static void validarCategoria(int idCategoria) throws ValidationException {
        if (idCategoria <= 0) {
            throw new ValidationException("Selecione a categoria.");
        }
    }

    /** Converte texto digitado ("49,90", "R$ 1.234,50", "49.90") em BigDecimal com 2 casas. */
    public static BigDecimal parsePreco(String texto) throws ValidationException {
        String t = texto == null ? "" : texto.replace("R$", "").replaceAll("\\s", "");
        if (t.isEmpty()) {
            throw new ValidationException("Informe o preço de venda.");
        }
        if (t.contains(",")) {
            t = t.replace(".", "").replace(",", ".");
        }
        try {
            return new BigDecimal(t).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new ValidationException("Preço inválido. Use o formato 49,90.");
        }
    }

    public static int parseAno(String texto) throws ValidationException {
        String t = texto == null ? "" : texto.trim();
        if (!t.matches("\\d{4}")) {
            throw new ValidationException("O ano de publicação deve ter 4 dígitos (ex: 2019).");
        }
        return Integer.parseInt(t);
    }
}