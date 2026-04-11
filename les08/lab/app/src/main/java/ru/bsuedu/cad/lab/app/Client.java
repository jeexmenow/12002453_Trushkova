package ru.bsuedu.cad.lab.app;

import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.bsuedu.cad.lab.service.CsvSeedService;
import ru.bsuedu.cad.lab.service.OrderService;

@Component
public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private final CsvSeedService csvSeedService;
    private final OrderService orderService;

    public Client(CsvSeedService csvSeedService, OrderService orderService) {
        this.csvSeedService = csvSeedService;
        this.orderService = orderService;
    }

    public void run() {
        csvSeedService.loadSeedData();
        LOGGER.info("Seed data loaded");

        var items = new LinkedHashMap<Integer, Integer>();
        items.put(1, 2);
        items.put(3, 1);

        var createdOrder = orderService.createOrder(1, "Белгород, ул. Победы, д. 1", items);
        LOGGER.info(
                "Created order id={}, customerId={}, total={}, status={}",
                createdOrder.getId(),
                createdOrder.getCustomer().getId(),
                createdOrder.getTotalPrice(),
                createdOrder.getStatus());

        var allOrders = orderService.getAllOrders();
        LOGGER.info("Orders in DB after creation: {}", allOrders.size());
        for (var order : allOrders) {
            LOGGER.info(
                    "Order id={}, customerId={}, date={}, total={}, status={}, address={}",
                    order.getId(),
                    order.getCustomer().getId(),
                    order.getOrderDate(),
                    order.getTotalPrice(),
                    order.getStatus(),
                    order.getShippingAddress());
        }
    }
}
