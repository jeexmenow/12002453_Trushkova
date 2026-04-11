package ru.bsuedu.cad.lab.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.bsuedu.cad.lab.ConfigJpa;
import ru.bsuedu.cad.lab.entity.Customer;
import ru.bsuedu.cad.lab.entity.Product;
import ru.bsuedu.cad.lab.repository.CustomerRepository;
import ru.bsuedu.cad.lab.repository.OrderRepository;
import ru.bsuedu.cad.lab.repository.ProductRepository;
import ru.bsuedu.cad.lab.service.dto.OrderCreateRequest;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigJpa.class)
@Transactional
class OrderServiceIntegrationTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    private Integer customerId;
    private Integer productId;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();

        var customer = new Customer();
        customer.setName("Alex");
        customerId = customerRepository.save(customer).getId();

        var product = new Product();
        product.setName("Dog food");
        product.setPrice(new BigDecimal("500.00"));
        product.setStockQuantity(10);
        productId = productRepository.save(product).getId();
    }

    @Test
    void createOrder_successIntegration() {
        var order = orderService.createOrder(new OrderCreateRequest(customerId, "Moscow", Map.of(productId, 2)));

        assertEquals(1, orderRepository.count());
        assertEquals(new BigDecimal("1000.00"), order.getTotalPrice());
        assertEquals(8, productRepository.findById(productId).orElseThrow().getStockQuantity());
    }

    @Test
    void createOrder_failIntegration_notEnoughStock() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(new OrderCreateRequest(customerId, "Moscow", Map.of(productId, 20))));
        assertEquals(0, orderRepository.count());
    }
}
