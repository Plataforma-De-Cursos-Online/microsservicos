package br.com.gateway.service.filter;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.security.Key;

@Component
public class AuthWebFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthWebFilter.class);

    private final Key secretKey;

    public AuthWebFilter(@Value("${api.security.token.secret}") String secretKeyString) {
        try {
            this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        } catch (Exception e) {
            logger.error("Erro ao criar a chave JWT: {}", e.getMessage());
            throw new IllegalArgumentException("Chave secreta inválida. Verifique a configuração em api.security.token.secret", e);
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = recoverToken(exchange);

        if (token == null) {
            logger.warn("Cabeçalho Authorization ausente ou inválido");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            logger.debug("Token JWT válido para a requisição: {}", exchange.getRequest().getURI());

            return chain.filter(exchange);
        } catch (JwtException e) {
            logger.warn("Falha na validação do token JWT", e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private String recoverToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.replace("Bearer ", "");
    }
}

