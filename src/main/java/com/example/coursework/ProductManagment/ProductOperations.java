package com.example.coursework.ProductManagment;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import com.example.coursework.FileInterpreter.LoggingHandler;

public class ProductOperations {
    private final LoggingHandler logger;

    public ProductOperations(LoggingHandler logger) {
        this.logger = logger;
    }

    public void productAdd(List<Product> products, Product newProduct) {
        try {
            if (newProduct != null) {
                products.add(newProduct);  // Add the new product to the list
                logger.logInfo("Product added: " + newProduct.toString());
            } else {
                logger.logError("New product is null, cannot add.");
            }
        } catch (Exception e) {
            logger.logError("Error adding product: " + e.getMessage());
        }
    }

    public Product productSearch(List<Product> products, String name) {
        try {
            if (name != null) {
                for (Product product : products) {
                    if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                        logger.logInfo("Product found: " + name);
                        return product;
                    }
                }
            }
        } catch (Exception e) {
            logger.logError("Error searching for product: " + name + " - " + e.getMessage());
        }
        return null;
    }

    public void productDelete(List<Product> products, Product product) {
        try {
            if (product != null) {
                products.remove(product);
                logger.logInfo("Product deleted: " + product.getName());
            }
        } catch (Exception e) {
            logger.logError("Error deleting product: " + product.getName() + " - " + e.getMessage());
        }
    }

    public void productEdit(List<Product> products, Product product, String newName, String newType, String newNumberOfUnits,
                            String newDateOfManufacture, String newShelfLife, String newPricePerUnite) {
        try {
            if (product != null) {
                int index = products.indexOf(product);

                if (index != -1) {

                    products.get(index).setName(newName);
                    products.get(index).setType(newType);
                    products.get(index).setNumberOfUnits(Integer.parseInt(newNumberOfUnits));
                    products.get(index).setDateOfManufacture(LocalDate.parse(newDateOfManufacture));
                    products.get(index).setExpirationDate(LocalDate.parse(newShelfLife));
                    products.get(index).setPricePerUnit(Double.parseDouble(newPricePerUnite));

                    logger.logInfo("Product updated: " + products.get(index).toString());
                }
            }
        } catch (Exception e) {
            logger.logError("Error editing product: " + product + " - " + e.getMessage());
        }
    }
}

