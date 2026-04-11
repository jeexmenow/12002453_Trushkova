package ru.bsuedu.cad.lab.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import ru.bsuedu.cad.lab.service.OrderService;

@WebServlet("/orders")
public class OrderListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var orderService = SpringBeanProvider.getBean(getServletContext(), OrderService.class);
        var orders = orderService.getAllOrderViews();
        String cp = req.getContextPath();

        resp.setContentType("text/html; charset=UTF-8");
        var out = resp.getWriter();
        out.println("<!doctype html>");
        out.println("<html lang=\"ru\"><head><meta charset=\"UTF-8\"><title>Заказы</title></head><body>");
        out.println("<h1>Список заказов</h1>");
        out.println("<p><a href=\"" + cp + "/orders/new\"><button>Создать заказ</button></a></p>");
        out.println("<table border=\"1\" cellpadding=\"6\" cellspacing=\"0\">");
        out.println("<tr><th>ID</th><th>Клиент</th><th>Дата</th><th>Сумма</th><th>Статус</th><th>Адрес</th></tr>");
        for (var order : orders) {
            out.printf(
                    "<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>%n",
                    order.id(),
                    HtmlEscaper.escape(order.customerName()),
                    HtmlEscaper.escape(String.valueOf(order.orderDate())),
                    HtmlEscaper.escape(String.valueOf(order.totalPrice())),
                    HtmlEscaper.escape(order.status()),
                    HtmlEscaper.escape(order.shippingAddress()));
        }
        out.println("</table>");
        out.println("</body></html>");
    }
}
