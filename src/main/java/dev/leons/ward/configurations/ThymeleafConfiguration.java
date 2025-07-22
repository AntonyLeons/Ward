package dev.leons.ward.configurations;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * ThymeleafConfiguration provides custom Thymeleaf configuration for Solon framework
 * to ensure proper template resolution with .html suffix
 *
 * @author Assistant
 * @version 1.0.0
 */
@Configuration
public class ThymeleafConfiguration {

    /**
     * Creates a custom ClassLoaderTemplateResolver with proper prefix and suffix configuration
     *
     * @return configured ClassLoaderTemplateResolver
     */
    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(true);
        templateResolver.setOrder(1);
        return templateResolver;
    }

    /**
     * Creates a custom TemplateEngine with the configured template resolver
     *
     * @return configured TemplateEngine
     */
    @Bean
    public TemplateEngine templateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }
}