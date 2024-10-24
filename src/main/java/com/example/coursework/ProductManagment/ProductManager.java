package com.example.coursework.ProductManagment;

import com.example.coursework.FileInterpreter.LoggingHandler;

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
}

