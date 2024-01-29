package br.com.agencia.crm.agenciacrm.models.records.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UsuarioDTO {
    
    private String nomeCompleto;
    private String clientId;

}
