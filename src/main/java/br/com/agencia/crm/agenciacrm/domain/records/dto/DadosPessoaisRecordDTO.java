package br.com.agencia.crm.agenciacrm.domain.records.dto;

import br.com.agencia.crm.agenciacrm.domain.enums.EstadoCivilEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.SexoEnum;

public record DadosPessoaisRecordDTO(
    String primeiroNome,
    String nomeDoMeio,
    String sobrenome,
    String dataNascimento,
    SexoEnum sexo,
    EstadoCivilEnum estadoCivil,
    String profissao
) {
    
}
