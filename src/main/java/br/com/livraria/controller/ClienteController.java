package br.com.livraria.controller;

import br.com.livraria.model.Cliente;
import br.com.livraria.model.dao.ClienteDAO;
import br.com.livraria.model.service.CargaCsvService;
import br.com.livraria.model.validation.ClienteValidator;
import br.com.livraria.model.validation.ValidationException;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller responsável por intermediar as ações da View e as regras de negócio / persistência do Model.
 */
public class ClienteController {

    private final ClienteDAO clienteDAO;
    private final CargaCsvService cargaCsvService;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
        this.cargaCsvService = new CargaCsvService();
    }

    public ClienteController(ClienteDAO clienteDAO, CargaCsvService cargaCsvService) {
        this.clienteDAO = clienteDAO;
        this.cargaCsvService = cargaCsvService;
    }

    /**
     * Operação CREATE: Valida dados cadastrais e persiste um novo cliente.
     */
    public void cadastrarCliente(Cliente cliente) throws ValidationException, SQLException {
        // Validação das regras de negócio
        ClienteValidator.validarParaCriacao(cliente);

        // Persistência no banco
        clienteDAO.inserir(cliente);
    }

    /**
     * Operação UPDATE: Valida e atualiza endereço, e-mail e telefone de contato de um cliente.
     */
    public void atualizarCliente(Cliente cliente) throws ValidationException, SQLException {
        // Validação das regras de negócio para alteração
        ClienteValidator.validarParaAtualizacao(cliente);

        // Persistência da atualização
        clienteDAO.atualizar(cliente);
    }

    /**
     * Operação DELETE: Exclusão permitida APENAS para clientes sem histórico de compras.
     */
    public void excluirCliente(int idCliente) throws ValidationException, SQLException {
        if (idCliente <= 0) {
            throw new ValidationException("Selecione um cliente válido para exclusão.");
        }

        if (clienteDAO.temHistoricoCompras(idCliente)) {
            int total = clienteDAO.contarPedidosDoCliente(idCliente);
            throw new ValidationException(
                    "Exclusão não permitida!\n\n" +
                    "O cliente possui histórico de compras com " + total + " pedido(s) registrado(s) no sistema.\n" +
                    "Para preservar a integridade referencial e o histórico financeiro da livraria, " +
                    "clientes com pedidos não podem ser deletados."
            );
        }

        clienteDAO.excluir(idCliente);
    }

    /**
     * Operação READ: Lista todos os clientes ou aplica filtro rápido por Nome ou CPF.
     */
    public List<Cliente> buscarClientes(String termo) throws SQLException {
        if (termo == null || termo.trim().isEmpty()) {
            return clienteDAO.listarTodos();
        }
        return clienteDAO.buscarPorNomeOuCpf(termo.trim());
    }

    /**
     * Busca um cliente específico pelo ID.
     */
    public Cliente buscarPorId(int idCliente) throws SQLException {
        return clienteDAO.buscarPorId(idCliente);
    }

    /**
     * Verifica se o cliente possui histórico de compras.
     */
    public boolean temHistoricoCompras(int idCliente) throws SQLException {
        return clienteDAO.temHistoricoCompras(idCliente);
    }

    /**
     * Executa a carga em lote a partir do arquivo CSV carga_clientes.csv.
     */
    public CargaCsvService.ResultadoCarga executarCargaCsv(File arquivoCsv) throws Exception {
        if (arquivoCsv == null) {
            arquivoCsv = CargaCsvService.localizarArquivoPadrao();
        }
        if (arquivoCsv == null || !arquivoCsv.exists()) {
            throw new ValidationException("O arquivo carga_clientes.csv não foi encontrado no projeto.");
        }
        return cargaCsvService.executarCarga(arquivoCsv);
    }
}
