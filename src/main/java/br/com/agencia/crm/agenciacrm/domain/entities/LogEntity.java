package br.com.agencia.crm.agenciacrm.domain.entities;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.agencia.crm.agenciacrm.domain.wrapper.PayloadRequestLogWrapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Document("registroLogs")
public class LogEntity implements Serializable {

    private String xtrid;
    private PayloadRequestLogWrapper payload;
    private Boolean sucesso;
    private String cause;

}
