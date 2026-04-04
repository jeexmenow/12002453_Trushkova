package ru.bsuedu.cad.lab.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import ru.bsuedu.cad.lab.Category;
import ru.bsuedu.cad.lab.CategoryProvider;
import ru.bsuedu.cad.lab.Product;
import ru.bsuedu.cad.lab.ProductProvider;
import ru.bsuedu.cad.lab.Renderer;

public class DataBaseRenderer implements Renderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseRenderer.class);

    private final JdbcTemplate jdbcTemplate;
    private final ProductProvider productProvider;
    private final CategoryProvider categoryProvider;

    public DataBaseRenderer(
            JdbcTemplate jdbcTemplate,
            ProductProvider productProvider,
            CategoryProvider categoryProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.productProvider = productProvider;
        this.categoryProvider = categoryProvider;
    }

    @Override
    public void render() {
        List<Category> categories = categoryProvider.getCategories();
        List<Product> products = productProvider.getProducts();

        jdbcTemplate.batchUpdate(
                "INSERT INTO categories (category_id, name, description) VALUES (?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Category c = categories.get(i);
                        ps.setLong(1, c.getCategoryId());
                        ps.setString(2, c.getName());
                        ps.setString(3, c.getDescription());
                    }

                    @Override
                    public int getBatchSize() {
                        return categories.size();
                    }
                });

        jdbcTemplate.batchUpdate(
                """
                INSERT INTO products (
                    product_id, name, description, category_id, price, stock_quantity,
                    image_url, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Product p = products.get(i);
                        ps.setLong(1, p.getProductId());
                        ps.setString(2, p.getName());
                        ps.setString(3, p.getDescription());
                        ps.setInt(4, p.getCategoryId());
                        ps.setBigDecimal(5, p.getPrice());
                        ps.setInt(6, p.getStockQuantity());
                        ps.setString(7, p.getImageUrl());
                        ps.setDate(8, new Date(p.getCreatedAt().getTime()));
                        ps.setDate(9, new Date(p.getUpdatedAt().getTime()));
                    }

                    @Override
                    public int getBatchSize() {
                        return products.size();
                    }
                });

        LOGGER.info(
                "Данные из CSV загружены в БД H2: категорий — {}, товаров — {}",
                categories.size(),
                products.size());
    }
}
