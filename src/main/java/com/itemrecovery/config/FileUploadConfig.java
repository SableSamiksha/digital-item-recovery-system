package com.itemrecovery.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.servlet.MultipartConfigElement;

/**
 * Configuration for file upload handling.
 * Configures multipart file upload settings.
 */
@Configuration
public class FileUploadConfig {
    
    /**
     * Configure multipart resolver for file uploads.
     * @return MultipartResolver instance
     */
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
    
    /**
     * Configure multipart configuration.
     * Sets maximum file size and request size.
     * @return MultipartConfigElement instance
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // Maximum file size: 5MB
        factory.setMaxFileSize(DataSize.ofMegabytes(5));
        
        // Maximum request size: 10MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(10));
        
        return factory.createMultipartConfig();
    }
}
