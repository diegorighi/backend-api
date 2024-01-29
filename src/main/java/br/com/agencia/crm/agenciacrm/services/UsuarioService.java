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
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.agencia.crm.agenciacrm.domain.entities.UsuarioEntity;
import br.com.agencia.crm.agenciacrm.domain.records.dto.TokenDTO;
import br.com.agencia.crm.agenciacrm.domain.records.dto.UsuarioDTO;
import br.com.agencia.crm.agenciacrm.domain.records.forms.UsuarioRecordForm;
import br.com.agencia.crm.agenciacrm.exceptions.ClienteJaCadastradoException;
import br.com.agencia.crm.agenciacrm.exceptions.UsuarioNaoEncontradoException;
import br.com.agencia.crm.agenciacrm.repositories.UsuarioRepository;
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
    
    @Autowired
    public UsuarioService(
        @Value("${username.nao.encontrado}") String usuarioNaoEncontrado,
        @Value("${username.ja.cadastrado}") String usuarioJaCadastrado
    ){
        UsuarioService.USERNAME_NAO_ENCONTRADO = usuarioNaoEncontrado;
        UsuarioService.USERNAME_JA_CADASTRADO = usuarioJaCadastrado;
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

    public String getSubject(String tokenJWT) {
        try {
                var algoritmo = Algorithm.HMAC256(secret);
                return JWT.require(algoritmo)
                                .withIssuer("AgenciaCRM")
                                .build()
                                .verify(tokenJWT)
                                .getSubject();
        } catch (JWTVerificationException exception) {
                throw new RuntimeException("Token JWT inválido ou expirado!");
        }
}

    private Optional<UsuarioEntity> getUsuario(@NonNull String clientId){
        return usuarioRepository.findById(clientId);
    }

    private Boolean validateCredentials(@NonNull String clientId, @NonNull String clientSecret){
        // Chamada ao banco de dados com credenciais informadas
        Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(clientId);

        // Se o usuário existir, verificar se a senha está correta, se não, lança exceção
        usuarioEntity.ifPresentOrElse((usuario) -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            encoder.matches(clientSecret, usuario.getClientSecret());
        }, () -> {
            throw new UsuarioNaoEncontradoException(USERNAME_NAO_ENCONTRADO);
        });

        // Sempre vai ser TRUE, pois se não existir, lança exceção
        return Boolean.TRUE;
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

}
