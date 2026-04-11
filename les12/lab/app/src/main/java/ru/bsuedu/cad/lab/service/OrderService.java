package ru.bsuedu.cad.lab.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bsuedu.cad.lab.entity.OrderDetail;
import ru.bsuedu.cad.lab.entity.OrderEntity;
import ru.bsuedu.cad.lab.repository.CustomerRepository;
import ru.bsuedu.cad.lab.repository.OrderRepository;
import ru.bsuedu.cad.lab.repository.ProductRepository;
import ru.bsuedu.cad.lab.service.dto.OrderCreateRequest;
import ru.bsuedu.cad.lab.service.dto.OrderUpdateRequest;
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

    @Transactional(readOnly = true)
    public List<OrderView> getAllOrders() {
        return orderRepository.getAll().stream().map(this::toView).toList();
    }

    @Transactional(readOnly = true)
    public Optional<OrderView> getOrderById(Integer id) {
        return orderRepository.getRecordById(id).map(this::toView);
    }

    @Transactional
    public OrderView createOrder(OrderCreateRequest request) {
        var customer = customerRepository.getRecordById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Клиент не найден: " + request.customerId()));

        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Заказ не должен быть пустым");
        }

        var order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("NEW");
        order.setShippingAddress(request.shippingAddress());

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> entry : request.items().entrySet()) {
            var product = productRepository.getRecordById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + entry.getKey()));
            int qty = entry.getValue();
            if (qty <= 0) {
                throw new IllegalArgumentException("Количество должно быть > 0");
            }
            if (product.getStockQuantity() < qty) {
                throw new IllegalArgumentException("Недостаточно товара на складе: " + product.getName());
            }

            var detail = new OrderDetail();
            detail.setProduct(product);
            detail.setQuantity(qty);
            detail.setPrice(product.getPrice());
            order.addDetail(detail);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
            product.setStockQuantity(product.getStockQuantity() - qty);
            productRepository.create(product);
        }

        order.setTotalPrice(total);
        return toView(orderRepository.create(order));
    }

    @Transactional
    public OrderView updateOrder(Integer id, OrderUpdateRequest request) {
        var order = orderRepository.getRecordById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + id));
        if (request.shippingAddress() != null && !request.shippingAddress().isBlank()) {
            order.setShippingAddress(request.shippingAddress());
        }
        if (request.status() != null && !request.status().isBlank()) {
            order.setStatus(request.status());
        }
        return toView(orderRepository.create(order));
    }

    @Transactional
    public void deleteOrder(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Заказ не найден: " + id);
        }
        orderRepository.deleteById(id);
    }

    private OrderView toView(OrderEntity order) {
        return new OrderView(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getOrderDate(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getShippingAddress());
    }
}
