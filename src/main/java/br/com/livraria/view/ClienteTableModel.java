package br.com.livraria.view;

import br.com.livraria.model.Cliente;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de tabela customizado para exibição dos dados dos clientes na JTable.
 */
public class ClienteTableModel extends AbstractTableModel {

    private final String[] colunas = {
            "ID", "Nome", "CPF", "E-mail", "Telefone", "Endereço", "Ativo", "Histórico de Compras"
    };

    private List<Cliente> clientes = new ArrayList<>();

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes != null ? clientes : new ArrayList<>();
        fireTableDataChanged();
    }

    public Cliente getClienteAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < clientes.size()) {
            return clientes.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return clientes.size();
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
        Cliente c = clientes.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return c.getIdCliente();
            case 1:
                return c.getNome();
            case 2:
                return c.getCpf();
            case 3:
                return c.getEmail();
            case 4:
                return c.getTelefone() != null && !c.getTelefone().isEmpty() ? c.getTelefone() : "-";
            case 5:
                return c.getEndereco() != null ? c.getEndereco().getFormatado() : "-";
            case 6:
                return (c.getAtivo() != null && c.getAtivo()) ? "Sim" : "Não";
            case 7:
                if (c.getQuantidadePedidos() > 0) {
                    return "Possui compras (" + c.getQuantidadePedidos() + " ped.)";
                } else {
                    return "Sem compras";
                }
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) return Integer.class;
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Edição feita através do formulário
    }
}
