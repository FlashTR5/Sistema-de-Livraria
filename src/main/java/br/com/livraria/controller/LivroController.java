package br.com.livraria.controller;

import br.com.livraria.model.Autor;
import br.com.livraria.model.Editora;
import br.com.livraria.model.Genero;
import br.com.livraria.model.Livro;
import br.com.livraria.model.dao.LivroDAO;
import br.com.livraria.model.validation.LivroValidator;
import br.com.livraria.model.validation.ValidationException;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller responsável por intermediar as ações da View e as regras de negócio / persistência de Livros.
 */
public class LivroController {

    private final LivroDAO livroDAO;

    public LivroController() {
        this.livroDAO = new LivroDAO();
    }

    public LivroController(LivroDAO livroDAO) {
        this.livroDAO = livroDAO;
    }

    /**
     * Operação CREATE: Valida dados e persiste um novo livro.
     */
    public void cadastrarLivro(Livro livro) throws ValidationException, SQLException {
        LivroValidator.validarParaCriacao(livro);
        livroDAO.inserir(livro);
    }

    /**
     * Operação UPDATE: Valida dados e atualiza as informações do livro.
     */
    public void atualizarLivro(Livro livro) throws ValidationException, SQLException {
        LivroValidator.validarParaAtualizacao(livro);
        livroDAO.atualizar(livro);
    }

    /**
     * Operação DELETE: Exclusão permitida APENAS para livros sem vendas e sem estoque.
     */
    public void excluirLivro(int idLivro) throws ValidationException, SQLException {
        if (idLivro <= 0) {
            throw new ValidationException("Selecione um livro válido para exclusão.");
        }
        livroDAO.excluir(idLivro);
    }

    /**
     * Operação READ: Busca livro por ID.
     */
    public Livro buscarLivroPorId(int idLivro) throws SQLException {
        if (idLivro <= 0) {
            return null;
        }
        return livroDAO.buscarPorId(idLivro);
    }

    /**
     * Operação READ: Busca livro por ISBN.
     */
    public Livro buscarLivroPorIsbn(String isbn) throws SQLException {
        return livroDAO.buscarPorIsbn(isbn);
    }

    /**
     * Operação READ: Retorna todos os livros.
     */
    public List<Livro> listarLivros() throws SQLException {
        return livroDAO.listarTodos();
    }

    /**
     * Operação READ: Filtra livros por texto (título/ISBN) e por gênero.
     */
    public List<Livro> filtrarLivros(String termo, Integer idGenero) throws SQLException {
        return livroDAO.listarFiltrado(termo, idGenero);
    }

    public List<Autor> listarAutores() throws SQLException {
        return livroDAO.listarAutores();
    }

    public List<Editora> listarEditoras() throws SQLException {
        return livroDAO.listarEditoras();
    }

    public List<Genero> listarGeneros() throws SQLException {
        return livroDAO.listarGeneros();
    }
}
