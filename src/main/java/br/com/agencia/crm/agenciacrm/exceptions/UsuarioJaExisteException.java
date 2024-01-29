package br.com.agencia.crm.agenciacrm.exceptions;

public class UsuarioJaExisteException extends RuntimeException {
    
    public UsuarioJaExisteException(String message) {
        super(message);
    }
    
}
