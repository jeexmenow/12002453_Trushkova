package ru.bsuedu.cad.lab;

public class Category {

    private final long categoryId;
    private final String name;
    private final String description;

    public Category(long categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
