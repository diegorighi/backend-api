package br.com.agencia.crm.agenciacrm.exceptions;

public class TokenJWTInvalidoException extends RuntimeException {

    public TokenJWTInvalidoException(String message) {
        super(message);
    }

    public TokenJWTInvalidoException(){
        super("Token JWT inválido");
    }
    
}
