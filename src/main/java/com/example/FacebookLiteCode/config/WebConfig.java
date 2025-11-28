package com.example.FacebookLiteCode.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static files from /Static directory
        // This ensures all static resources (css, js, screens, etc.) are accessible
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/Static/")
                .setCachePeriod(0); // Disable cache for development
        
        // Explicitly handle screens directory for clarity
        registry.addResourceHandler("/screens/**")
                .addResourceLocations("classpath:/Static/screens/")
                .setCachePeriod(0);
    }
}

