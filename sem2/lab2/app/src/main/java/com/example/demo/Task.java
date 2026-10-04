package com.example.demo;

public class Task {

    private static long nextId = 1L;

    private Long id;
    private String title;
    private String description;
    private boolean completed;

    public Task() {
        this.id = generateId();
    }

    public Task(String title, String description) {
        this.id = generateId();
        this.title = title;
        this.description = description;
        this.completed = false;
    }

    private static synchronized Long generateId() {
        return nextId++;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Task{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", description='" + description + '\''
                + ", completed=" + completed
                + '}';
    }
}
