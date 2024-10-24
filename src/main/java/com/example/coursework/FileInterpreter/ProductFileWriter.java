package com.example.coursework.FileInterpreter;

import com.example.coursework.ProductManagment.Product;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ProductFileWriter {
    private final LoggingHandler logger;

    public ProductFileWriter(LoggingHandler logger) {
        this.logger = logger;
    }

    public void writeProductsToDirectory(String directoryPath, List<Product> products) {
        // Очищуємо всі файли в папці перед початком запису
        clearAllFilesInDirectory(directoryPath);

        // Якщо список порожній, нічого не записуємо
        if (products.isEmpty()) {
            logger.logInfo("Product list is empty, all files cleared.");
            return;
        }

        // Записуємо продукти у відповідні файли за їх типами
        for (Product product : products) {
            try {
                String fileName = product.getType().isEmpty() ? "Продукти.txt" : product.getType() + ".txt";
                Path filePath = Paths.get(directoryPath, fileName);

                // Записуємо продукт у файл
                writeProductToFile(filePath.toString(), product);
            } catch (Exception e) {
                logger.logError("Error processing product: " + product.getName() + " - " + e.getMessage());
            }
        }
    }

    // Метод для очищення всіх файлів у директорії
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
            e.printStackTrace();
        }
    }

    // Метод для очищення одного файлу
    private void clearFile(String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) { // false = перезапис файлу (очищення)
            logger.logInfo("File cleared: " + filePath);
        } catch (IOException e) {
            logger.logError("Error clearing file: " + filePath + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Метод для запису продукту у файл
    private void writeProductToFile(String filePath, Product product) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) { // Append mode = true для додавання
            // Формуємо рядок для запису у форматі Name | Type | Number of units | Date of manufacture | Shelf life | Price per unit
            String line = formatProduct(product);
            bw.write(line);
            bw.newLine(); // Переходимо на новий рядок після кожного продукту
            logger.logInfo("Product written to file: " + product.getName() + " in " + filePath);
        } catch (IOException e) {
            logger.logError("Error writing to file: " + filePath + " for product: " + product.getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Метод для форматування інформації про продукт у рядок
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
                    // Визначення правильного відмінювання для місяців
                    String monthWord = monthsBetween % 10 == 1 && monthsBetween % 100 != 11 ? "місяць" :
                            monthsBetween % 10 >= 2 && monthsBetween % 10 <= 4 && monthsBetween % 100 >= 20 ? "місяці" : "місяців";
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