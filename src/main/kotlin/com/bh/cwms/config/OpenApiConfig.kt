package com.bh.cwms.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    fun createApiScheme(): SecurityScheme = SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .bearerFormat("JWT")
        .scheme("bearer")

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .addSecurityItem(
            SecurityRequirement().addList("Bearer Authentication")
        ).components(
            Components().addSecuritySchemes("Bearer Authentication", createApiScheme())
        ).info(
            Info()
                .title("Crypto Wallet Management System")
                .description("This is the API documentation for the CWMS System that allows for user, wallet creation and transfer units from wallets.")
                .version("1.0")
        )
}