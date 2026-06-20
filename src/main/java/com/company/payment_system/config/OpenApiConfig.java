package com.company.payment_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentSystemOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Payment Integration System API")
                        .description(
                                "Real-world Payment Integration System built using Spring Boot")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Pranjali")
                                .email("your-email@example.com")));
    }
}