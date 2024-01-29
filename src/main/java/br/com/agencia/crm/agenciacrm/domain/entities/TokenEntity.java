package br.com.agencia.crm.agenciacrm.domain.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Document("tokens")
public class TokenEntity {
    
    @Id
    private String token;
    private Boolean isValid;
    private Long expiresIn;

}
