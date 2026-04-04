package ru.bsuedu.cad.lab.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Parser;
import ru.bsuedu.cad.lab.Product;

@Component
public class CSVParser implements Parser {

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Override
    public List<Product> parse(String data) {
        List<Product> products = new ArrayList<>();
        String[] lines = data.split("\\R");
        boolean headerSkipped = false;
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            if (!headerSkipped) {
                headerSkipped = true;
                continue;
            }
            List<String> fields = splitCsvLine(line);
            if (fields.size() < 9) {
                throw new IllegalArgumentException("Неверное число полей в строке CSV: " + line);
            }
            products.add(toProduct(fields));
        }
        return products;
    }

    private static Product toProduct(List<String> fields) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN, Locale.ROOT);
            return new Product(
                    Long.parseLong(fields.get(0).trim()),
                    fields.get(1).trim(),
                    fields.get(2).trim(),
                    Integer.parseInt(fields.get(3).trim()),
                    new BigDecimal(fields.get(4).trim()),
                    Integer.parseInt(fields.get(5).trim()),
                    fields.get(6).trim(),
                    df.parse(fields.get(7).trim()),
                    df.parse(fields.get(8).trim()));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Ошибка разбора даты в строке товара", e);
        }
    }

    public static List<String> splitCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields;
    }
}
