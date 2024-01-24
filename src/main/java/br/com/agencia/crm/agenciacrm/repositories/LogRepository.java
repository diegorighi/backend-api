package br.com.agencia.crm.agenciacrm.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.agencia.crm.agenciacrm.models.entities.LogEntity;

@Repository
public interface LogRepository extends MongoRepository<LogEntity, String> {

}
