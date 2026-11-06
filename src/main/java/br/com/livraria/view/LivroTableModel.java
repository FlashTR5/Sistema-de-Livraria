package br.com.livraria.view;

import br.com.livraria.model.Livro;

import javax.swing.table.AbstractTableModel;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Modelo de tabela customizado para exibição dos livros na JTable.
 */
public class LivroTableModel extends AbstractTableModel {

    private final String[] colunas = {
            "ID", "Título", "ISBN", "Preço", "Autor", "Editora", "Gênero", "Lançamento", "Vendas", "Estoque", "Status Exclusão"
    };

    private final NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private List<Livro> livros = new ArrayList<>();

    public void setLivros(List<Livro> livros) {
        this.livros = livros != null ? livros : new ArrayList<>();
        fireTableDataChanged();
    }

    public Livro getLivroAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < livros.size()) {
            return livros.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return livros.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Livro l = livros.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> l.getIdLivro();
            case 1 -> l.getNome();
            case 2 -> l.getIsbn() != null ? l.getIsbn() : "-";
            case 3 -> l.getPreco() != null ? formatoMoeda.format(l.getPreco()) : "R$ 0,00";
            case 4 -> l.getNomeAutor() != null ? l.getNomeAutor() : ("Autor #" + l.getIdAutor());
            case 5 -> l.getNomeEditora() != null ? l.getNomeEditora() : ("Editora #" + l.getIdEditora());
            case 6 -> l.getNomeGenero() != null ? l.getNomeGenero() : ("Gênero #" + l.getIdGenero());
            case 7 -> l.getLancamento() != null ? l.getLancamento().format(formatoData) : "-";
            case 8 -> l.getQuantidadeVendas();
            case 9 -> l.getQuantidadeEstoque();
            case 10 -> l.podeExcluir() ? "Liberado" : "Bloqueado (com vínculos)";
            default -> null;
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0, 8, 9 -> Integer.class;
            default -> String.class;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
