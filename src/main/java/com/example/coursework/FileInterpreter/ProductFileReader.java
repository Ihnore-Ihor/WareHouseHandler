package com.example.coursework.FileInterpreter;

import com.example.coursework.ProductManagment.Product;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ProductFileReader {
    private final LoggingHandler logger;

    public ProductFileReader(LoggingHandler logger) {
        this.logger = logger;
    }

    public List<Product> readProductsFromDirectory(String directoryPath) {
        List<Product> products = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath), "*.txt")) {
            for (Path filePath : stream) {
                String fileName = filePath.getFileName().toString();
                String productType = extractProductTypeFromFileName(fileName); // Витягуємо тип із назви файлу

                // Зчитуємо кожен файл і використовуємо тип із назви файлу
                List<Product> fileProducts = readProductsFromFile(filePath.toString(), productType);
                products.addAll(fileProducts);
                logger.logInfo("Successfully read file: " + filePath);
            }

        } catch (IOException e) {
            logger.logError("Error reading directory: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    private String extractProductTypeFromFileName(String fileName) {
        try {
            return fileName.substring(0, fileName.indexOf(".txt")).trim();
        } catch (StringIndexOutOfBoundsException e) {
            logger.logError("Error extracting product type from file name: " + fileName + " - " + e.getMessage());
            return "UnknownType"; // Додаємо резервне значення у разі помилки
        }
    }

    public List<Product> readProductsFromFile(String filePath, String productType) {
        List<Product> products = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Пропускаємо рядки з #
                if (!line.startsWith("#")) {
                    try {
                        // Видаляємо слово "одиниць", якщо воно є в рядку
                        line = line.replace("одиниць", "").trim();
                        String[] fields = line.split("\\|"); // Розділяємо рядок по знаку |

                        // Перевіряємо, чи є правильна кількість полів у рядку
                        if (fields.length == 5) { // Чекаємо 5 полів, бо тип зчитуємо з назви файлу
                            Product product = getProduct(fields, productType); // Передаємо тип з назви файлу
                            products.add(product);
                            logger.logInfo("Successfully processed product: " + product.getName());
                        } else {
                            logger.logError("Invalid number of fields in line: " + filePath);
                        }
                    } catch (Exception e) {
                        logger.logError("Error processing line: " + line + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            logger.logError("Error reading file: " + filePath + " - " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    private Product getProduct(String[] fields, String type) {
        String name = fields[0].trim();
        int numberOfUnits = 0;
        double pricePerUnit = 0.0;
        String dateOfManufacture = fields[2].trim();
        String shelfLife = fields[3].trim();

        try {
            numberOfUnits = Integer.parseInt(fields[1].trim());
        } catch (NumberFormatException e) {
            logger.logError("Invalid number of units: " + fields[1].trim() + " - " + e.getMessage());
        }

        try {
            if (fields[4].trim().contains(","))
            {
                fields[4] = fields[4].trim().replace(",", ".");
            }
            pricePerUnit = Double.parseDouble(fields[4].trim());
        } catch (NumberFormatException e) {
            logger.logError("Invalid price per unit: " + fields[4].trim() + " - " + e.getMessage());
        }

        try {
            if (shelfLife.matches("\\d+ місяців") || shelfLife.matches("\\d+ місяці")) {
                int months = Integer.parseInt(shelfLife.split(" ")[0]);
                shelfLife = calculateShelfLife(dateOfManufacture, months);
            } else if (shelfLife.matches("\\d+ роки?")) {
                int years = Integer.parseInt(shelfLife.split(" ")[0]);
                shelfLife = calculateShelfLife(dateOfManufacture, years * 12);
            } else if (shelfLife.equals("Немає")) {
                shelfLife = null;
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            logger.logError("Error processing shelf life: " + shelfLife + " - " + e.getMessage());
        }

        return new Product(name, type, numberOfUnits, dateOfManufacture, shelfLife, pricePerUnit);
    }

    private String calculateShelfLife(String dateOfManufacture, int months) {
        try {
            LocalDate manufactureDate = LocalDate.parse(dateOfManufacture);
            LocalDate expiryDate = manufactureDate.plusMonths(months);
            logger.logInfo("Calculated shelf life: " + expiryDate + " for manufacture date: " + manufactureDate);
            return expiryDate.toString();
        } catch (DateTimeParseException e) {
            logger.logError("Invalid date format for date of manufacture: " + dateOfManufacture + " - " + e.getMessage());
            return "Unknown";
        }
    }
}

