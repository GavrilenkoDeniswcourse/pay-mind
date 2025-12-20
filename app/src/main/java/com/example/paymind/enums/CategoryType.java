package com.example.paymind.enums;

public enum CategoryType {
    SUBSCRIPTION(1, "Подписка"),
    SERVICE(2, "Услуга");

    private final int id;
    private final String name;

    CategoryType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static CategoryType fromId(int id) {
        for (CategoryType category : values()) {
            if (category.getId() == id) {
                return category;
            }
        }
        throw new IllegalArgumentException("No Category found with id: " + id);
    }
}
