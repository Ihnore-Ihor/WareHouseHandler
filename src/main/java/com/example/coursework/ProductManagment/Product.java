package com.example.coursework.ProductManagment;

import com.example.coursework.FileInterpreter.LoggingHandler;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.time.temporal.ChronoUnit;

//Name | Type | Number of units | Date of manufacture | Shelf life | Price per unit
public class Product {
    protected String Name;
    protected String Type;
    protected int numberOfUnits;
    protected LocalDate dateOfManufacture;
    protected LocalDate expirationDate;
    protected double pricePerUnit;

    private LoggingHandler logger = null;

    public Product(LoggingHandler logger) {
        this.logger = logger;
    }

    public Product(String Name, String Type, int numberOfUnits, String dateOfManufacture, String expirationDate, double pricePerUnit, LoggingHandler logger) {
        this.logger = logger;
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

    public Product(String Name, String Type, int numberOfUnits, String dateOfManufacture, String expirationDate, double pricePerUnit) {
        if (logger == null) {
            this.logger = new LoggingHandler();
        }
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

    public Product(Product other, LoggingHandler logger) {
        this.logger = logger;
        this.Name = other.Name;
        this.Type = other.Type;
        this.numberOfUnits = other.numberOfUnits;
        this.dateOfManufacture = other.dateOfManufacture;
        this.expirationDate = other.expirationDate;
        this.pricePerUnit = other.pricePerUnit;
    }

    public Product() {
        this.logger = new LoggingHandler();
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




    public List<Product> readProductsFromDirectory(String directoryPath) {
        List<Product> products = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath), "*.txt")) {
            for (Path filePath : stream) {
                String fileName = filePath.getFileName().toString();
                String productType = extractProductTypeFromFileName(fileName);

                List<Product> fileProducts = readProductsFromFile(filePath.toString(), productType);
                products.addAll(fileProducts);
                logger.logInfo("Successfully read file: " + filePath);
            }
        } catch (IOException e) {
            logger.logError("Error reading directory: " + e.getMessage());
        }
        return products;
    }

    // Helper method to extract product type from file name
    private String extractProductTypeFromFileName(String fileName) {
        try {
            return fileName.substring(0, fileName.indexOf(".txt")).trim();
        } catch (StringIndexOutOfBoundsException e) {
            logger.logError("Error extracting product type from file name: " + fileName);
            return "UnknownType";
        }
    }

    // Method to read products from a file
    public List<Product> readProductsFromFile(String filePath, String productType) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    line = line.replace("одиниць", "").trim();
                    String[] fields = line.split("\\|");
                    if (fields.length == 5) {
                        Product product = parseProductFields(fields, productType);
                        products.add(product);
                        logger.logInfo("Processed product: " + product.getName());
                    } else {
                        logger.logError("Invalid number of fields in line: " + filePath);
                    }
                }
            }
        } catch (IOException e) {
            logger.logError("Error reading file: " + filePath + " - " + e.getMessage());
        }
        return products;
    }

    // Helper method to parse fields into a Product
    private Product parseProductFields(String[] fields, String type) {
        String name = fields[0].trim();
        int numberOfUnits = parseInteger(fields[1], "number of units");
        double pricePerUnit = parseDouble(fields[4], "price per unit");

        String dateOfManufacture = fields[2].trim();
        String shelfLife = parseShelfLife(fields[3], dateOfManufacture);

        return new Product(name, type, numberOfUnits, dateOfManufacture, shelfLife, pricePerUnit, logger);
    }

    // Helper to parse integer fields safely
    private int parseInteger(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.logError("Invalid " + fieldName + ": " + value);
            return 0;
        }
    }

    // Helper to parse double fields safely
    private double parseDouble(String value, String fieldName) {
        try {
            if (value.contains(",")) {
                value = value.replace(",", ".");
            }
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            logger.logError("Invalid " + fieldName + ": " + value);
            return 0.0;
        }
    }

    // Helper method to parse and calculate shelf life
    private String parseShelfLife(String shelfLife, String dateOfManufacture) {
        try {
            if (shelfLife.matches("\\d+ місяців") || shelfLife.matches("\\d+ місяці")) {
                int months = Integer.parseInt(shelfLife.split(" ")[0]);
                return calculateShelfLife(dateOfManufacture, months);
            } else if (shelfLife.matches("\\d+ роки?")) {
                int years = Integer.parseInt(shelfLife.split(" ")[0]);
                return calculateShelfLife(dateOfManufacture, years * 12);
            } else if (shelfLife.equals("Немає")) {
                return null;
            }
        } catch (NumberFormatException e) {
            logger.logError("Invalid shelf life: " + shelfLife);
        }
        return "Unknown";
    }

    // Helper method to calculate shelf life from the date of manufacture and months
    private String calculateShelfLife(String dateOfManufacture, int months) {
        try {
            LocalDate manufactureDate = LocalDate.parse(dateOfManufacture);
            LocalDate expiryDate = manufactureDate.plusMonths(months);
            logger.logInfo("Calculated shelf life: " + expiryDate + " for manufacture date: " + manufactureDate);
            return expiryDate.toString();
        } catch (DateTimeParseException e) {
            logger.logError("Invalid date format for manufacture date: " + dateOfManufacture);
            return "Unknown";
        }
    }


    // Method to write products to a directory, organized by product type
    public void writeProductsToDirectory(String directoryPath, List<Product> products) {
        clearAllFilesInDirectory(directoryPath);

        if (products.isEmpty()) {
            logger.logInfo("Product list is empty, all files cleared.");
            return;
        }

        for (Product product : products) {
            try {
                String fileName = product.getType().isEmpty() ? "Продукти.txt" : product.getType() + ".txt";
                Path filePath = Paths.get(directoryPath, fileName);
                writeProductToFile(filePath.toString(), product);
            } catch (Exception e) {
                logger.logError("Error processing product: " + product.getName() + " - " + e.getMessage());
            }
        }
    }

    // Method to clear all files in a directory
    private void clearAllFilesInDirectory(String directoryPath) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath))) {
            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {
                    clearFile(path.toString());
                }
            }
            logger.logInfo("All files in directory cleared: " + directoryPath);
        } catch (IOException e) {
            logger.logError("Error clearing files in directory: " + directoryPath + " - " + e.getMessage());
        }
    }

    // Method to clear a single file
    private void clearFile(String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            logger.logInfo("File cleared: " + filePath);
        } catch (IOException e) {
            logger.logError("Error clearing file: " + filePath + " - " + e.getMessage());
        }
    }

    // Method to write a single product to a file
    private void writeProductToFile(String filePath, Product product) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            String line = formatProduct(product);
            bw.write(line);
            bw.newLine();
            logger.logInfo("Product written to file: " + product.getName() + " in " + filePath);
        } catch (IOException e) {
            logger.logError("Error writing to file: " + filePath + " for product: " + product.getName() + " - " + e.getMessage());
        }
    }

    // Method to format the product details into a string
    private String formatProduct(Product product) {
        String shelfLife;

        try {
            if (product.getExpirationDate().equals(LocalDate.MAX)) {
                shelfLife = "Немає";
            } else {
                long monthsBetween = ChronoUnit.MONTHS.between(LocalDate.now(), product.getExpirationDate());
                if (monthsBetween >= 24) {
                    shelfLife = (monthsBetween / 12) + " років";
                } else if (monthsBetween >= 6) {
                    String monthWord = (monthsBetween % 10 == 1 && monthsBetween % 100 != 11) ? "місяць" :
                            (monthsBetween % 10 >= 2 && monthsBetween % 10 <= 4 && monthsBetween % 100 >= 20) ? "місяці" : "місяців";
                    shelfLife = monthsBetween + " " + monthWord;
                } else {
                    shelfLife = product.getExpirationDate().toString();
                }
            }
        } catch (Exception e) {
            logger.logError("Error formatting shelf life for product: " + product.getName() + " - " + e.getMessage());
            shelfLife = "Unknown";
        }

        return String.join(" | ",
                product.getName(),
                product.getNumberOfUnits() + " одиниць",
                product.getDateOfManufacture().toString(),
                shelfLife,
                String.format("%.2f", product.getPricePerUnit())
        );
    }

}

