package ru.bsuedu.cad.lab;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(LabConfiguration.class)) {
            Renderer renderer = ctx.getBean("renderer", Renderer.class);
            renderer.render();
        }
    }
}
