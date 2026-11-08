package com.routecompany.routemath.routemath.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Libera o acesso à API para um front-end que roda em outra porta
 * (React/Vite, Live Server etc.). Ajuste as origens conforme o front do grupo.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:3000",   // React (Create React App / Next)
                        "http://localhost:5173",   // Vite
                        "http://127.0.0.1:5500",   // Live Server (VS Code)
                        "http://localhost:5500")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
