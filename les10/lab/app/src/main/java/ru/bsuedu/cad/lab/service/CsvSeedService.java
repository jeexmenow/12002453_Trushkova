package ru.bsuedu.cad.lab.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bsuedu.cad.lab.entity.Category;
import ru.bsuedu.cad.lab.entity.Customer;
import ru.bsuedu.cad.lab.entity.Product;
import ru.bsuedu.cad.lab.repository.CategoryRepository;
import ru.bsuedu.cad.lab.repository.CustomerRepository;
import ru.bsuedu.cad.lab.repository.ProductRepository;

@Service
public class CsvSeedService {
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CsvSeedService(
            CategoryRepository categoryRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void loadSeedData() {
        if (categoryRepository.count() > 0 || customerRepository.count() > 0 || productRepository.count() > 0) {
            return;
        }

        var categoryByExternalId = loadCategories();
        loadCustomers();
        loadProducts(categoryByExternalId);
    }

    private Map<Integer, Category> loadCategories() {
        var map = new HashMap<Integer, Category>();
        readCsv("data/category.csv", 3, cells -> {
            var category = new Category();
            category.setName(cells[1].trim());
            category.setDescription(cells[2].trim());
            var saved = categoryRepository.create(category);
            map.put(Integer.parseInt(cells[0].trim()), saved);
        });
        return map;
    }

    private void loadCustomers() {
        readCsv("data/customer.csv", 5, cells -> {
            var customer = new Customer();
            customer.setName(cells[1].trim());
            customer.setEmail(cells[2].trim());
            customer.setPhone(cells[3].trim());
            customer.setAddress(cells[4].trim());
            customerRepository.create(customer);
        });
    }

    private void loadProducts(Map<Integer, Category> categoryByExternalId) {
        readCsv("data/product.csv", 9, cells -> {
            var category = categoryByExternalId.get(Integer.parseInt(cells[3].trim()));
            if (category == null) {
                throw new IllegalArgumentException("Unknown category id: " + cells[3]);
            }

            var product = new Product();
            product.setName(cells[1].trim());
            product.setDescription(cells[2].trim());
            product.setCategory(category);
            product.setPrice(new BigDecimal(cells[4].trim()));
            product.setStockQuantity(Integer.parseInt(cells[5].trim()));
            product.setImageUrl(cells[6].trim());
            product.setCreatedAt(LocalDate.parse(cells[7].trim()).atStartOfDay());
            product.setUpdatedAt(LocalDate.parse(cells[8].trim()).atStartOfDay());
            productRepository.create(product);
        });
    }

    private void readCsv(String resourcePath, int expectedColumns, RowHandler rowHandler) {
        try (var stream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IllegalStateException("Resource not found: " + resourcePath);
            }

            try (var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                boolean isHeader = true;
                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }
                    if (line.isBlank()) {
                        continue;
                    }
                    var cells = line.split(",", expectedColumns);
                    if (cells.length != expectedColumns) {
                        throw new IllegalArgumentException("Invalid CSV row: " + line);
                    }
                    rowHandler.handle(cells);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read csv " + resourcePath, e);
        }
    }

    @FunctionalInterface
    private interface RowHandler {
        void handle(String[] cells);
    }
}
