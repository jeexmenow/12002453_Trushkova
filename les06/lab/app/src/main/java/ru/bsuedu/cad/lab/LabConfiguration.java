package ru.bsuedu.cad.lab;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import ru.bsuedu.cad.lab.impl.CSVParser;
import ru.bsuedu.cad.lab.impl.CategoryCSVParser;
import ru.bsuedu.cad.lab.impl.ConcreteCategoryProvider;
import ru.bsuedu.cad.lab.impl.ConcreteProductProvider;
import ru.bsuedu.cad.lab.impl.DataBaseRenderer;
import ru.bsuedu.cad.lab.impl.ResourceFileReader;

@Configuration
public class LabConfiguration {

    @Bean
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("petshop")
                .addScript("classpath:schema.sql")
                .build();
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    Reader productReader() {
        return new ResourceFileReader("product.csv");
    }

    @Bean
    Reader categoryReader() {
        return new ResourceFileReader("category.csv");
    }

    @Bean
    Parser parser() {
        return new CSVParser();
    }

    @Bean
    CategoryParser categoryParser() {
        return new CategoryCSVParser();
    }

    @Bean
    ProductProvider productProvider(
            @Qualifier("productReader") Reader productReader, Parser parser) {
        return new ConcreteProductProvider(productReader, parser);
    }

    @Bean
    CategoryProvider categoryProvider(
            @Qualifier("categoryReader") Reader categoryReader, CategoryParser categoryParser) {
        return new ConcreteCategoryProvider(categoryReader, categoryParser);
    }

    @Bean(name = "renderer")
    Renderer renderer(
            JdbcTemplate jdbcTemplate,
            ProductProvider productProvider,
            CategoryProvider categoryProvider) {
        return new DataBaseRenderer(jdbcTemplate, productProvider, categoryProvider);
    }

    @Bean
    CategoryRequest categoryRequest(JdbcTemplate jdbcTemplate) {
        return new CategoryRequest(jdbcTemplate);
    }
}
