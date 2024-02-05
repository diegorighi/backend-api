package br.com.agencia.crm.agenciacrm.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import br.com.agencia.crm.agenciacrm.domain.entities.UsuarioEntity;

@Repository
public interface UsuarioRepository extends MongoRepository<UsuarioEntity, String> {

    UserDetails findByClientId(String username);

}
