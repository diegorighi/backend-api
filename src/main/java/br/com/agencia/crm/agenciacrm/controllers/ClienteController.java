package br.com.agencia.crm.agenciacrm.controllers;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.agencia.crm.agenciacrm.models.entities.Cliente;
import br.com.agencia.crm.agenciacrm.models.entities.TitularEntity;
import br.com.agencia.crm.agenciacrm.models.records.dto.ClienteDTO;
import br.com.agencia.crm.agenciacrm.models.records.dto.DependenteRecordDTO;
import br.com.agencia.crm.agenciacrm.models.records.dto.TitularRecordDTO;
import br.com.agencia.crm.agenciacrm.models.records.forms.DependenteEditRecordForm;
import br.com.agencia.crm.agenciacrm.models.records.forms.DependenteRecordForm;
import br.com.agencia.crm.agenciacrm.models.records.forms.TitularEditRecordForm;
import br.com.agencia.crm.agenciacrm.models.records.forms.TitularRecordForm;
import br.com.agencia.crm.agenciacrm.models.wrapper.ResponseWrapper;
import br.com.agencia.crm.agenciacrm.services.ClienteService;
import br.com.agencia.crm.agenciacrm.utils.ClienteUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private ClienteService service;

    // Injeção de dependência do construtor
    public ClienteController(@Autowired ClienteService service) {
        this.service = service;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<ResponseWrapper<TitularRecordDTO>> cadastrarCliente(
            @RequestHeader(value = "x-trid", required = true) String xtrid,
            @RequestBody @Valid TitularRecordForm form) {
                

        log.info("========================================================================");
        log.info("x-trid: {} | Camada de Controller | Iniciando processo de [CADASTRO DE CLIENTE]",
                xtrid);

        Optional<Cliente> cliente = service.cadastroProcesso(xtrid, form);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseWrapper<TitularRecordDTO>(
                        ClienteUtils.titularEntityToDto((TitularEntity) cliente.get()),
                        "Cliente cadastrado com sucesso!",
                        true));

    }

    @GetMapping("/listar")
    public ResponseEntity<Page<TitularRecordDTO>> listarCliente(
            @RequestHeader(value = "x-trid", required = true) String xtrid,
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "50") int tamanho) {

        log.info("========================================================================");
        log.info("x-trid: {} | Camada de Controller | Iniciando processo de [LISTAGEM PAGINADA DE CLIENTES]", xtrid);
        PageRequest pageRequest = PageRequest.of(pagina, tamanho, Sort.Direction.ASC, "nome");
        Page<TitularEntity> listaClientes = service.listarClientes(pageRequest);

        Page<TitularRecordDTO> listaClientesDTO = listaClientes.map(ClienteUtils::titularEntityToDto);
        return ResponseEntity.ok(listaClientesDTO);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<ClienteDTO> buscarCliente(
        @RequestHeader(value = "x-trid", required = true) String xtrid,
        @PathVariable String cpf) {

        log.info("========================================================================");
        log.info("x-trid: {} | Camada de Controller | Iniciando processo de [BUSCA DE CLIENTE POR CPF]", xtrid);
        ClienteDTO cliente = service.buscarPorCPF(cpf);

        return ResponseEntity.status(200).body(cliente);
    }

    @PostMapping("/dependente/incluir")
    public ResponseEntity<ResponseWrapper<DependenteRecordDTO>> incluirDependente(
        @RequestHeader(value = "x-trid", required = true) String xtrid,
        @RequestBody @Valid DependenteRecordForm form) {
       
        log.info("========================================================================");
        log.info("x-trid: {} | Camada de Controller | Iniciando processo de [INCLUSÃO DE DEPENDENTE]", xtrid);

        DependenteRecordDTO dependente = service.incluirDependente(xtrid, form);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseWrapper<DependenteRecordDTO>(
                        dependente,
                        "Dependente cadastrado com sucesso!",
                        true));
    }

    @PatchMapping("/editar/titular/{cpf}")
    public ResponseEntity<ResponseWrapper<HashMap<String, Object>>> editarTitular(
            @RequestHeader(value = "x-trid", required = true) String xtrid,
            @PathVariable String cpf,
            @RequestBody TitularEditRecordForm formEdit) {

        log.info("========================================================================");
        log.info("x-trid: {} | Camada de Controller | Iniciando processo de [EDIÇÃO DO TITULAR]", xtrid);

        HashMap<String, Object> alteracoes = service.editarTitular(xtrid, cpf, formEdit);

        return ResponseEntity.ok(
                new ResponseWrapper<HashMap<String, Object>>(
                        alteracoes,
                        "Alterações realizadas com sucesso!",
                        true));
    }

    @DeleteMapping("/excluir/{cpf}")
    public ResponseEntity<ResponseWrapper<String>> excluirCliente(
        @RequestHeader(value = "x-trid", required = true) String xtrid,
        @PathVariable String cpf) {

        log.info("========================================================================");
        log.info("x-trid: {} | Camada de Controller | Iniciando processo de [REMOCAO DE CLIENTE POR CPF]", xtrid);
        Boolean clienteRemovido = service.removerCliente(xtrid, cpf);

        return ResponseEntity.status(204).body(new ResponseWrapper<String>(
                        cpf,
                        "O Cpf informado foi removido com sucesso!",
                        clienteRemovido));
    }

    // Editar dependente
    @PatchMapping("/dependente/editar/{cpfDependente}")
    public ResponseEntity<ResponseWrapper<HashMap<String, Object>>> editarDependente(
            @RequestHeader(value = "x-trid", required = true) String xtrid,
            @PathVariable String cpfDependente,
            @RequestBody final DependenteEditRecordForm formEdit) {

        log.info("========================================================================");
        log.info("x-trid: {} | Camada de Controller | Inciando processo de [EDIÇÃO DE DEPENDENTE]", xtrid);
        HashMap<String, Object> alteracoes = service.editarDependente(xtrid, cpfDependente, formEdit);

        return ResponseEntity.ok(
                new ResponseWrapper<HashMap<String, Object>>(
                        alteracoes,
                        "Alterações realizadas com sucesso!",
                        true));
    }

}
