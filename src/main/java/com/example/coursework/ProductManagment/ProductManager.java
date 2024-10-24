package com.example.coursework.ProductManagment;

import com.example.coursework.FileInterpreter.LoggingHandler;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProductManager {
    protected final LoggingHandler logger;

    public ProductManager(LoggingHandler logger) {
        this.logger = logger;
    }

    public List<Product> typeWithSmallestAverageShelfLife(List<Product> products) {
        HashMap<String, Integer> totalDaysInTypes = new HashMap<>();
        HashMap<String, Integer> counts = new HashMap<>();

        try {
            for (Product product : products) {
                // Calculate the number of days between dates
                long days = ChronoUnit.DAYS.between(product.getDateOfManufacture(), product.getExpirationDate());

                totalDaysInTypes.merge(product.getType(), (int) days, Integer::sum);
                counts.merge(product.getType(), 1, Integer::sum);
            }

            HashMap<String, Double> averageDaysInTypes = new HashMap<>();

            for (String type : totalDaysInTypes.keySet()) {
                int totalDays = totalDaysInTypes.get(type);
                int count = counts.get(type);
                averageDaysInTypes.put(type, (double) totalDays / count);
            }

            String smallestDurationType = null;
            Double smallestDuration = null;

            for (Map.Entry<String, Double> entry : averageDaysInTypes.entrySet()) {
                if (smallestDuration == null || entry.getValue() < smallestDuration) {
                    smallestDuration = entry.getValue();
                    smallestDurationType = entry.getKey();
                }
            }

            List<Product> smallestAverageShelfLifeType = new ArrayList<>();
            for (Product product : products) {
                if (product.getType().equals(smallestDurationType)) {
                    smallestAverageShelfLifeType.add(product);
                }
            }

            logger.logInfo("Products with smallest average shelf life found.");
            return smallestAverageShelfLifeType;

        } catch (Exception e) {
            logger.logError("Error finding smallest average shelf life: " + e.getMessage());
        }

        return Collections.emptyList();
    }


    public List<Product> productsExpiringInMonth(Month month, List<Product> products) {
        int year = LocalDate.now().getYear();
        List<Product> productsExpiringInMonth = new ArrayList<>();

        try {
            YearMonth targetYearMonth = YearMonth.of(year, month.getValue());

            for (Product product : products) {
                try {
                    YearMonth productYearMonth = YearMonth.from(product.getExpirationDate());

                    // Check if the product's expiration date falls within the target month
                    if (productYearMonth.equals(targetYearMonth)) {
                        productsExpiringInMonth.add(product);
                    }
                } catch (DateTimeParseException e) {
                    logger.logError("Error parsing shelf life date for product: " + product.getName() + " - " + e.getMessage());
                }
            }

            logger.logInfo("Products expiring in the month " + month + " of " + year + " found.");
        } catch (DateTimeException e) {
            logger.logError("Invalid month or year: " + e.getMessage());
        } catch (Exception e) {
            logger.logError("Unexpected error while filtering products: " + e.getMessage());
        }

        return productsExpiringInMonth;
    }


    public List<Map.Entry<String, Double>> calculateTotalValueByType(List<Product> products) {
        Map<String, Double> totalValueByType = new HashMap<>();

        try {
            for (Product product : products) {
                String type = product.getType();
                double totalValue = product.getPricePerUnit() * product.getNumberOfUnits();
                totalValueByType.put(type, totalValueByType.getOrDefault(type, 0.0) + totalValue);
            }

            List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(totalValueByType.entrySet());
            quickSort(sortedEntries, 0, sortedEntries.size() - 1);

            logger.logInfo("Total value by type calculated successfully.");
            return sortedEntries;
        } catch (Exception e) {
            logger.logError("Error calculating total value by type: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public void quickSort(List<Map.Entry<String, Double>> list, int start, int end) {
        try {
            if (start >= end) {
                return;
            }

            int pivotIndex = partition(list, start, end);
            quickSort(list, start, pivotIndex - 1);
            quickSort(list, pivotIndex + 1, end);
        } catch (Exception e) {
            logger.logError("Error during quickSort: " + e.getMessage());
        }
    }

    private int partition(List<Map.Entry<String, Double>> list, int start, int end) {
        try {
            Map.Entry<String, Double> pivot = list.get(end);
            int i = start - 1;

            for (int j = start; j < end; j++) {
                if (list.get(j).getValue() >= pivot.getValue()) {
                    i++;
                    Collections.swap(list, i, j);
                }
            }

            Collections.swap(list, i + 1, end);
            return i + 1;
        } catch (Exception e) {
            logger.logError("Error during partitioning: " + e.getMessage());
            return -1;
        }
    }

    public List<Product> productsInPriceRange(double minPrice, double maxPrice, List<Product> products) {
        List<Product> filteredProducts = new ArrayList<>();

        try {
            for (Product product : products) {
                double pricePerUnit = product.getPricePerUnit();
                if (pricePerUnit >= minPrice && pricePerUnit <= maxPrice) {
                    filteredProducts.add(product);
                }
            }
            logger.logInfo("Products in price range found.");
        } catch (Exception e) {
            logger.logError("Error filtering products by price range: " + e.getMessage());
        }

        return filteredProducts;
    }

    public List<List<Product>> productsByManufactureDate(List<Product> products) {
        List<List<Product>> result = new ArrayList<>();

        try {
            Map<String, Map<LocalDate, List<Product>>> groupedProducts = new HashMap<>();

            for (Product product : products) {
                String type = product.getType();
                LocalDate manufactureDate = product.getDateOfManufacture();

                groupedProducts.putIfAbsent(type, new HashMap<>());
                groupedProducts.get(type).putIfAbsent(manufactureDate, new ArrayList<>());
                groupedProducts.get(type).get(manufactureDate).add(product);
            }

            for (Map<LocalDate, List<Product>> dateGroup : groupedProducts.values()) {
                result.addAll(dateGroup.values());
            }

            logger.logInfo("Products grouped by manufacture date successfully.");
        } catch (Exception e) {
            logger.logError("Error grouping products by manufacture date: " + e.getMessage());
        }

        return result;
    }

    public List<List<Product>> groupProductsByPrice(List<Product> products) {
        List<List<Product>> result = new ArrayList<>();

        try {
            Map<Double, List<Product>> priceGroups = new HashMap<>();

            for (Product product : products) {
                double price = product.getPricePerUnit();
                priceGroups.putIfAbsent(price, new ArrayList<>());
                priceGroups.get(price).add(product);
            }

            result = new ArrayList<>(priceGroups.values());

            logger.logInfo("Products grouped by price successfully.");
        } catch (Exception e) {
            logger.logError("Error grouping products by price: " + e.getMessage());
        }

        return result;
    }


    public void productAdd(List<Product> products, Product newProduct) {
        try {
            if (newProduct != null) {
                products.add(newProduct);
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

        return new Product(name, type, numberOfUnits, dateOfManufacture, shelfLife, pricePerUnit, logger);
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

