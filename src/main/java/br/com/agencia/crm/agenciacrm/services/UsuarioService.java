package br.com.agencia.crm.agenciacrm.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.agencia.crm.agenciacrm.domain.entities.UsuarioEntity;
import br.com.agencia.crm.agenciacrm.domain.records.dto.TokenDTO;
import br.com.agencia.crm.agenciacrm.domain.records.dto.UsuarioDTO;
import br.com.agencia.crm.agenciacrm.domain.records.forms.UsuarioLoginForm;
import br.com.agencia.crm.agenciacrm.domain.records.forms.UsuarioRecordForm;
import br.com.agencia.crm.agenciacrm.exceptions.ClienteJaCadastradoException;
import br.com.agencia.crm.agenciacrm.exceptions.ClienteNaoEncontradoException;
import br.com.agencia.crm.agenciacrm.exceptions.TokenJWTInvalidoException;
import br.com.agencia.crm.agenciacrm.exceptions.UsuarioNaoEncontradoException;
import br.com.agencia.crm.agenciacrm.repositories.UsuarioRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${jwt.secret}")
    private String secret;

    private static String USERNAME_NAO_ENCONTRADO;
    private static String USERNAME_JA_CADASTRADO;

    private Counter authUserErrorCounter;
    
    @Autowired
    public UsuarioService(
        @Value("${username.nao.encontrado}") String usuarioNaoEncontrado,
        @Value("${username.ja.cadastrado}") String usuarioJaCadastrado,
        MeterRegistry registry
    ){
        UsuarioService.USERNAME_NAO_ENCONTRADO = usuarioNaoEncontrado;
        UsuarioService.USERNAME_JA_CADASTRADO = usuarioJaCadastrado;

        this.authUserErrorCounter = Counter.builder("auth_user_error")
            .description("Número de erros de autenticação do usuário")
            .register(registry);
    }


    public TokenDTO getToken(UsuarioEntity usuario){
        try {
            log.info("PASSO 4: Formatando DTO");
            var algorithm = Algorithm.HMAC256(secret);
            Instant expiresAt = dataExpiracao();

            String token = JWT.create()
            .withIssuer("AgenciaCRM")
            .withSubject(usuario.getClientId())
            .withExpiresAt(expiresAt)
            .sign(algorithm);

            TokenDTO tokenResponse = new TokenDTO(
                token,
                "Grant-Type: password",
                expiresAt
            );

        return tokenResponse;
        } catch (JWTCreationException exception){
            throw new UsuarioNaoEncontradoException("Erro ao gerar token");
        }

    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }

    public String getSubject(String tokenJWT) throws JWTDecodeException, JWTVerificationException {
        var algoritmo = Algorithm.HMAC256(secret);

        return JWT.require(algoritmo)
                    .withIssuer("AgenciaCRM")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
    }

    private Optional<UsuarioEntity> getUsuario(@NonNull String clientId){
        return usuarioRepository.findById(clientId);
    }

    public Boolean isTokenValid(String token) {
        return true;
    }

    public UsuarioDTO cadastrarUsuario(UsuarioRecordForm form) {
        // Verificar se o usuário já existe
        log.info("PASSO 1: Verificando se o username já está cadastrado");
        var usuarioExiste = getUsuario(form.clientId());

        // Nao deixa cadastrar se já existir
        if(usuarioExiste.isPresent()) throw new ClienteJaCadastradoException(USERNAME_JA_CADASTRADO);

        // Se não existir, encripta password e salva no banco
        log.info("PASSO 2: Encriptando password e salvando no banco");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        var encrypted = encoder.encode(form.clientSecret());

        log.info("PASSO 3: Criando objeto");
        UsuarioEntity usuario = UsuarioEntity.builder()
            .nomeCompleto(form.nomeCompleto())
            .clientId(form.clientId())
            .clientSecret(encrypted)
            .build();
        
        log.info("PASSO 4: Salvando no banco");
        usuarioRepository.save(usuario);

        log.info("PASSO 5: Retornando DTO");
        return new UsuarioDTO(form.nomeCompleto(), form.clientId());

    }

    public Boolean login(UsuarioLoginForm form) {
        Optional<UsuarioEntity> usuario = usuarioRepository.findById(form.clientId());

        if(usuario.isEmpty()) {
            authUserErrorCounter.increment();
            throw new UsuarioNaoEncontradoException(USERNAME_NAO_ENCONTRADO);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(encoder.matches(form.clientSecret(), usuario.get().getClientSecret())) return true;

        authUserErrorCounter.increment();
        return false;

    }

}
