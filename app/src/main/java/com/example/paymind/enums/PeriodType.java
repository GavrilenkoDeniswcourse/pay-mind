package com.example.paymind.enums;

public enum PeriodType {
    DAY(1, "День"),
    WEEK(2, "Неделя"),
    MONTH(3, "Месяц"),
    YEAR(4, "Год");
    private final int id;
    private final String name;


    PeriodType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static PeriodType fromId(int id) {
        for (PeriodType period : values()) {
            if (period.getId() == id) {
                return period;
            }
        }
        throw new IllegalArgumentException("No Period found with id: " + id);
    }
}
