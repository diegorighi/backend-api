package br.com.agencia.crm.agenciacrm.filters;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.agencia.crm.agenciacrm.domain.entities.UsuarioEntity;
import br.com.agencia.crm.agenciacrm.exceptions.UsuarioJaExisteException;
import br.com.agencia.crm.agenciacrm.repositories.UsuarioRepository;
import br.com.agencia.crm.agenciacrm.services.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SecurityFilters extends OncePerRequestFilter {

    private UsuarioService usuarioService;
    private UsuarioRepository repository;

    @Value("${username.ja.cadastrado}")
    private String usuarioJaExiste;

    public SecurityFilters(
        @Autowired UsuarioService usuarioService, 
        @Autowired UsuarioRepository repository) {

        this.usuarioService = usuarioService;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("========================================================================");
        log.info("PASSO 1: Iniciando o filtros de segurança do SpringBoot");
        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            log.info("PASSO 4: Autenticando o usuário");
            var subject = usuarioService.getSubject(tokenJWT);
            Optional<UsuarioEntity> usuario = repository.findById(subject);

            log.info("PASSO 5: Setando o usuário autenticado");
            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.get().getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("PASSO 6: Sucesso. Finalizando o processo de autenticação");
        }else log.warn("PASSO 4: "+this.usuarioJaExiste);
        
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
