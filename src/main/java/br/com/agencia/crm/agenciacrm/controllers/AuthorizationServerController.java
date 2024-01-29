package br.com.agencia.crm.agenciacrm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.agencia.crm.agenciacrm.models.entities.UsuarioEntity;
import br.com.agencia.crm.agenciacrm.models.records.forms.UsuarioRecordForm;
import br.com.agencia.crm.agenciacrm.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/oauth")
public class AuthorizationServerController {

    private AuthenticationManager manager;
    private UsuarioService tokenService;

    @Autowired
    public AuthorizationServerController(
        @Autowired AuthenticationManager manager, 
        @Autowired UsuarioService tokenService) {
        
        this.manager = manager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<?> getToken(@RequestBody @Valid UsuarioRecordForm form) {
        
        log.info("PASSO 1: Iniciando o processo de autenticação");
        var token = new UsernamePasswordAuthenticationToken(form.clientId(), form.clientSecret());
        var authenticate = manager.authenticate(token);

        return ResponseEntity.ok(tokenService.getToken((UsuarioEntity) authenticate.getPrincipal()));
    }
}
