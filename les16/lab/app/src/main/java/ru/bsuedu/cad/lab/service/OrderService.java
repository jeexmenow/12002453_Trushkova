package ru.bsuedu.cad.lab.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bsuedu.cad.lab.entity.OrderDetail;
import ru.bsuedu.cad.lab.entity.OrderEntity;
import ru.bsuedu.cad.lab.repository.CustomerRepository;
import ru.bsuedu.cad.lab.repository.OrderRepository;
import ru.bsuedu.cad.lab.repository.ProductRepository;
import ru.bsuedu.cad.lab.service.dto.OrderCreateRequest;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderEntity createOrder(OrderCreateRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Order items are empty");
        }
        var customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        var order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("NEW");
        order.setShippingAddress(request.shippingAddress());

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> entry : request.items().entrySet()) {
            var product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            int qty = entry.getValue();
            if (qty <= 0) {
                throw new IllegalArgumentException("Quantity should be > 0");
            }
            if (product.getStockQuantity() < qty) {
                throw new IllegalArgumentException("Not enough stock");
            }
            var detail = new OrderDetail();
            detail.setProduct(product);
            detail.setQuantity(qty);
            detail.setPrice(product.getPrice());
            order.addDetail(detail);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
            product.setStockQuantity(product.getStockQuantity() - qty);
            productRepository.save(product);
        }
        order.setTotalPrice(total);
        return orderRepository.save(order);
    }
}
