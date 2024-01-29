package br.com.agencia.crm.agenciacrm.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.agencia.crm.agenciacrm.domain.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler<T> {
    
    @ExceptionHandler(ClienteJaCadastradoException.class)
    public ResponseEntity<ResponseWrapper<T>> handleClienteJaCadastradoException(ClienteJaCadastradoException ex) {
        ResponseWrapper<T> response = new ResponseWrapper<T>(null, ex.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<ResponseWrapper<T>> handleClienteNaoEncontradoException(ClienteNaoEncontradoException ex) {
        ResponseWrapper<T> response = new ResponseWrapper<T>(null, ex.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NaoExistemAlteracoesException.class)
    public ResponseEntity<ResponseWrapper<T>> handleNaoExistemAlteracoesException(NaoExistemAlteracoesException ex) {
        ResponseWrapper<T> response = new ResponseWrapper<T>(null, ex.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.NOT_MODIFIED);
    }

    @ExceptionHandler(DependenteException.class)
    public ResponseEntity<ResponseWrapper<T>> handleDependenteJaExisteException(DependenteException ex) {
        ResponseWrapper<T> response = new ResponseWrapper<T>(null, ex.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ResponseWrapper<T>> handleUsuarioNaoEncontradoException(UsuarioNaoEncontradoException ex) {
        ResponseWrapper<T> response = new ResponseWrapper<T>(null, ex.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<List<FieldValidationError>>> handleBeanValidationError(MethodArgumentNotValidException ex) {
        List<FieldError> listaErros = ex.getFieldErrors();
        
        ResponseWrapper<List<FieldValidationError>> response = 
                new ResponseWrapper<List<FieldValidationError>>(
                        listaErros.stream().map(FieldValidationError::new).collect(Collectors.toList()), 
                        "Erro no payload. Ha problema(s) no request", 
                        false);

        log.error("Erro no payload. Ha problema(s) no request: " + 
            listaErros.stream()
                .map(error -> "Campo: " + error.getField() + " -> " + error.getDefaultMessage())
                .collect(Collectors.joining("; ")));

        return new ResponseEntity<ResponseWrapper<List<FieldValidationError>>>(response, HttpStatus.BAD_REQUEST);
    }


    // Record para encapsular o erro de validação do bean
    private record FieldValidationError(String parametro, String mensagem) {

        public FieldValidationError(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }


}
