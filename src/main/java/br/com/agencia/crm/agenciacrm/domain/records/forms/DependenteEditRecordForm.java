package br.com.agencia.crm.agenciacrm.domain.records.forms;

import br.com.agencia.crm.agenciacrm.domain.enums.EstadoCivilEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.SexoEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DependenteEditRecordForm(
    @NotEmpty(message = "O primeiro nome não pode estar vazio")
    @Size(min = 2, max = 50, message = "O primeiro nome deve ter entre 2 e 50 caracteres")
    String primeiroNome,

    @Size(max = 50, message = "O nome do meio não deve exceder 50 caracteres")
    String nomeDoMeio,

    @NotEmpty(message = "O sobrenome não pode estar vazio")
    @Size(min = 2, max = 50, message = "O sobrenome deve ter entre 2 e 50 caracteres")
    String sobrenome,

    @NotNull(message = "O sexo não pode estar vazio")
    SexoEnum sexo,

    @NotNull(message = "O estado civil não pode estar vazio")
    EstadoCivilEnum estadoCivil,

    @NotEmpty(message = "A profissão não pode estar vazia")
    @Size(max = 100, message = "A profissão não deve exceder 100 caracteres")
    String profissao,

    @NotEmpty(message = "O passaporte não pode estar vazio")
    @Size(min = 5, max = 20, message = "O passaporte deve ter entre 5 e 20 caracteres")
    String passaporte,

    @NotEmpty(message = "A data de vencimento do passaporte não pode estar vazia")
    String dataVencimentoPassaporte
) {
    
}
