package ru.bsuedu.cad.lab.impl;

import java.util.List;

import ru.bsuedu.cad.lab.Category;
import ru.bsuedu.cad.lab.CategoryParser;
import ru.bsuedu.cad.lab.CategoryProvider;
import ru.bsuedu.cad.lab.Reader;

public class ConcreteCategoryProvider implements CategoryProvider {

    private final Reader reader;
    private final CategoryParser parser;

    public ConcreteCategoryProvider(Reader reader, CategoryParser parser) {
        this.reader = reader;
        this.parser = parser;
    }

    @Override
    public List<Category> getCategories() {
        return parser.parse(reader.read());
    }
}
