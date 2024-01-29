package br.com.agencia.crm.agenciacrm.utils;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.com.agencia.crm.agenciacrm.domain.enums.EstadoCivilEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.PreferenciaAssentoEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.PreferenciaClasseEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.PreferenciaRefeicaoEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.SexoEnum;
import br.com.agencia.crm.agenciacrm.domain.enums.UfEnum;
import br.com.agencia.crm.agenciacrm.domain.records.forms.TitularRecordForm;

public class CadastroClienteTest {

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private TitularRecordForm criarClienteFormComCPF(String cpf) {
        return new TitularRecordForm(
            "",
            "João",
            "Carlos",
            "Silva",
            "1990-01-15",
            SexoEnum.MASCULINO,
            EstadoCivilEnum.CASADO,
            "Engenheiro",
            PreferenciaClasseEnum.ECONOMICA,
            PreferenciaAssentoEnum.JANELA,
            PreferenciaRefeicaoEnum.VEGETARIANA,
            cpf,
            "AB12345",
            "2025-08-20",
            "joao.silva@email.com",
            "11999887766",
            "Rua das Flores",
            123,
            "Apto 101",
            "São Paulo",
            UfEnum.SP,
            "01234-567",
            "Brasil"
        );
    }

    @Test
    public void valoresCorretosClienteRecordForm() {
        TitularRecordForm clienteForm = criarClienteFormComCPF("335.192.518-25");
        var violations = validator.validate(clienteForm);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void cpfInvalidoClienteRecordForm() {
        TitularRecordForm clienteForm = criarClienteFormComCPF("123.456.789-00");
        var violations = validator.validate(clienteForm);
        assertFalse(violations.isEmpty());
    }
}
