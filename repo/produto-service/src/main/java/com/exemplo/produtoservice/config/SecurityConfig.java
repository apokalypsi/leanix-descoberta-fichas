package com.exemplo.produtoservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Seguranca baseada em OAuth2 Resource Server: a API valida um token JWT
 * (Bearer) emitido por um Authorization Server externo (issuer configurado em
 * {@code spring.security.oauth2.resourceserver.jwt.issuer-uri}).
 *
 * <ul>
 *   <li>Endpoints de observabilidade e documentacao ficam publicos.</li>
 *   <li>Escritas (POST/PUT/DELETE) exigem o scope {@code produtos:write}.</li>
 *   <li>Leituras (GET) exigem o scope {@code produtos:read}.</li>
 * </ul>
 * A API e stateless (sem sessao) e sem CSRF (nao usa cookie de sessao).
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/health/**",
                                "/actuator/info",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/produtos/**")
                        .hasAuthority("SCOPE_produtos:read")
                        .requestMatchers(HttpMethod.POST, "/api/v1/produtos/**")
                        .hasAuthority("SCOPE_produtos:write")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/produtos/**")
                        .hasAuthority("SCOPE_produtos:write")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/produtos/**")
                        .hasAuthority("SCOPE_produtos:write")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    /**
     * Converte os {@code scope}/{@code scp} do JWT em authorities prefixadas
     * com {@code SCOPE_}.
     */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
        scopes.setAuthorityPrefix("SCOPE_");
        scopes.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(scopes);
        return converter;
    }
}
