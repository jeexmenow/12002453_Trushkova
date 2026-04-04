package ru.bsuedu.cad.lab;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(LabApplicationConfiguration.class)) {
            Renderer renderer = ctx.getBean(Renderer.class);
            renderer.render();
        }
    }
}
