package br.com.agencia.crm.agenciacrm.models.records.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenDTO {
    
    private String access_token;
    private String token_type;
    private Long expires_in;

    
}
