package ru.bsuedu.cad.lab.impl;

import java.util.ArrayList;
import java.util.List;

import ru.bsuedu.cad.lab.Category;
import ru.bsuedu.cad.lab.CategoryParser;

public class CategoryCSVParser implements CategoryParser {

    @Override
    public List<Category> parse(String data) {
        List<Category> categories = new ArrayList<>();
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
            List<String> fields = CSVParser.splitCsvLine(line);
            if (fields.size() < 3) {
                throw new IllegalArgumentException("Неверное число полей в строке CSV категории: " + line);
            }
            categories.add(
                    new Category(
                            Long.parseLong(fields.get(0).trim()),
                            fields.get(1).trim(),
                            fields.get(2).trim()));
        }
        return categories;
    }
}
