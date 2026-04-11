package ru.bsuedu.cad.lab.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import ru.bsuedu.cad.lab.service.CatalogService;
import ru.bsuedu.cad.lab.service.OrderService;

@WebServlet("/orders/new")
public class OrderCreateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var catalogService = SpringBeanProvider.getBean(getServletContext(), CatalogService.class);
        var customers = catalogService.getCustomers();
        var products = catalogService.getProducts();
        String cp = req.getContextPath();

        resp.setContentType("text/html; charset=UTF-8");
        var out = resp.getWriter();
        out.println("<!doctype html>");
        out.println("<html lang=\"ru\"><head><meta charset=\"UTF-8\"><title>Создание заказа</title></head><body>");
        out.println("<h1>Создать заказ</h1>");
        out.println("<form method=\"post\" action=\"" + cp + "/orders/new\">");
        out.println("<label>Клиент:</label><br/>");
        out.println("<select name=\"customerId\" required>");
        for (var customer : customers) {
            out.printf("<option value=\"%d\">%s (id=%d)</option>%n", customer.id(), customer.name(), customer.id());
        }
        out.println("</select><br/><br/>");

        out.println("<label>Адрес доставки:</label><br/>");
        out.println("<input type=\"text\" name=\"shippingAddress\" size=\"60\" required/><br/><br/>");

        out.println("<label>Товар:</label><br/>");
        out.println("<select name=\"productId\" required>");
        for (var product : products) {
            out.printf(
                    "<option value=\"%d\">%s (остаток: %d)</option>%n",
                    product.id(), HtmlEscaper.escape(product.name()), product.stockQuantity());
        }
        out.println("</select><br/><br/>");

        out.println("<label>Количество:</label><br/>");
        out.println("<input type=\"number\" name=\"quantity\" min=\"1\" value=\"1\" required/><br/><br/>");

        out.println("<button type=\"submit\">Создать</button>");
        out.println("</form>");
        out.println("<p><a href=\"" + cp + "/orders\">Назад к заказам</a></p>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var orderService = SpringBeanProvider.getBean(getServletContext(), OrderService.class);
        try {
            Integer customerId = Integer.valueOf(req.getParameter("customerId"));
            String shippingAddress = req.getParameter("shippingAddress");
            Integer productId = Integer.valueOf(req.getParameter("productId"));
            Integer quantity = Integer.valueOf(req.getParameter("quantity"));

            orderService.createOrder(customerId, shippingAddress, Map.of(productId, quantity));
            resp.sendRedirect(req.getContextPath() + "/orders");
        } catch (Exception e) {
            resp.setContentType("text/plain; charset=UTF-8");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Ошибка при создании заказа: " + e.getMessage());
        }
    }
}
