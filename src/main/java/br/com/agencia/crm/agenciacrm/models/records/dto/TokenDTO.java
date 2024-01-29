package br.com.agencia.crm.agenciacrm.models.records.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenDTO {
    
    private String access_token;
    private String token_type;
    private Instant expires_in;

    
}
