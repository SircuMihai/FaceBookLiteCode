package com.example.FacebookLiteCode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permite toate originile (pentru development)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // Sau specifică originile exacte (pentru production)
        // configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
        
        // Permite toate metodele HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Permite toate header-ele
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permite credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Expune header-ele în răspuns
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Timpul de cache pentru preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
