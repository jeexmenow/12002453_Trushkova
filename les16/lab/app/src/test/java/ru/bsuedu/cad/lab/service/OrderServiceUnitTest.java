package ru.bsuedu.cad.lab.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bsuedu.cad.lab.entity.Customer;
import ru.bsuedu.cad.lab.entity.OrderEntity;
import ru.bsuedu.cad.lab.entity.Product;
import ru.bsuedu.cad.lab.repository.CustomerRepository;
import ru.bsuedu.cad.lab.repository.OrderRepository;
import ru.bsuedu.cad.lab.repository.ProductRepository;
import ru.bsuedu.cad.lab.service.dto.OrderCreateRequest;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_success() {
        var customer = new Customer();
        var product = new Product();
        product.setName("Food");
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(10);

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(productRepository.findById(5)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = orderService.createOrder(new OrderCreateRequest(1, "Belgorod", Map.of(5, 2)));

        assertEquals(new BigDecimal("200.00"), result.getTotalPrice());
        assertEquals("NEW", result.getStatus());
        assertEquals(1, result.getDetails().size());
        verify(productRepository).save(product);
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void createOrder_failWhenNotEnoughStock() {
        var customer = new Customer();
        var product = new Product();
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(1);

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(productRepository.findById(5)).thenReturn(Optional.of(product));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(new OrderCreateRequest(1, "Belgorod", Map.of(5, 2))));
    }
}
