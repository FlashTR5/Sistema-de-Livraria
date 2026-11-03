package br.com.livraria.model.validation;

/**
 * Exceção lançada quando regras de validação ou de negócio são violadas.
 */
public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
