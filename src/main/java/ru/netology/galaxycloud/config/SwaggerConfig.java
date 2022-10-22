package ru.netology.galaxycloud.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Storage Galaxy Cloud API",
        version = "v1.0",
        description = "Documentation APIs v1.0"))
public class SwaggerConfig {

}

