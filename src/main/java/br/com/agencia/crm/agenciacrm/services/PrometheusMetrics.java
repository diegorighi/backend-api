package br.com.agencia.crm.agenciacrm.services;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;

@Getter
@Service
public class PrometheusMetrics {
    
    private Timer usrReqCadastroClienteAverageTime;
    private Timer usrReqListagemClientesAverageTime;
    private Timer usrReqBuscaClienteCPFAverageTime;
    private Timer usrReqEdicaoTitularAverageTime;
    private Timer usrReqExclusaoClienteAverageTime;
    private Timer usrReqInclusaoDependenteAverageTime;
    private Timer usrReqEdicaoDependenteAverageTime;

    private Counter usrReqCadastroClienteTotalRequests;
    private Counter usrReqListagemClientesTotalRequests;
    private Counter usrReqBuscaClienteCPFTotalRequests;
    private Counter usrReqEdicaoTitularTotalRequests;
    private Counter usrReqExclusaoClienteTotalRequests;
    private Counter usrReqInclusaoDependenteTotalRequests;
    private Counter usrReqEdicaoDependenteTotalRequests;

    public PrometheusMetrics(MeterRegistry registry){
        this.usrReqCadastroClienteAverageTime = registry.timer("usr_req_cadastro_cliente_time", "method", "cadastrarCliente");
        this.usrReqListagemClientesAverageTime = registry.timer("usr_req_listagem_clientes_time", "method", "listarClientes");
        this.usrReqBuscaClienteCPFAverageTime = registry.timer("usr_req_busca_cliente_cpf_time", "method", "buscarClienteCPF");
        this.usrReqEdicaoTitularAverageTime = registry.timer("usr_req_edicao_titular_time", "method", "editarTitular");
        this.usrReqExclusaoClienteAverageTime = registry.timer("usr_req_exclusao_cliente_time", "method", "excluirCliente");
        this.usrReqInclusaoDependenteAverageTime = registry.timer("usr_req_inclusao_dependente_time", "method", "incluirDependente");
        this.usrReqEdicaoDependenteAverageTime = registry.timer("usr_req_edicao_dependente_time", "method", "editarDependente");

        this.usrReqCadastroClienteTotalRequests = registry.counter("usr_req_cadastro_cliente_total_requests", "method", "cadastrarCliente");
        this.usrReqListagemClientesTotalRequests = registry.counter("usr_req_listagem_clientes_total_requests", "method", "listarClientes");
        this.usrReqBuscaClienteCPFTotalRequests = registry.counter("usr_req_busca_cliente_cpf_total_requests", "method", "buscarClienteCPF");
        this.usrReqEdicaoTitularTotalRequests = registry.counter("usr_req_edicao_titular_total_requests", "method", "editarTitular");
        this.usrReqExclusaoClienteTotalRequests = registry.counter("usr_req_exclusao_cliente_total_requests", "method", "excluirCliente");
        this.usrReqInclusaoDependenteTotalRequests = registry.counter("usr_req_inclusao_dependente_total_requests", "method", "incluirDependente");
        this.usrReqEdicaoDependenteTotalRequests = registry.counter("usr_req_edicao_dependente_total_requests", "method", "editarDependente");

    }
   


    
}
