package br.com.livraria.model.validation;

import br.com.livraria.model.Cliente;

import java.util.regex.Pattern;

/**
 * Validador responsável por aplicar regras de negócio sobre os dados do Cliente.
 */
public class ClienteValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    /**
     * Valida os dados para criação de um novo cliente.
     */
    public static void validarParaCriacao(Cliente cliente) {
        if (cliente == null) {
            throw new ValidationException("Os dados do cliente não podem ser nulos.");
        }
        validarNome(cliente.getNome());
        validarCpf(cliente.getCpf(), true);
        validarEmail(cliente.getEmail());
        validarTelefone(cliente.getTelefone());
    }

    /**
     * Valida os dados para atualização (Update: e-mail, telefone, endereço).
     */
    public static void validarParaAtualizacao(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null || cliente.getIdCliente() <= 0) {
            throw new ValidationException("Cliente inválido para atualização: ID obrigatório.");
        }
        validarNome(cliente.getNome());
        validarEmail(cliente.getEmail());
        validarTelefone(cliente.getTelefone());
        if (cliente.getCpf() != null && !cliente.getCpf().trim().isEmpty()) {
            validarFormatoCpf(cliente.getCpf());
        }
    }

    public static void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidationException("O nome do cliente é obrigatório.");
        }
        if (nome.trim().length() < 3) {
            throw new ValidationException("O nome do cliente deve conter pelo menos 3 caracteres.");
        }
        if (nome.trim().length() > 120) {
            throw new ValidationException("O nome do cliente não pode ultrapassar 120 caracteres.");
        }
    }

    public static void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("O e-mail de contato é obrigatório.");
        }
        String limpo = email.trim();
        if (!EMAIL_PATTERN.matcher(limpo).matches()) {
            throw new ValidationException("O e-mail informado é inválido: '" + email + "'.");
        }
        if (limpo.length() > 300) {
            throw new ValidationException("O e-mail não pode ultrapassar 300 caracteres.");
        }
    }

    public static void validarTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            // Telefone é opcional segundo o schema do banco, mas se preenchido deve ser válido
            return;
        }
        String apenasNumeros = telefone.replaceAll("\\D", "");
        if (apenasNumeros.length() < 10 || apenasNumeros.length() > 14) {
            throw new ValidationException("O telefone informado é inválido. Deve conter DDD e de 8 a 9 dígitos (10 a 11 números).");
        }
    }

    /**
     * Valida o CPF. Se estrito for true, valida os dígitos verificadores (módulo 11).
     * Caso o CPF seja um dos números fictícios de demonstração (como no CSV da atividade),
     * valida apenas o formato de 11 dígitos válidos.
     */
    public static void validarCpf(String cpf, boolean validarDigitosVerificadores) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new ValidationException("O CPF do cliente é obrigatório.");
        }
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            throw new ValidationException("O CPF deve conter exatamente 11 dígitos numéricos.");
        }

        // Verifica se todos os dígitos são iguais (ex: 111.111.111-11)
        if (digitos.matches("(\\d)\\1{10}")) {
            throw new ValidationException("CPF inválido: sequência de dígitos repetidos.");
        }

        if (validarDigitosVerificadores) {
            if (!calcularDigitosCpf(digitos)) {
                throw new ValidationException("CPF inválido: dígitos verificadores não conferem.");
            }
        }
    }

    public static void validarFormatoCpf(String cpf) {
        validarCpf(cpf, false);
    }

    /**
     * Algoritmo padrão oficial da Receita Federal para cálculo de dígitos de CPF (Módulo 11).
     */
    public static boolean calcularDigitosCpf(String digitos) {
        if (digitos == null || digitos.length() != 11) {
            return false;
        }
        try {
            int soma1 = 0;
            for (int i = 0; i < 9; i++) {
                soma1 += (digitos.charAt(i) - '0') * (10 - i);
            }
            int d1 = 11 - (soma1 % 11);
            if (d1 >= 10) d1 = 0;

            if ((digitos.charAt(9) - '0') != d1) {
                return false;
            }

            int soma2 = 0;
            for (int i = 0; i < 10; i++) {
                soma2 += (digitos.charAt(i) - '0') * (11 - i);
            }
            int d2 = 11 - (soma2 % 11);
            if (d2 >= 10) d2 = 0;

            return (digitos.charAt(10) - '0') == d2;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Formata uma string de 11 dígitos para o formato ###.###.###-##.
     */
    public static String formatarCpf(String cpf) {
        if (cpf == null) return "";
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() == 11) {
            return String.format("%s.%s.%s-%s",
                    digitos.substring(0, 3),
                    digitos.substring(3, 6),
                    digitos.substring(6, 9),
                    digitos.substring(9, 11));
        }
        return cpf;
    }
}
