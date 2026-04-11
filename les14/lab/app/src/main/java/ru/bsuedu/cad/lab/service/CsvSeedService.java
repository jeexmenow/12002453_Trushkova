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

    public CsvSeedService(CategoryRepository categoryRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void loadSeedData() {
        if (categoryRepository.count() > 0 || customerRepository.count() > 0 || productRepository.count() > 0) {
            return;
        }
        var map = loadCategories();
        loadCustomers();
        loadProducts(map);
    }

    private Map<Integer, Category> loadCategories() {
        var map = new HashMap<Integer, Category>();
        readCsv("data/category.csv", 3, cells -> {
            var c = new Category();
            c.setName(cells[1].trim());
            c.setDescription(cells[2].trim());
            map.put(Integer.parseInt(cells[0].trim()), categoryRepository.create(c));
        });
        return map;
    }

    private void loadCustomers() {
        readCsv("data/customer.csv", 5, cells -> {
            var c = new Customer();
            c.setName(cells[1].trim());
            c.setEmail(cells[2].trim());
            c.setPhone(cells[3].trim());
            c.setAddress(cells[4].trim());
            customerRepository.create(c);
        });
    }

    private void loadProducts(Map<Integer, Category> categories) {
        readCsv("data/product.csv", 9, cells -> {
            var p = new Product();
            p.setName(cells[1].trim());
            p.setDescription(cells[2].trim());
            p.setCategory(categories.get(Integer.parseInt(cells[3].trim())));
            p.setPrice(new BigDecimal(cells[4].trim()));
            p.setStockQuantity(Integer.parseInt(cells[5].trim()));
            p.setImageUrl(cells[6].trim());
            p.setCreatedAt(LocalDate.parse(cells[7].trim()).atStartOfDay());
            p.setUpdatedAt(LocalDate.parse(cells[8].trim()).atStartOfDay());
            productRepository.create(p);
        });
    }

    private void readCsv(String resourcePath, int expectedColumns, RowHandler handler) {
        try (var stream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IllegalStateException("Resource not found: " + resourcePath);
            }
            try (var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                boolean head = true;
                while ((line = reader.readLine()) != null) {
                    if (head) {
                        head = false;
                        continue;
                    }
                    if (line.isBlank()) {
                        continue;
                    }
                    var cells = line.split(",", expectedColumns);
                    handler.handle(cells);
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
