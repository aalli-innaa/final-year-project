package kg.manas.skincare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Указываем, что запросы к /storage/**
        // должны искать файлы в реальной папке storage в корне проекта
        String storagePath = Paths.get("storage").toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/storage/**")
                .addResourceLocations(storagePath);
    }
}