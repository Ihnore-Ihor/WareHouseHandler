package com.example.coursework.ProductManagment;

public class ProductTypeValue {
    private String type;
    private Double totalValue;

    public ProductTypeValue(String type, Double totalValue) {
        this.type = type;
        this.totalValue = totalValue;
    }

    public String getType() {
        return type;
    }

    public Double getTotalValue() {
        return totalValue;
    }
}


