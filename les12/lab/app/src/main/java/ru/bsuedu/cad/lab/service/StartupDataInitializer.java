package ru.bsuedu.cad.lab.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupDataInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartupDataInitializer.class);
    private final CsvSeedService csvSeedService;
    private boolean initialized;

    public StartupDataInitializer(CsvSeedService csvSeedService) {
        this.csvSeedService = csvSeedService;
    }

    @EventListener
    public synchronized void onContextRefreshed(ContextRefreshedEvent event) {
        if (initialized || event.getApplicationContext().getParent() != null) {
            return;
        }
        csvSeedService.loadSeedData();
        initialized = true;
        LOGGER.info("CSV data initialized");
    }
}
