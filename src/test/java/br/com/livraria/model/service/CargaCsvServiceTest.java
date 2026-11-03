package br.com.livraria.model.service;

import br.com.livraria.model.dao.ClienteDAO;
import br.com.livraria.model.dao.ConnectionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CargaCsvServiceTest {

    @BeforeAll
    public static void setUp() {
        assertTrue(ConnectionFactory.testarConexao());
    }

    @Test
    public void testSepararColunasCsv() {
        String linha = "\"Diego Ribeiro\";\"740.327.153-01\";\"diegoribeiro@gmail.com\";\"2004-04-23\";\"+55 (61) 99987-6542\";\"{ \"\"logradouro\"\": \"\"Rua da Liberdade\"\" }\";\"TRUE\";\"M\";\"Brasil\";\"2021-08-24 19:45:00\";\"[]\"";
        List<String> colunas = CargaCsvService.separarColunasCsv(linha);
        assertEquals(11, colunas.size());
        assertEquals("Diego Ribeiro", colunas.get(0));
        assertEquals("740.327.153-01", colunas.get(1));
        assertEquals("diegoribeiro@gmail.com", colunas.get(2));
        assertEquals("TRUE", colunas.get(6));
        assertTrue(colunas.get(5).contains("Rua da Liberdade"));
    }

    @Test
    public void testExecutarCargaCsvCompleta() throws Exception {
        File arquivo = CargaCsvService.localizarArquivoPadrao();
        assertNotNull(arquivo, "Arquivo carga_clientes.csv deve ser encontrado");
        assertTrue(arquivo.exists());

        CargaCsvService service = new CargaCsvService();
        CargaCsvService.ResultadoCarga resultado = service.executarCarga(arquivo);

        assertEquals(150, resultado.totalLidos, "Deve processar as 150 linhas do arquivo");
        assertTrue(resultado.clientesInseridos + resultado.clientesExistentes >= 150);

        // Verificar se os clientes agora constam no banco
        ClienteDAO dao = new ClienteDAO();
        assertTrue(dao.listarTodos().size() >= 150);
    }
}
