package ru.bsuedu.cad.lab.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bsuedu.cad.lab.entity.OrderDetail;
import ru.bsuedu.cad.lab.entity.OrderEntity;
import ru.bsuedu.cad.lab.repository.CustomerRepository;
import ru.bsuedu.cad.lab.repository.OrderRepository;
import ru.bsuedu.cad.lab.repository.ProductRepository;
import ru.bsuedu.cad.lab.service.dto.OrderView;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderEntity createOrder(Integer customerId, String shippingAddress, Map<Integer, Integer> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order cannot be empty");
        }

        var customer = customerRepository.getRecordById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        var order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("NEW");
        order.setShippingAddress(shippingAddress);

        BigDecimal total = BigDecimal.ZERO;
        for (var entry : items.entrySet()) {
            var product = productRepository.getRecordById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + entry.getKey()));
            var quantity = entry.getValue();
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Invalid quantity for product " + product.getId());
            }
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough stock for product " + product.getId());
            }

            var detail = new OrderDetail();
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setPrice(product.getPrice());
            order.addDetail(detail);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.create(product);
        }

        order.setTotalPrice(total);
        return orderRepository.create(order);
    }

    @Transactional(readOnly = true)
    public List<OrderView> getAllOrderViews() {
        return orderRepository.getAll().stream()
                .map(order -> new OrderView(
                        order.getId(),
                        order.getCustomer().getName(),
                        order.getOrderDate(),
                        order.getTotalPrice(),
                        order.getStatus(),
                        order.getShippingAddress()))
                .toList();
    }
}
