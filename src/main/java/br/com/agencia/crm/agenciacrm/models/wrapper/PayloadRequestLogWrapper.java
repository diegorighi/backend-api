package br.com.agencia.crm.agenciacrm.models.wrapper;

import lombok.Getter;

@Getter
public class PayloadRequestLogWrapper {

    private String xtrid = null;
    private Object payload = null;

    public PayloadRequestLogWrapper(String xtrid, Object payload) {
        this.xtrid = xtrid;
        this.payload = payload;
    }

}
