package ru.bsuedu.cad.lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import ru.bsuedu.cad.lab.impl.CSVParser;
import ru.bsuedu.cad.lab.impl.HTMLTableRenderer;

class AppTest {

    @Test
    void springContextLoadsAndProvidesProducts() {
        try (AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(LabApplicationConfiguration.class)) {
            ProductProvider provider = ctx.getBean(ProductProvider.class);
            assertFalse(provider.getProducts().isEmpty());
            assertEquals(10, provider.getProducts().size());
        }
    }

    @Test
    void primaryRendererIsHtml(@TempDir Path temp) throws Exception {
        Path out = temp.resolve("out.html");
        try (AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(LabApplicationConfiguration.class)) {
            HTMLTableRenderer html = ctx.getBean(HTMLTableRenderer.class);
            ReflectionTestUtils.setField(html, "outputFile", out.toString());
            Renderer primary = ctx.getBean(Renderer.class);
            assertTrue(primary instanceof HTMLTableRenderer);
            primary.render();
        }
        assertTrue(Files.exists(out));
        String content = Files.readString(out);
        assertTrue(content.contains("<table"));
        assertTrue(content.contains("Сухой корм для собак"));
    }

    @Test
    void csvParserHandlesQuotedCommas() {
        String line =
                "1,\"Name, with comma\",Short description,1,10,1,https://example.com/x.jpg,2025-01-01,2025-01-02";
        assertEquals(9, CSVParser.splitCsvLine(line).size());
        assertEquals("Name, with comma", CSVParser.splitCsvLine(line).get(1));
    }
}
