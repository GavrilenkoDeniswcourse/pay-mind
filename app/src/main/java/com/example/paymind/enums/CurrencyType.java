package com.example.paymind.enums;

public enum CurrencyType {
    USD(1, "USD", "$"),
    EUR(2, "EUR", "€"),
    RUB(3, "RUB", "₽"),
    KZT(4, "KZT", "₸"),
    TRY(5, "TRY", "₺"),
    INR(6, "INR", "₹");

    private final int id;
    private final String code;
    private final String symbol;

    CurrencyType(int id, String code, String symbol) {
        this.id = id;
        this.code = code;
        this.symbol = symbol;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getSymbol() {
        return symbol;
    }

    public static CurrencyType fromId(int id) {
        for (CurrencyType currency : values()) {
            if (currency.getId() == id) {
                return currency;
            }
        }
        throw new IllegalArgumentException("No Currency found with id: " + id);
    }
}
