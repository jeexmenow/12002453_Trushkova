package ru.bsuedu.cad.lab.impl;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Product;
import ru.bsuedu.cad.lab.ProductProvider;
import ru.bsuedu.cad.lab.Renderer;

@Component
@Primary
public class HTMLTableRenderer implements Renderer {

    private static final String DATE_DISPLAY = "yyyy-MM-dd";

    private final ProductProvider provider;
    private final String outputFile;

    public HTMLTableRenderer(
            ProductProvider provider,
            @Value("#{'${app.products.html-output}'.trim()}") String outputFile) {
        this.provider = provider;
        this.outputFile = outputFile;
    }

    @Override
    public void render() {
        List<Product> products = provider.getProducts();
        SimpleDateFormat df = new SimpleDateFormat(DATE_DISPLAY, Locale.ROOT);

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"ru\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\"/>\n");
        html.append("<title>Товары зоомагазина</title>\n");
        html.append("</head>\n<body>\n<table border=\"1\" cellpadding=\"6\" cellspacing=\"0\">\n");
        html.append("<thead><tr>");
        appendHeader(html, "ID");
        appendHeader(html, "Название");
        appendHeader(html, "Описание");
        appendHeader(html, "Кат.");
        appendHeader(html, "Цена");
        appendHeader(html, "Остаток");
        appendHeader(html, "URL изображения");
        appendHeader(html, "Создан");
        appendHeader(html, "Обновлён");
        html.append("</tr></thead>\n<tbody>\n");

        for (Product p : products) {
            html.append("<tr>");
            appendCell(html, Long.toString(p.getProductId()));
            appendCell(html, p.getName());
            appendCell(html, p.getDescription());
            appendCell(html, Integer.toString(p.getCategoryId()));
            appendCell(html, p.getPrice().toPlainString());
            appendCell(html, Integer.toString(p.getStockQuantity()));
            appendCell(html, p.getImageUrl());
            appendCell(html, df.format(p.getCreatedAt()));
            appendCell(html, df.format(p.getUpdatedAt()));
            html.append("</tr>\n");
        }

        html.append("</tbody>\n</table>\n</body>\n</html>\n");

        try {
            Path path = Path.of(outputFile);
            Files.writeString(path, html.toString(), StandardCharsets.UTF_8);
            System.out.println("HTML-таблица записана в файл: " + path.toAbsolutePath().normalize());
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось записать HTML-файл", e);
        }
    }

    private static void appendHeader(StringBuilder html, String text) {
        html.append("<th>").append(escapeHtml(text)).append("</th>");
    }

    private static void appendCell(StringBuilder html, String text) {
        html.append("<td>").append(escapeHtml(text)).append("</td>");
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
