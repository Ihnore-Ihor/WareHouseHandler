package com.example.coursework.ProductManagment;

import com.example.coursework.LoggingHandler.LoggingHandler;

import java.time.LocalDate;

//Name | Type | Number of units | Date of manufacture | Shelf life | Price per unit
public class Product {
    private String name;
    private String type;
    private int numberOfUnits;
    private LocalDate dateOfManufacture;
    private LocalDate expirationDate;
    private double pricePerUnit;

    private LoggingHandler logger = null;

    public Product(LoggingHandler logger) {
        this.logger = logger;
    }

    public Product(String Name, String Type, int numberOfUnits, String dateOfManufacture, String expirationDate,
                   double pricePerUnit, LoggingHandler logger) {
        this.logger = logger;
        this.name = Name;
        this.type = Type;
        this.numberOfUnits = numberOfUnits;
        if (dateOfManufacture == null) {
            this.dateOfManufacture = LocalDate.MIN;
        }
        else {
            this.dateOfManufacture = LocalDate.parse(dateOfManufacture);
        }
        if (expirationDate != null) {
            this.expirationDate = LocalDate.parse(expirationDate);
        } else {
            this.expirationDate = LocalDate.MAX;
        }
        this.pricePerUnit = pricePerUnit;
    }

    public Product(String Name, String Type, int numberOfUnits, String dateOfManufacture, String expirationDate,
                   double pricePerUnit) {
        if (logger == null) {
            this.logger = new LoggingHandler();
        }
        this.name = Name;
        this.type = Type;
        this.numberOfUnits = numberOfUnits;
        if (dateOfManufacture == null) {
            this.dateOfManufacture = LocalDate.MIN;
        }
        else {
            this.dateOfManufacture = LocalDate.parse(dateOfManufacture);
        }
        if (expirationDate != null) {
            this.expirationDate = LocalDate.parse(expirationDate);
        } else {
            this.expirationDate = LocalDate.MAX;
        }
        this.pricePerUnit = pricePerUnit;
    }

    public Product(Product other, LoggingHandler logger) {
        this.logger = logger;
        this.name = other.name;
        this.type = other.type;
        this.numberOfUnits = other.numberOfUnits;
        this.dateOfManufacture = other.dateOfManufacture;
        this.expirationDate = other.expirationDate;
        this.pricePerUnit = other.pricePerUnit;
    }

    public Product() {
        this.logger = new LoggingHandler();
        name = "Продукт";
        type = "Тип";
        numberOfUnits = 0;
        dateOfManufacture = LocalDate.MIN;
        expirationDate = LocalDate.MIN;
        pricePerUnit = 0;
    }

    public String GetName() {
        return name;
    }

    public String GetType() {
        return type;
    }

    public int GetNumberOfUnits() {
        return numberOfUnits;
    }

    public LocalDate GetDateOfManufacture() {
        return dateOfManufacture;
    }

    public LocalDate GetExpirationDate() {
        return expirationDate;
    }

    public Double GetPricePerUnit() {
        return pricePerUnit;
    }

    public void SetName(String Name) {
        this.name = Name;
    }

    public void SetType(String Type) {
        this.type = Type;
    }

    public void SetNumberOfUnits(int numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public void SetDateOfManufacture(LocalDate dateOfManufacture) {
        this.dateOfManufacture = LocalDate.parse(dateOfManufacture.toString());
    }

    public void SetExpirationDate(LocalDate shelfLife) {
        this.expirationDate = LocalDate.parse(shelfLife.toString());
    }

    public void SetPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public String toString() {
        if (expirationDate == LocalDate.MAX) {
            String expirationDateMax = "Немає";
            return "Продукт: " + name + " | Тип: " + type + " | Кількість: " + numberOfUnits + " | Дата виготовлення: " +
                    dateOfManufacture.toString() + " | Термін зберігання: " + expirationDateMax + " | Ціна: " + pricePerUnit;
        }
        return "Продукт: " + name + " | Тип: " + type + " | Кількість: " + numberOfUnits + " | Дата виготовлення: " +
                dateOfManufacture.toString() + " | Термін зберігання: " + expirationDate.toString() + " | Ціна: " + pricePerUnit;
    }

}

