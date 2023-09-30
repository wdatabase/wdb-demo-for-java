package com.wdb.demo;
 
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import jakarta.servlet.MultipartConfigElement;

@Configuration
public class UploadConfig {
 
     @Bean
     public MultipartConfigElement getMultipartConfig() {
         MultipartConfigFactory config = new MultipartConfigFactory();
         config.setMaxRequestSize(DataSize.parse("10MB"));
         config.setMaxRequestSize(DataSize.parse("100MB"));
         config.setLocation("/tmp");
         return config.createMultipartConfig();
     }
    
}
