package ru.bsuedu.cad.lab.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.bsuedu.cad.lab.service.OrderService;
import ru.bsuedu.cad.lab.service.dto.OrderCreateRequest;
import ru.bsuedu.cad.lab.service.dto.OrderUpdateRequest;
import ru.bsuedu.cad.lab.service.dto.OrderView;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {
    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderView> getAll() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderView getById(@PathVariable Integer id) {
        return orderService.getOrderById(id).orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderView create(@RequestBody OrderCreateRequest request) {
        return orderService.createOrder(request);
    }

    @PutMapping("/{id}")
    public OrderView update(@PathVariable Integer id, @RequestBody OrderUpdateRequest request) {
        return orderService.updateOrder(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        orderService.deleteOrder(id);
    }
}
