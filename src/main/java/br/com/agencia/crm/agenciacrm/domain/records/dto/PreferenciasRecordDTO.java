package br.com.agencia.crm.agenciacrm.domain.records.dto;

import br.com.agencia.crm.agenciacrm.domain.enums.PreferenciaAssentoEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.PreferenciaClasseEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.PreferenciaRefeicaoEnum;

public record PreferenciasRecordDTO(

    PreferenciaClasseEnum preferenciaClasse,
    PreferenciaAssentoEnum preferenciaAssento,
    PreferenciaRefeicaoEnum preferenciaRefeicao

) {
    
}
