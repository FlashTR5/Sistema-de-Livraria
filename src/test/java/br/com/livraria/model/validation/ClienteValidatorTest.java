package br.com.livraria.model.validation;

import br.com.livraria.model.Cliente;
import br.com.livraria.model.Endereco;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteValidatorTest {

    @Test
    public void testValidarNomeObrigatorio() {
        Cliente c = new Cliente(null, "", "123.456.789-00", "teste@email.com", "(61) 99999-8888", new Endereco());
        assertThrows(ValidationException.class, () -> ClienteValidator.validarParaCriacao(c));
    }

    @Test
    public void testValidarEmailInvalido() {
        Cliente c = new Cliente(null, "Maria Silva", "123.456.789-00", "email_invalido", "(61) 99999-8888", new Endereco());
        assertThrows(ValidationException.class, () -> ClienteValidator.validarParaCriacao(c));
    }

    @Test
    public void testValidarCpfRepetido() {
        assertThrows(ValidationException.class, () -> ClienteValidator.validarCpf("111.111.111-11", false));
    }

    @Test
    public void testValidarCpfDigitosIncorretos() {
        // CPF com formato 11 dígitos mas dígitos verificadores inválidos
        assertThrows(ValidationException.class, () -> ClienteValidator.validarCpf("123.456.789-00", true));
    }

    @Test
    public void testFormatarCpf() {
        assertEquals("123.456.789-01", ClienteValidator.formatarCpf("12345678901"));
        assertEquals("123.456.789-01", ClienteValidator.formatarCpf("123.456.789-01"));
    }
}
