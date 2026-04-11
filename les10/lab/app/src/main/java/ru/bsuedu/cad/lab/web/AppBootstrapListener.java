package ru.bsuedu.cad.lab.web;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bsuedu.cad.lab.service.CsvSeedService;

public class AppBootstrapListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppBootstrapListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        var seeder = SpringBeanProvider.getBean(sce.getServletContext(), CsvSeedService.class);
        seeder.loadSeedData();
        LOGGER.info("Seed data initialized for web application");
    }
}
