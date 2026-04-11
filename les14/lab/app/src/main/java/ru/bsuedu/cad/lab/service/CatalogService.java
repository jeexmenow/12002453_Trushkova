package ru.bsuedu.cad.lab.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bsuedu.cad.lab.repository.CustomerRepository;
import ru.bsuedu.cad.lab.repository.ProductRepository;
import ru.bsuedu.cad.lab.service.dto.SimpleCustomerView;
import ru.bsuedu.cad.lab.service.dto.SimpleProductView;

@Service
public class CatalogService {
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public CatalogService(ProductRepository productRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<SimpleCustomerView> getCustomers() {
        return customerRepository.getAll().stream()
                .map(c -> new SimpleCustomerView(c.getId(), c.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SimpleProductView> getProducts() {
        return productRepository.getAll().stream()
                .map(p -> new SimpleProductView(p.getId(), p.getName(), p.getStockQuantity()))
                .toList();
    }
}
