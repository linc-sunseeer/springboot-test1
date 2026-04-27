package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final String uploadResourceLocation;

    public StaticResourceConfig(@Value("${app.menu-image.public-root:uploads}") String uploadRoot) {
        Path uploadPath = Paths.get(uploadRoot).toAbsolutePath().normalize();
        String normalizedPath = uploadPath.toString().replace("\\", "/");
        this.uploadResourceLocation = "file:" + (normalizedPath.endsWith("/") ? normalizedPath : normalizedPath + "/");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadResourceLocation);
    }
}
