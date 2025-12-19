package service.sllbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.nio.file.Paths;
import java.util.Locale;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${appointment.image.upload-dir:uploads/appointment-images/}")
    private String appointmentUploadDir;
    
    @Value("${product.feedback.image.upload-dir:uploads/product-feedback-images/}")
    private String productFeedbackUploadDir;
    
    @Value("${product.image.upload-dir:uploads/product-images/}")
    private String productUploadDir;
    
    @Value("${service.image.upload-dir:uploads/service-images/}")
    private String serviceUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded appointment images from the upload directory
        String appointmentUploadPath = Paths.get(appointmentUploadDir).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/appointment-images/**")
                .addResourceLocations(appointmentUploadPath);
        
        // Serve uploaded product feedback images from the upload directory
        String productFeedbackUploadPath = Paths.get(productFeedbackUploadDir).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/product-feedback-images/**")
                .addResourceLocations(productFeedbackUploadPath);
        
        // Serve uploaded product images from the upload directory
        String productUploadPath = Paths.get(productUploadDir).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/product-images/**")
                .addResourceLocations(productUploadPath);
        
        // Serve uploaded service images from the upload directory
        String serviceUploadPath = Paths.get(serviceUploadDir).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/service-images/**")
                .addResourceLocations(serviceUploadPath);
    }


    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("classpath:messages");
        ms.setDefaultEncoding("UTF-8");
        ms.setFallbackToSystemLocale(false);
        return ms;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
