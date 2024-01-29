package br.com.agencia.crm.agenciacrm.models.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TokenWrapper {
    
    private String token;
    private Boolean isValid;
    private Long expiresIn;
    
}
