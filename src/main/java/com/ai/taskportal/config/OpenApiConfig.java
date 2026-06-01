package com.ai.taskportal.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI taskPortalOpenAPI() {

        return new OpenAPI()

                .info(
                        new Info()
                                .title("AI Powered Task Management Portal API")
                                .version("1.0.0")
                                .description("""
                                        AI-powered task management backend.

                                        Features:
                                        • JWT Authentication
                                        • User Registration & Login
                                        • Task CRUD Operations
                                        • AI Task Generation using Gemini
                                        • Audit Trail Tracking
                                        • Pagination & Filtering
                                        • PostgreSQL + Flyway
                                        • Swagger/OpenAPI Documentation
                                        """)
                                .contact(
                                        new Contact()
                                                .name("Satagouda Patil")
                                                .email("your-email@gmail.com")
                                )
                )

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME)
                )

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("Enter JWT token")
                                )
                );
    }
}
