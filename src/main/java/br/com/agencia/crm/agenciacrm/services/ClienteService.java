package br.com.agencia.crm.agenciacrm.services;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.agencia.crm.agenciacrm.exceptions.ClienteJaCadastradoException;
import br.com.agencia.crm.agenciacrm.exceptions.ClienteNaoEncontradoException;
import br.com.agencia.crm.agenciacrm.exceptions.DependenteException;
import br.com.agencia.crm.agenciacrm.models.entities.Cliente;
import br.com.agencia.crm.agenciacrm.models.entities.DependenteEntity;
import br.com.agencia.crm.agenciacrm.models.entities.TitularEntity;
import br.com.agencia.crm.agenciacrm.models.records.dto.ClienteDTO;
import br.com.agencia.crm.agenciacrm.models.records.dto.DependenteRecordDTO;
import br.com.agencia.crm.agenciacrm.models.records.forms.ClienteForm;
import br.com.agencia.crm.agenciacrm.models.records.forms.DependenteEditRecordForm;
import br.com.agencia.crm.agenciacrm.models.records.forms.DependenteRecordForm;
import br.com.agencia.crm.agenciacrm.models.records.forms.TitularEditRecordForm;
import br.com.agencia.crm.agenciacrm.models.records.forms.TitularRecordForm;
import br.com.agencia.crm.agenciacrm.repositories.ClienteRepository;
import br.com.agencia.crm.agenciacrm.repositories.DependenteClienteRepository;
import br.com.agencia.crm.agenciacrm.utils.ClienteUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe de serviço para Cliente
 * 
 * @author Diego Righi
 */
@Slf4j
@Service
@PropertySource("classpath:mensagens.properties")
public class ClienteService {

    @Autowired
    private ClienteRepository<TitularEntity> titularRepository;
    @Autowired
    private DependenteClienteRepository dependenteRepository;

    private static String CLIENTE_JA_CADASTRADO;

    private static String CLIENTE_CADASTRADO_TITULAR;

    private static String CLIENTE_CADASTRADO_DEPENDENTE;

    private static String DEPENDENTE_JA_CADASTRADO;

    private static String DEPENDENTE_CADASTRADO_SUCESSO;

    private static String DEPENDENTE_ALTERADO_SUCESSO;

    private static String TITULAR_NAO_ENCONTRADO;

    private static String TITULAR_ATUALIZADO_SUCESSO;

    private static String GENERIC_DEPENDENTE_LOCALIZADO;

    private static String GENERIC_SEM_ALTERACOES;

    private static String GENERIC_ALTERACOES_IDENTIFICADAS;

    private static String GENERIC_DEPENDENTE_NAO_LOCALIZADO;

    private static String GENERIC_CLIENTE_NAO_ENCONTRADO;

    private static String TITULAR_REMOVER;

    private static String DEPENDENTE_REMOVER;

    @Autowired
    public ClienteService(@Value("${cliente.ja.cadastrado}") String clienteJaCadastrado,
                    @Value("${cliente.cadastrado.titular}") String clienteCadastradoTitular,
                    @Value("${cliente.cadastrado.dependente}") String clienteCadastradoDependente,
                    @Value("${dependente.ja.cadastrado}") String dependenteJaCadastrado,
                    @Value("${dependente.cadastrado.sucesso}") String dependenteCadastradoSucesso,
                    @Value("${dependente.alterado.sucesso}") String dependenteAlteradoSucesso,
                    @Value("${titular.nao.encontrado}") String titularNaoEncontrado,
                    @Value("${titular.atualizado.sucesso}") String titularAtualizadoSucesso,
                    @Value("${generic.sem.alteracoes}") String genericSemAlteracoes,
                    @Value("${generic.alteracoes.identificadas}") String genericAlteracoesIdentificadas,
                    @Value("${generic.dependente.localizado}") String genericDependenteLocalizado,
                    @Value("${generic.dependente.nao.localizado}") String genericDependenteNaoLocalizado,
                    @Value("${generic.cliente.nao.encontrado}") String genericClienteNaoEncontrado,
                    @Value("${titular.remover}") String titularRemover,
                    @Value("${dependente.remover}") String dependenteRemover
     ) {
        ClienteService.CLIENTE_JA_CADASTRADO = clienteJaCadastrado;
        ClienteService.CLIENTE_CADASTRADO_TITULAR = clienteCadastradoTitular;
        ClienteService.CLIENTE_CADASTRADO_DEPENDENTE = clienteCadastradoDependente;
        ClienteService.DEPENDENTE_JA_CADASTRADO = dependenteJaCadastrado;
        ClienteService.DEPENDENTE_CADASTRADO_SUCESSO = dependenteCadastradoSucesso;
        ClienteService.DEPENDENTE_ALTERADO_SUCESSO = dependenteAlteradoSucesso;
        ClienteService.TITULAR_NAO_ENCONTRADO = titularNaoEncontrado;
        ClienteService.TITULAR_ATUALIZADO_SUCESSO = titularAtualizadoSucesso;
        ClienteService.GENERIC_SEM_ALTERACOES = genericSemAlteracoes;
        ClienteService.GENERIC_ALTERACOES_IDENTIFICADAS = genericAlteracoesIdentificadas;
        ClienteService.GENERIC_DEPENDENTE_LOCALIZADO = genericDependenteLocalizado;
        ClienteService.GENERIC_DEPENDENTE_NAO_LOCALIZADO = genericDependenteNaoLocalizado;
        ClienteService.GENERIC_CLIENTE_NAO_ENCONTRADO = genericClienteNaoEncontrado;
        ClienteService.TITULAR_REMOVER = titularRemover;
        ClienteService.DEPENDENTE_REMOVER = dependenteRemover;
    } 

    @Transactional
    public Optional<Cliente> cadastroProcesso(String xtrid, ClienteForm form) {
        // Verifica se cliente já existe na base
        if (existeCliente(form.getCpf())){
            log.warn("x-trid: {} | Camada de Servico | cadastroProcesso | Sucesso: {} | Causa: {} |", 
                    xtrid, 
                    Boolean.FALSE,
                    CLIENTE_JA_CADASTRADO);
            throw new ClienteJaCadastradoException(CLIENTE_JA_CADASTRADO);
        }
        else
            return Optional.of(cadastro(xtrid, form));
    }

    /**
     * Coesão de responsabilidade: verifica se cliente já existe na base
     * 
     * @param cpf do cliente
     * @return true se cliente já existe, false se não existe
     */
    private Boolean existeCliente(String cpf) {
        Optional<TitularEntity> titular = titularRepository.findByDocumentos_Cpf(cpf);
        Optional<DependenteEntity> dependente = dependenteRepository.findByDocumentos_Cpf(cpf);

        if (titular.isPresent() || dependente.isPresent())
            return true;
        else
            return false;
    }

    /**
     * Coesão de responsabilidade: cadastra cliente na base
     * 
     * @param form cliente a ser cadastrado
     * @return mongoEntity salvo
     */
    private Cliente cadastro(String xtrid, ClienteForm form) {

        if (form.parent_id() == null) {
            TitularEntity titular = ClienteUtils.titularFormToEntity((TitularRecordForm) form);

            log.info("x-trid: {} | Camada de Servico | cadastro | Sucesso: {} | Causa: {} |", 
                    xtrid, 
                    Boolean.TRUE,
                    CLIENTE_CADASTRADO_TITULAR);

            return titularRepository.save(titular);
        } else {
            DependenteEntity dependente = ClienteUtils.dependenteFormToEntity((DependenteRecordForm) form);
            log.info("x-trid: {} | Camada de Servico | cadastro | Sucesso: {} | Causa: {} |", 
                    xtrid, 
                    Boolean.TRUE,
                    CLIENTE_CADASTRADO_DEPENDENTE);

            return dependenteRepository.save(dependente);
        }

    }

    /**
     * Coesão de responsabilidade: lista todos os clientes da base
     * 
     * @param pageRequest
     * @return lista de clientes paginada
     */
    public Page<TitularEntity> listarClientes(PageRequest pageRequest) {
        Page<TitularEntity> mongoEntities = titularRepository.findAll(pageRequest);
        return new PageImpl<>(mongoEntities.getContent(), pageRequest, mongoEntities.getTotalElements());
    }

    @Cacheable(value = "cliente", key = "#cpf", unless = "#result == null || #exception != null")
    public ClienteDTO buscarPorCPF(String cpf) {
        if (existeCliente(cpf)) {
            Optional<TitularEntity> titular = titularRepository.findByDocumentos_Cpf(cpf);
            Optional<DependenteEntity> dependente = dependenteRepository.findByDocumentos_Cpf(cpf);

            if (!titular.isEmpty()) {
                ClienteDTO titularDTO = ClienteUtils.titularEntityToDto(titular.get());
                return titularDTO;
            } else {
                ClienteDTO dependenteDTO = ClienteUtils.dependenteEntityToDto(dependente.get());
                return dependenteDTO;
            }
        } else
            throw new ClienteNaoEncontradoException(GENERIC_CLIENTE_NAO_ENCONTRADO);
    }

    @Transactional
    @CacheEvict(value = "cliente", key = "#cpf")
    public HashMap<String, Object> editarTitular(String xtrid, String cpf, TitularEditRecordForm formEdit) {
        // Busca o titular na base
        Optional<TitularEntity> titular = titularRepository.findByDocumentos_Cpf(cpf);

        // Cria um HashMap para armazenar as alterações
        HashMap<String, Object> alteracoesMap = new HashMap<>();

        // Verifica se o titular existe
        titular.ifPresentOrElse(
                titularValue -> {
                    // Verifica se existem alterações
                    alteracoesMap
                            .putAll(exitemAlteracoesTitular(formEdit, titularValue, new HashMap<String, Object>()));
                    // Se existirem alterações, atualiza o titular
                    if (!alteracoesMap.isEmpty()) {
                        titularValue.getDadosPessoais().setSobrenome(formEdit.sobrenome());
                        titularValue.getDadosPessoais().setEstadoCivil(formEdit.estadoCivil().getDescricao());
                        titularValue.getDadosPessoais().setProfissao(formEdit.profissao());
                        titularValue.getPreferencias()
                                .setPreferenciaClasse(formEdit.preferenciaClasse().getDescricao());
                        titularValue.getPreferencias()
                                .setPreferenciaAssento(formEdit.preferenciaAssento().getDescricao());
                        titularValue.getPreferencias()
                                .setPreferenciaRefeicao(formEdit.preferenciaRefeicao().getDescricao());
                        titularValue.getDocumentos().setPassaporte(formEdit.passaporte());
                        titularValue.getDocumentos().setDataVencimentoPassaporte(formEdit.dataVencimentoPassaporte());
                        titularValue.getContatos().setEmail(formEdit.email());
                        titularValue.getContatos().setCelular(formEdit.celular());
                        titularValue.getEndereco().setLogradouro(formEdit.logradouro());
                        titularValue.getEndereco().setNumero(formEdit.numero());
                        titularValue.getEndereco().setComplemento(formEdit.complemento());
                        titularValue.getEndereco().setCidade(formEdit.cidade());
                        titularValue.getEndereco().setUf(formEdit.uf().getDescricao());
                        titularValue.getEndereco().setCep(formEdit.cep());
                        titularValue.getEndereco().setPais(formEdit.pais());

                        titularRepository.save(titularValue);
                        
                        log.info("x-trid: {} | Camada de Servico | editarTitular | Sucesso: {} | Causa: {} |", 
                            xtrid, 
                            Boolean.TRUE,
                            TITULAR_ATUALIZADO_SUCESSO);
                    } else{
                        log.warn("x-trid: {} | Camada de Servico | editarTitular | Sucesso: {} | Causa: {} |", 
                            xtrid, 
                            Boolean.FALSE,
                            GENERIC_SEM_ALTERACOES);
                        throw new ClienteJaCadastradoException(GENERIC_SEM_ALTERACOES);

                    }
                },
                () -> {
                    log.warn("x-trid: {} | Camada de Servico | editarTitular | Sucesso: {} | Causa: {} |", 
                        xtrid, 
                        Boolean.FALSE,
                        TITULAR_NAO_ENCONTRADO);
                    throw new ClienteNaoEncontradoException(TITULAR_NAO_ENCONTRADO);
                });

        // Retorna o mapa de alterações
        return alteracoesMap;
    }

    @Transactional
    @CacheEvict(value = "cliente", key = "#cpfDependente")
    public HashMap<String, Object> editarDependente(String xtrid, String cpfDependente, DependenteEditRecordForm formEdit) {
        // Busca o dependente na base
        Optional<DependenteEntity> dependente = dependenteRepository.findByDocumentos_Cpf(cpfDependente);
        
        // Cria um HashMap para armazenar as alterações
        HashMap<String, Object> alteracoesMap = new HashMap<>();
        
        // Verifica se o dependente existe
        dependente.ifPresentOrElse(dependenteValue -> {
            
            // Verifica se existem alterações
            alteracoesMap.putAll(existemAlteracoesDependente(formEdit, dependenteValue, new HashMap<String, Object>()));

            // Se existirem alterações, atualiza o dependente e o titular
            if (!alteracoesMap.isEmpty()) {
                dependenteValue.getDadosPessoais().setPrimeiroNome(formEdit.primeiroNome());
                dependenteValue.getDadosPessoais().setNomeDoMeio(formEdit.nomeDoMeio());
                dependenteValue.getDadosPessoais().setSobrenome(formEdit.sobrenome());
                dependenteValue.getDadosPessoais().setEstadoCivil(formEdit.estadoCivil().getDescricao());
                dependenteValue.getDadosPessoais().setProfissao(formEdit.profissao());
                dependenteValue.getDocumentos().setPassaporte(formEdit.passaporte());
                dependenteValue.getDocumentos().setDataVencimentoPassaporte(formEdit.dataVencimentoPassaporte());

                dependenteRepository.save(dependenteValue);
                
                // Altera dados no titular
                TitularEntity titular = titularRepository.findByDocumentos_Cpf(dependenteValue.getParentId()).get();
                titular.getDependentes().forEach(dependenteEntity -> {
                    if (dependenteEntity.getDocumentos().getCpf().equals(cpfDependente)) {
                        dependenteEntity.getDadosPessoais().setPrimeiroNome(formEdit.primeiroNome());
                        dependenteEntity.getDadosPessoais().setNomeDoMeio(formEdit.nomeDoMeio());
                        dependenteEntity.getDadosPessoais().setSobrenome(formEdit.sobrenome());
                        dependenteEntity.getDadosPessoais().setEstadoCivil(formEdit.estadoCivil().getDescricao());
                        dependenteEntity.getDadosPessoais().setProfissao(formEdit.profissao());
                        dependenteEntity.getDocumentos().setPassaporte(formEdit.passaporte());
                        dependenteEntity.getDocumentos().setDataVencimentoPassaporte(formEdit.dataVencimentoPassaporte());
                    }
                });
                titularRepository.save(titular);

                log.info("x-trid: {} | Camada de Servico | editarDependente | Sucesso: {} | Causa: {} |", 
                    xtrid, 
                    Boolean.TRUE,
                    DEPENDENTE_ALTERADO_SUCESSO);
            } else{
                log.warn("x-trid: {} | Camada de Servico | editarDependente | Sucesso: {} | Causa: {} |", 
                    xtrid, 
                    Boolean.FALSE,
                    GENERIC_SEM_ALTERACOES);
                throw new ClienteNaoEncontradoException(GENERIC_SEM_ALTERACOES);

            }
            
        },() -> {
            log.warn("x-trid: {} | Camada de Servico | editarDependente | Sucesso: {} | Causa: {} |", 
                    xtrid, 
                    Boolean.FALSE,
                    GENERIC_DEPENDENTE_NAO_LOCALIZADO);
            throw new ClienteNaoEncontradoException(GENERIC_DEPENDENTE_NAO_LOCALIZADO);
        });

        return alteracoesMap;
    }

    private HashMap<String, Object> exitemAlteracoesTitular(TitularEditRecordForm form, TitularEntity titular,
            HashMap<String, Object> alteracoesMap) {
        return ClienteUtils.comparaFormComEntity(form, titular, alteracoesMap);
    }

    private HashMap<String, Object> existemAlteracoesDependente(DependenteEditRecordForm form, DependenteEntity dadosDaBase,
            HashMap<String, Object> formAlteracoes) {
        return ClienteUtils.comparaFormComEntity(form, dadosDaBase, formAlteracoes);
    }

    @Transactional
    @Caching(evict = {@CacheEvict(value = "cliente", key = "#cpf")})
    public Boolean removerCliente(String xtrid, String cpf) {
        Boolean removido = Boolean.FALSE;
        // Verifica se cliente existe
        if(existeCliente(cpf)){
            // Verifica se é titular ou dependente
            Optional<TitularEntity> titular = titularRepository.findByDocumentos_Cpf(cpf);
            Optional<DependenteEntity> dependente = dependenteRepository.findByDocumentos_Cpf(cpf);

            if(titular.isPresent()){
                // Remove todos os dependentes vinculados ao titular
                dependenteRepository.deleteAllByParentId(cpf);
                // Remove o titular
                titularRepository.delete(titular.get());

                removido = Boolean.TRUE;

                log.info("x-trid: {} | Camada de Servico | removerCliente | Sucesso: {} | Causa: {} |", 
                    xtrid, 
                    removido,
                    TITULAR_REMOVER);
            }

            if(dependente.isPresent()){
                // Remove o dependente da collection
                dependenteRepository.delete(dependente.get());
                // Remove o dependente do titular
                TitularEntity titularMongoEntity = titularRepository.findByDocumentos_Cpf(dependente.get().getParentId()).get();
                titularMongoEntity.removeDependente(cpf);
                // Atualiza o titular
                titularRepository.save(titularMongoEntity);

                removido = Boolean.TRUE;

                log.info("x-trid: {} | Camada de Servico | removerCliente | Sucesso: {} | Causa: {} |", 
                    xtrid, 
                    removido,
                    DEPENDENTE_REMOVER);
            }
        }else{
            log.warn("x-trid: {} | Camada de Servico | removerCliente | Sucesso: {} | Causa: {} |", 
                    xtrid, 
                    removido,
                    GENERIC_CLIENTE_NAO_ENCONTRADO);
            throw new ClienteNaoEncontradoException(GENERIC_CLIENTE_NAO_ENCONTRADO);
        }

        return removido;
    }

    @Transactional
    public DependenteRecordDTO incluirDependente(String xtrid, DependenteRecordForm form) {
        // Verificar se o titular existe
        Optional<TitularEntity> titular = titularRepository.findByDocumentos_Cpf(form.parent_id());
        if (titular.isPresent()) {
            // Verificar se o dependente já existe
            Optional<DependenteEntity> dependente = dependenteRepository.findByDocumentos_Cpf(form.cpf());
            if (dependente.isPresent()){
                log.warn("x-trid: {} | Camada de Servico | Sucesso: {} | Causa: {} |", 
                    xtrid, 
                    Boolean.FALSE,
                    DEPENDENTE_JA_CADASTRADO);
                throw new DependenteException(DEPENDENTE_JA_CADASTRADO);
            }
            else {
                // A variável parent_id é o cpf do titular que vem no form
                DependenteEntity dependenteEntity = ClienteUtils.dependenteFormToEntity(form);
                // Precisa cadastrar o dependente no TitularEntity
                titular.get().getDependentes().add(dependenteEntity);
                titularRepository.save(titular.get());

                // Precisa salvar o dependente no DependenteEntity
                dependenteRepository.save(dependenteEntity);

                log.info("x-trid: {} | Camada de Servico | Sucesso: {} | Causa: {} |", 
                    xtrid, 
                    Boolean.TRUE,
                    DEPENDENTE_CADASTRADO_SUCESSO);
                return updateCacheAndReturnDto(dependenteEntity);
            }
        } else{
            log.warn("x-trid: {} | Camada de Servico | Sucesso: {} | Causa: {} |", 
                    xtrid, 
                    Boolean.FALSE,
                    TITULAR_NAO_ENCONTRADO);
            throw new ClienteNaoEncontradoException(TITULAR_NAO_ENCONTRADO);
        }
    }

    @CachePut(value = "cliente", key = "#dependenteEntity.documentos.cpf")
    public DependenteRecordDTO updateCacheAndReturnDto(DependenteEntity dependenteEntity) {
        return ClienteUtils.dependenteEntityToDto(dependenteEntity);
    }

}
