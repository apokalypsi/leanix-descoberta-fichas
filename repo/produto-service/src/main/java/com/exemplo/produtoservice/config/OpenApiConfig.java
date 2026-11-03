package com.exemplo.produtoservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "oauth2";

    @Bean
    OpenAPI produtoServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("produto-service API")
                        .version("1.0.0")
                        .description("CRUD de Produto com consumo de evento Kafka, cache/idempotencia Redis e integracao ViaCEP. Protegida por OAuth2 (JWT Bearer).")
                        .contact(new Contact().name("Time de Catalogo")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT OAuth2 (Authorization: Bearer <token>)")));
    }
}
