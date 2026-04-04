package ru.bsuedu.cad.lab.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.bsuedu.cad.lab.Product;
import ru.bsuedu.cad.lab.ProductProvider;
import ru.bsuedu.cad.lab.Renderer;

public class ConsoleTableRenderer implements Renderer {

    private static final int MAX_CELL_WIDTH = 36;
    private static final String DATE_DISPLAY = "yyyy-MM-dd";

    private final ProductProvider provider;

    public ConsoleTableRenderer(ProductProvider provider) {
        this.provider = provider;
    }

    @Override
    public void render() {
        List<Product> products = provider.getProducts();
        SimpleDateFormat df = new SimpleDateFormat(DATE_DISPLAY, Locale.ROOT);

        String[] headers = {
            "ID",
            "Название",
            "Описание",
            "Кат.",
            "Цена",
            "Остаток",
            "URL изображения",
            "Создан",
            "Обновлён"
        };

        List<String[]> rows = new ArrayList<>();
        for (Product p : products) {
            rows.add(
                    new String[] {
                        Long.toString(p.getProductId()),
                        p.getName(),
                        p.getDescription(),
                        Integer.toString(p.getCategoryId()),
                        p.getPrice().toPlainString(),
                        Integer.toString(p.getStockQuantity()),
                        p.getImageUrl(),
                        df.format(p.getCreatedAt()),
                        df.format(p.getUpdatedAt())
                    });
        }

        int cols = headers.length;
        int[] widths = new int[cols];
        for (int c = 0; c < cols; c++) {
            widths[c] = Math.min(MAX_CELL_WIDTH, headers[c].length());
        }
        for (String[] row : rows) {
            for (int c = 0; c < cols; c++) {
                int len = Math.min(MAX_CELL_WIDTH, row[c].length());
                widths[c] = Math.max(widths[c], len);
            }
        }

        String horizontal = buildHorizontal(widths);
        System.out.println(horizontal);
        System.out.println(buildRow(headers, widths));
        System.out.println(horizontal);
        for (String[] row : rows) {
            System.out.println(buildRow(row, widths));
        }
        System.out.println(horizontal);
    }

    private static String buildHorizontal(int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append('+');
        for (int w : widths) {
            sb.append("-".repeat(w + 2));
            sb.append('+');
        }
        return sb.toString();
    }

    private static String buildRow(String[] cells, int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (int i = 0; i < cells.length; i++) {
            String cell = truncate(cells[i], widths[i]);
            sb.append(' ');
            sb.append(padRight(cell, widths[i]));
            sb.append(" |");
        }
        return sb.toString();
    }

    private static String truncate(String s, int maxWidth) {
        if (s.length() <= maxWidth) {
            return s;
        }
        if (maxWidth <= 3) {
            return s.substring(0, maxWidth);
        }
        return s.substring(0, maxWidth - 3) + "...";
    }

    private static String padRight(String s, int width) {
        if (s.length() >= width) {
            return s;
        }
        return s + " ".repeat(width - s.length());
    }
}
