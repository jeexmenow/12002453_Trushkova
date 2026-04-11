package ru.bsuedu.cad.lab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import ru.bsuedu.cad.lab.service.CatalogService;

@WebServlet("/api/products")
public class ProductRestServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var catalogService = SpringBeanProvider.getBean(getServletContext(), CatalogService.class);
        var products = catalogService.getProductStocks();

        resp.setContentType("application/json; charset=UTF-8");
        objectMapper.writeValue(resp.getOutputStream(), products);
    }
}
