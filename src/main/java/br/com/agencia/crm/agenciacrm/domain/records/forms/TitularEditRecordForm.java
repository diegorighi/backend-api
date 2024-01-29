package br.com.agencia.crm.agenciacrm.domain.records.forms;

import br.com.agencia.crm.agenciacrm.domain.enums.EstadoCivilEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.PreferenciaAssentoEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.PreferenciaClasseEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.PreferenciaRefeicaoEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.UfEnum;

public record TitularEditRecordForm(
    String sobrenome,
    EstadoCivilEnum estadoCivil,
    String profissao,
    PreferenciaClasseEnum preferenciaClasse,
    PreferenciaAssentoEnum preferenciaAssento,
    PreferenciaRefeicaoEnum preferenciaRefeicao,
    String passaporte,
    String dataVencimentoPassaporte,
    String email,
    String celular,
    String logradouro,
    Integer numero,
    String complemento,
    String cidade,
    UfEnum uf,
    String cep,
    String pais
) {
    
}
