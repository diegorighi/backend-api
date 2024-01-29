package br.com.agencia.crm.agenciacrm.domain.records.forms;


public record UsuarioRecordForm(
    String nomeCompleto,
    String clientId,
    String clientSecret
) {
    
}
