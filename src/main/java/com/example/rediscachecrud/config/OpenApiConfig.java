package com.example.rediscachecrud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Servidor de Desarrollo");

        Info info = new Info()
                .title("API de Productos Electrónicos")
                .version("1.0")
                .description("API REST para gestión de productos electrónicos con caché Redis");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
