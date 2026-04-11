package ru.bsuedu.cad.lab.web;

import jakarta.servlet.ServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public final class SpringBeanProvider {
    private SpringBeanProvider() {}

    public static <T> T getBean(ServletContext servletContext, Class<T> beanClass) {
        WebApplicationContext context =
                WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return context.getBean(beanClass);
    }
}
