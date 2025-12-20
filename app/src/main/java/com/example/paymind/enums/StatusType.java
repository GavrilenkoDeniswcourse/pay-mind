package com.example.paymind.enums;

public enum StatusType {
    WAIT(1, "Ожидает"),
    PAID(2, "Оплачено");

    private final int id;
    private final String name;

    StatusType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static StatusType fromId(int id) {
        for (StatusType category : values()) {
            if (category.getId() == id) {
                return category;
            }
        }
        throw new IllegalArgumentException("No Status found with id: " + id);
    }
}
