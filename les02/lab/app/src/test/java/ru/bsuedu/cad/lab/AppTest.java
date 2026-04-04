package ru.bsuedu.cad.lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ru.bsuedu.cad.lab.impl.CSVParser;

class AppTest {

    @Test
    void springContextLoadsAndProvidesProducts() {
        try (AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(LabConfiguration.class)) {
            ProductProvider provider = ctx.getBean(ProductProvider.class);
            List<Product> products = provider.getProducts();
            assertFalse(products.isEmpty());
            assertEquals(10, products.size());
        }
    }

    @Test
    void csvParserHandlesQuotedCommas() {
        String line =
                "1,\"Name, with comma\",Short description,1,10,1,https://example.com/x.jpg,2025-01-01,2025-01-02";
        List<String> fields = CSVParser.splitCsvLine(line);
        assertEquals(9, fields.size());
        assertEquals("Name, with comma", fields.get(1));
    }
}
