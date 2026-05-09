package com.app.b_and_t_lms.config;

import org.springframework.context.annotation.Configuration;
<<<<<<< HEAD
=======
import org.springframework.web.servlet.config.annotation.CorsRegistry;
>>>>>>> c13b675b96e1287ac668e4a860527469263bca48
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
<<<<<<< HEAD
                .addResourceLocations("file:C:/uploads/");
    }
}
=======
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600);
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/uploads/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
>>>>>>> c13b675b96e1287ac668e4a860527469263bca48
