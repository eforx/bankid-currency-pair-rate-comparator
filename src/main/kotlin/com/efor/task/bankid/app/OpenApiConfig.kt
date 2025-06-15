package com.efor.task.bankid.app

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI {
        val securitySchemeName = "basicAuth"

        return OpenAPI()
            .info(
                Info()
                    .title("Currency Exchange API")
                    .description("API for currency exchange rates and comparison between different providers")
                    .version("1.0.0")
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0.html"),
                    ),
            )
            .components(
                io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("basic"),
                    ),
            )
            .addSecurityItem(
                SecurityRequirement().addList(securitySchemeName),
            )
            .addServersItem(
                Server()
                    .url("/")
                    .description("Local server"),
            )
    }
}
