package br.com.agencia.crm.agenciacrm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.agencia.crm.agenciacrm.models.entities.LogEntity;
import br.com.agencia.crm.agenciacrm.models.wrapper.PayloadRequestLogWrapper;
import br.com.agencia.crm.agenciacrm.repositories.LogRepository;

@Service
public class LogService {

    @Autowired
    private LogRepository repository;

    public void registrarInput(String xtrid, PayloadRequestLogWrapper payload, Boolean success, String cause) {
        repository.save(new LogEntity(xtrid, payload, success, cause));
    }
}
