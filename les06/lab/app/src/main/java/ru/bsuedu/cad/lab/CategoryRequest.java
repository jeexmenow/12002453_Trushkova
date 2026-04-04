package ru.bsuedu.cad.lab;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import ru.bsuedu.cad.lab.impl.CategoryMultiProductRowMapper;

public class CategoryRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryRequest.class);

    private static final String SQL =
            """
            SELECT c.category_id, c.name, c.description, COUNT(p.product_id) AS product_count
            FROM categories c
            INNER JOIN products p ON c.category_id = p.category_id
            GROUP BY c.category_id, c.name, c.description
            HAVING COUNT(p.product_id) > 1
            ORDER BY c.category_id
            """;

    private final JdbcTemplate jdbcTemplate;
    private final CategoryMultiProductRowMapper rowMapper = new CategoryMultiProductRowMapper();

    public CategoryRequest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void execute() {
        List<CategoryProductSummary> rows = jdbcTemplate.query(SQL, rowMapper);
        LOGGER.info("Категории с количеством товаров больше 1: найдено записей {}", rows.size());
        for (CategoryProductSummary row : rows) {
            LOGGER.info(
                    "category_id={}, name={}, product_count={}",
                    row.categoryId(),
                    row.name(),
                    row.productCount());
        }
    }
}
