package ru.bsuedu.cad.lab.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Reader;

@Component
public class ResourceFileReader implements Reader {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String classpathResource;

    public ResourceFileReader(
            @Value("#{'${app.products.csv-file}'.trim()}") String classpathResource) {
        this.classpathResource = classpathResource;
    }

    @PostConstruct
    void afterInit() {
        System.out.println(
                "[Lifecycle] ResourceFileReader полностью инициализирован: " + LocalDateTime.now().format(TS));
    }

    @Override
    public String read() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream in = loader.getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new IllegalStateException("Ресурс не найден на classpath: " + classpathResource);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
