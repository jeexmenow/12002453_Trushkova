package ru.bsuedu.cad.lab;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.bsuedu.cad.lab.app.Client;

public class App {
    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(ConfigJpa.class)) {
            var client = ctx.getBean(Client.class);
            client.run();
        }
    }
}
