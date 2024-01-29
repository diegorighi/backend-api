package br.com.agencia.crm.agenciacrm.domain.wrapper;

import java.util.List;

import org.springframework.validation.FieldError;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class FieldErrorValidation {
    
    private String campo;
    private String mensagem;

}
