package com.example.coursework.ProductManagment;

import java.time.LocalDate;

//Name | Type | Number of units | Date of manufacture | Shelf life | Price per unit
public class Product {
    protected String Name;
    protected String Type;
    protected int numberOfUnits;
    protected LocalDate dateOfManufacture;
    protected LocalDate expirationDate;
    protected double pricePerUnit;

    public Product(String Name, String Type, int numberOfUnits, String dateOfManufacture, String expirationDate, double pricePerUnit) {
        this.Name = Name;
        this.Type = Type;
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

    public Product(Product other) {
        this.Name = other.Name;
        this.Type = other.Type;
        this.numberOfUnits = other.numberOfUnits;
        this.dateOfManufacture = other.dateOfManufacture;
        this.expirationDate = other.expirationDate;
        this.pricePerUnit = other.pricePerUnit;
    }

    public Product() {
        Name = "Продукт";
        Type = "Тип";
        numberOfUnits = 0;
        dateOfManufacture = LocalDate.MIN;
        expirationDate = LocalDate.MIN;
        pricePerUnit = 0;
    }

    public String getName() {
        return Name;
    }

    public String getType() {
        return Type;
    }

    public int getNumberOfUnits() {
        return numberOfUnits;
    }

    public LocalDate getDateOfManufacture() {
        return dateOfManufacture;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public void setNumberOfUnits(int numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public void setDateOfManufacture(LocalDate dateOfManufacture) {
        this.dateOfManufacture = LocalDate.parse(dateOfManufacture.toString());
    }

    public void setExpirationDate(LocalDate shelfLife) {
        this.expirationDate = LocalDate.parse(shelfLife.toString());
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public String toString() {
        if (expirationDate == LocalDate.MAX) {
            String expirationDateMax = "Немає";
            return "Продукт: " + Name + " | Тип: " + Type + " | Кількість: " + numberOfUnits + " | Дата виготовлення: " +
                    dateOfManufacture.toString() + " | Термін зберігання: " + expirationDateMax + " | Ціна: " + pricePerUnit;
        }
        return "Продукт: " + Name + " | Тип: " + Type + " | Кількість: " + numberOfUnits + " | Дата виготовлення: " +
                dateOfManufacture.toString() + " | Термін зберігання: " + expirationDate.toString() + " | Ціна: " + pricePerUnit;
    }
}

