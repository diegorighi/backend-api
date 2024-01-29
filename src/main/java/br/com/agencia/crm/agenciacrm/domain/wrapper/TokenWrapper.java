package br.com.agencia.crm.agenciacrm.domain.wrapper;

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
