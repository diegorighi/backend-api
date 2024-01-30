package br.com.agencia.crm.agenciacrm.filters;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTDecodeException;

import br.com.agencia.crm.agenciacrm.domain.entities.UsuarioEntity;
import br.com.agencia.crm.agenciacrm.exceptions.TokenJWTInvalidoException;
import br.com.agencia.crm.agenciacrm.repositories.UsuarioRepository;
import br.com.agencia.crm.agenciacrm.services.UsuarioService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * A classe SecurityFilters é um filtro que intercepta todas as requisições e verifica se o token JWT é válido.
 * Se o token for válido, o usuário é autenticado e o processo de autenticação é finalizado.
 * Se o token não for válido, um erro é registrado e o processo de autenticação é finalizado.
 * @author Diego Righi
 */

@Slf4j
@Component
public class SecurityFilters extends OncePerRequestFilter {

    private UsuarioService usuarioService;
    private UsuarioRepository repository;

    private Counter authUserSuccessCounter;
    private Counter authUserErrorCounter;

    @Value("${username.ja.cadastrado}")
    private String usuarioJaExiste;

    /**
     * Construtor
     * O construtor da classe SecurityFilters recebe três parâmetros: 
     * @param usuarioService
     * @param repository
     * @param registry
     * O UsuarioService e o UsuarioRepository são injetados usando a anotação @Autowired,
     * que é uma maneira de o Spring fazer a injeção de dependência. 
     * O MeterRegistry é passado como um argumento para o construtor e é usado para registrar os contadores.
     */
    public SecurityFilters(
        @Autowired UsuarioService usuarioService, 
        @Autowired UsuarioRepository repository,
        MeterRegistry registry) {

        this.usuarioService = usuarioService;
        this.repository = repository;

        this.authUserSuccessCounter = Counter.builder("auth_user_success")
            .description("Número de autenticações de usuário bem-sucedidas")
            .register(registry);

        this.authUserErrorCounter = Counter.builder("auth_user_error")
            .description("Número de erros de autenticação do usuário")
            .register(registry);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("========================================================================");
        log.info("PASSO 1: Iniciando o filtros de segurança do SpringBoot");
        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            try{
                log.info("PASSO 4: Autenticando o usuário");
                var subject = usuarioService.getSubject(tokenJWT);
                Optional<UsuarioEntity> usuario = repository.findById(subject);
    
                log.info("PASSO 5: Setando o usuário autenticado");
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.get().getAuthorities());
    
                log.info("PASSO 6: Incrementando contador de sucesso de autenticação no Prometheus");
                this.authUserSuccessCounter.increment();
    
                log.info("PASSO 7: Sucesso. Finalizando o processo de autenticação");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            }catch(JWTDecodeException e) {
                log.error("PASSO 5: Incrementando contador de erro de autenticação no Prometheus");
                this.authUserErrorCounter.increment();
                throw new TokenJWTInvalidoException();
            }
        }else {
            log.warn("PASSO 4: "+this.usuarioJaExiste);
        }

        filterChain.doFilter(request, response);

    }
    
    private String recuperarToken(HttpServletRequest request) {
        log.info("PASSO 2: Recuperando o token");
        var authorizationHeader = request.getHeader("Authorization");

        log.info("PASSO 3: Verificando se o token é válido");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "").trim();
        }
        return null;
    }


    
}
