package br.com.agencia.crm.agenciacrm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.agencia.crm.agenciacrm.models.records.dto.UsuarioDTO;
import br.com.agencia.crm.agenciacrm.models.records.forms.UsuarioRecordForm;
import br.com.agencia.crm.agenciacrm.models.wrapper.ResponseWrapper;
import br.com.agencia.crm.agenciacrm.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private UsuarioService service;

    private static String USERNAME_CADASTRADO_SUCESSO;

    @Autowired
    public UsuarioController(
            UsuarioService service,
            @Value("${username.cadastrado.sucesso}") String usernameCadastradoSucesso) {
            this.service = service;
            UsuarioController.USERNAME_CADASTRADO_SUCESSO = usernameCadastradoSucesso;
        }

    @PostMapping("/cadastrar")
    public ResponseEntity<ResponseWrapper<UsuarioDTO>> cadastrarUsuario(@RequestBody @Valid UsuarioRecordForm form) {
        log.info("========================================================================");
        log.info("OAuth2 | Authorization Server | Iniciando processo de [CREDENCIAMENTO DE USERNAME]");
        
        UsuarioDTO usuario = service.cadastrarUsuario(form);
        
        log.info("OAuth2 | Authorization Server | Finalizando com sucesso o processo de [CREDENCIAMENTO DE USERNAME]");
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ResponseWrapper<UsuarioDTO>(usuario, 
            UsuarioController.USERNAME_CADASTRADO_SUCESSO + ": " + form.nomeCompleto(), 
            Boolean.TRUE)
        );
    }
    
}
