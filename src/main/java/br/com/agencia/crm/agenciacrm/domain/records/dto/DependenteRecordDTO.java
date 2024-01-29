package br.com.agencia.crm.agenciacrm.domain.records.dto;

public record DependenteRecordDTO(
    String parent_id,
    DadosPessoaisRecordDTO dadosPessoais,
    DocumentosRecordDTO documentos
) implements ClienteDTO {
    
}
