package br.com.livraria.model.validation;

import br.com.livraria.model.Livro;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validações de negócio da entidade Livro.
 */
public class LivroValidator {

    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private LivroValidator() {
    }

    /**
     * Remove espaços e hífens e converte em maiúsculo (preserva o dígito X de ISBN-10).
     */
    public static String normalizarIsbn(String isbn) {
        return isbn == null ? "" : isbn.replaceAll("[\\s-]", "").toUpperCase();
    }

    public static void validarNome(String nome) throws ValidationException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidationException("Informe o título/nome do livro.");
        }
        if (nome.trim().length() > 120) {
            throw new ValidationException("O título/nome deve ter no máximo 120 caracteres.");
        }
    }

    public static void validarTitulo(String titulo) throws ValidationException {
        validarNome(titulo);
    }

    public static void validarIsbn(String isbn) throws ValidationException {
        String n = normalizarIsbn(isbn);
        if (!n.matches("\\d{13}") && !n.matches("\\d{9}[\\dX]")) {
            throw new ValidationException("ISBN inválido. Informe 10 ou 13 dígitos numéricos (ex: 9788535902775).");
        }
    }

    public static void validarPreco(BigDecimal preco) throws ValidationException {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("O preço de venda deve ser maior que zero.");
        }
    }

    public static void validarAutor(Integer idAutor) throws ValidationException {
        if (idAutor == null || idAutor <= 0) {
            throw new ValidationException("Selecione o autor do livro.");
        }
    }

    public static void validarEditora(Integer idEditora) throws ValidationException {
        if (idEditora == null || idEditora <= 0) {
            throw new ValidationException("Selecione a editora.");
        }
    }

    public static void validarGenero(Integer idGenero) throws ValidationException {
        if (idGenero == null || idGenero <= 0) {
            throw new ValidationException("Selecione o gênero literário.");
        }
    }

    public static void validarCategoria(int idCategoria) throws ValidationException {
        validarGenero(idCategoria);
    }

    public static void validarLancamento(LocalDate lancamento) throws ValidationException {
        if (lancamento != null) {
            int ano = lancamento.getYear();
            int anoLimite = Year.now().getValue() + 2;
            if (ano < 1450 || ano > anoLimite) {
                throw new ValidationException("Data de lançamento inválida (ano permitido entre 1450 e " + anoLimite + ").");
            }
        }
    }

    public static void validarAno(int ano) throws ValidationException {
        int anoLimite = Year.now().getValue() + 2;
        if (ano < 1450 || ano > anoLimite) {
            throw new ValidationException("Ano de publicação inválido (permitido entre 1450 e " + anoLimite + ").");
        }
    }

    public static void validarParaCriacao(Livro livro) throws ValidationException {
        if (livro == null) {
            throw new ValidationException("Dados do livro não fornecidos.");
        }
        validarNome(livro.getNome());
        validarIsbn(livro.getIsbn());
        validarPreco(livro.getPreco());
        validarAutor(livro.getIdAutor());
        validarEditora(livro.getIdEditora());
        validarGenero(livro.getIdGenero());
        validarLancamento(livro.getLancamento());
    }

    public static void validarParaAtualizacao(Livro livro) throws ValidationException {
        if (livro == null || livro.getIdLivro() == null || livro.getIdLivro() <= 0) {
            throw new ValidationException("ID do livro é obrigatório para atualização.");
        }
        validarNome(livro.getNome());
        validarPreco(livro.getPreco());
        validarAutor(livro.getIdAutor());
        validarEditora(livro.getIdEditora());
        validarGenero(livro.getIdGenero());
        validarLancamento(livro.getLancamento());
    }

    /**
     * Converte texto monetário ("49,90", "R$ 49,90", "49.90") em BigDecimal com 2 casas decimais.
     */
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
            throw new ValidationException("Preço inválido. Use o formato: 49,90");
        }
    }

    public static int parseAno(String texto) throws ValidationException {
        String t = texto == null ? "" : texto.trim();
        if (!t.matches("\\d{4}")) {
            throw new ValidationException("O ano de publicação deve ter 4 dígitos (ex: 2024).");
        }
        return Integer.parseInt(t);
    }

    /**
     * Converte data em texto (formato dd/MM/yyyy ou yyyy-MM-dd ou apenas ano yyyy) para LocalDate.
     */
    public static LocalDate parseDataLancamento(String texto) throws ValidationException {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        String t = texto.trim();
        if (t.matches("\\d{4}")) {
            return LocalDate.of(Integer.parseInt(t), 1, 1);
        }
        try {
            if (t.contains("/")) {
                return LocalDate.parse(t, FORMATO_BR);
            }
            return LocalDate.parse(t);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Data de lançamento inválida. Use o formato dd/mm/aaaa ou aaaa.");
        }
    }
}
