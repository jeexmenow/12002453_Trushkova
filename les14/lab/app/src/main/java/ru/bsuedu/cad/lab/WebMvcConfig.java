package ru.bsuedu.cad.lab;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.bsuedu.cad.lab.controller")
public class WebMvcConfig implements WebMvcConfigurer {
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        var r = new SpringResourceTemplateResolver();
        r.setPrefix("/WEB-INF/templates/");
        r.setSuffix(".html");
        r.setCharacterEncoding("UTF-8");
        r.setCacheable(false);
        return r;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        var e = new SpringTemplateEngine();
        e.setTemplateResolver(templateResolver());
        return e;
    }

    @Bean
    public ViewResolver viewResolver() {
        var vr = new ThymeleafViewResolver();
        vr.setTemplateEngine(templateEngine());
        vr.setCharacterEncoding("UTF-8");
        return vr;
    }
}
