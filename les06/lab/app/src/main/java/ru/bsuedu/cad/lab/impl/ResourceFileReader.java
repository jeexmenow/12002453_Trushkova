package ru.bsuedu.cad.lab.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import ru.bsuedu.cad.lab.Reader;

public class ResourceFileReader implements Reader {

    private final String classpathResource;

    public ResourceFileReader(String classpathResource) {
        this.classpathResource = classpathResource;
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
