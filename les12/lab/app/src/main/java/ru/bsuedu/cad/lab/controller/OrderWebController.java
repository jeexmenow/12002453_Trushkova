package ru.bsuedu.cad.lab.controller;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.bsuedu.cad.lab.controller.model.OrderCreateForm;
import ru.bsuedu.cad.lab.controller.model.OrderEditForm;
import ru.bsuedu.cad.lab.service.CatalogService;
import ru.bsuedu.cad.lab.service.OrderService;
import ru.bsuedu.cad.lab.service.dto.OrderCreateRequest;
import ru.bsuedu.cad.lab.service.dto.OrderUpdateRequest;

@Controller
@RequestMapping("/orders")
public class OrderWebController {
    private final OrderService orderService;
    private final CatalogService catalogService;

    public OrderWebController(OrderService orderService, CatalogService catalogService) {
        this.orderService = orderService;
        this.catalogService = catalogService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders-list";
    }

    @GetMapping("/new")
    public String createPage(Model model) {
        model.addAttribute("customers", catalogService.getCustomers());
        model.addAttribute("products", catalogService.getProducts());
        model.addAttribute("form", new OrderCreateForm());
        return "orders-create";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("form") OrderCreateForm form, Model model) {
        try {
            orderService.createOrder(new OrderCreateRequest(
                    form.getCustomerId(),
                    form.getShippingAddress(),
                    Map.of(form.getProductId(), form.getQuantity())));
            return "redirect:/orders";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("customers", catalogService.getCustomers());
            model.addAttribute("products", catalogService.getProducts());
            return "orders-create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Integer id, Model model) {
        var order = orderService.getOrderById(id).orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));
        var form = new OrderEditForm();
        form.setShippingAddress(order.shippingAddress());
        form.setStatus(order.status());
        model.addAttribute("orderId", id);
        model.addAttribute("form", form);
        return "orders-edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, @ModelAttribute("form") OrderEditForm form) {
        orderService.updateOrder(id, new OrderUpdateRequest(form.getShippingAddress(), form.getStatus()));
        return "redirect:/orders";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return "redirect:/orders";
    }
}
