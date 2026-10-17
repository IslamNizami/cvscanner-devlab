package com.islamnizami.cvscannerdevlab.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cvScannerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CVScanner API")
                        .description("Automated Bulk CV Parsing and Extraction System for HR Teams")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Islam Nizami")
                                .email("islamnizami0046@gmail.com")));
    }
}