package com.example.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${REACT_URL}")
    private String reactUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("CORS Configurations are being applied with reactUrl: " + reactUrl);
        registry.addMapping("/**")
                .allowedOrigins(reactUrl)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}