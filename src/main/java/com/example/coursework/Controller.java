package com.example.coursework;
import com.example.coursework.ProductManagment.Product;
import com.example.coursework.ProductManagment.ProductManager;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

public class Controller {
    private List<Product> products;
    private ProductManager manager;

    public void initializeData(List<Product> products, ProductManager manager) {
        this.products = products;
        this.manager = manager;
    }

    @FXML
    public void initialize() {
        TableProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableProductType.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableProductNumberOfUnits.setCellValueFactory(new PropertyValueFactory<>("numberOfUnits"));

        TableProductDateOfManufacture.setCellValueFactory(new PropertyValueFactory<>("dateOfManufacture"));
        TableProductDateOfManufacture.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate dateOfManufacture, boolean empty) {
                super.updateItem(dateOfManufacture, empty);

                if (empty) {
                    setText(null); // Ensure empty rows show nothing
                } else if (dateOfManufacture == null || dateOfManufacture.equals(LocalDate.MIN)) {
                    setText("Немає");
                } else {
                    setText(dateOfManufacture.toString());
                }
            }
        });

        TableProductExpirationDate.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
        TableProductExpirationDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate expirationDate, boolean empty) {
                super.updateItem(expirationDate, empty);

                if (empty) {
                    setText(null); // Ensure empty rows show nothing
                } else if (expirationDate == null || expirationDate.equals(LocalDate.MAX)) {
                    setText("Немає");
                } else {
                    setText(expirationDate.toString());
                }
            }
        });


        TableProductPricePerUnit.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));

        // Прив'язуємо активацію/деактивацію кнопок
        setupButtonBindings();
    }


    @FXML
    private TableView<Product> ProductTable;

    @FXML
    private TableColumn<Product, LocalDate> TableProductDateOfManufacture;

    @FXML
    private TableColumn<Product, LocalDate> TableProductExpirationDate;

    @FXML
    private TableColumn<Product, String> TableProductName;

    @FXML
    private TableColumn<Product, Integer> TableProductNumberOfUnits;

    @FXML
    private TableColumn<Product, Double> TableProductPricePerUnit;

    @FXML
    private TableColumn<Product, String> TableProductType;

    @FXML
    private TextField maxPriceInput;

    @FXML
    private TextField minPriceInput;

    @FXML
    private TextField monthInput;

    @FXML
    private TextField searchProductInput;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button expireButton;

    @FXML
    private Button showPriceRangeButton;


    // Змінна для збереження вибраного продукту
    private Product selectedProduct;

    // Метод для обробки вибору рядка в таблиці
    @FXML
    void RowSelectedProducts(MouseEvent event) {
        // Отримуємо вибраний рядок з таблиці
        selectedProduct = ProductTable.getSelectionModel().getSelectedItem();

        // Перевіряємо, чи щось вибрано
        if (selectedProduct != null) {
            System.out.println("Selected Product: " + selectedProduct.getName());
        } else {
            System.out.println("No product selected.");
        }
    }

    // Метод для видалення вибраного продукту
    @FXML
    void DeleteProduct(MouseEvent event) {
        if (selectedProduct != null) {
            // Видаляємо вибраний продукт з таблиці
            ProductTable.getItems().remove(selectedProduct);
            manager.productDelete(products, selectedProduct);
            System.out.println("Product deleted: " + selectedProduct.getName());

            // Очищуємо вибір після видалення
            selectedProduct = null;
        } else {
            System.out.println("No product selected for deletion.");
        }
    }

    // Метод для редагування вибраного продукту
    @FXML
    void EditProduct(MouseEvent event) {
        if (selectedProduct != null) {
            try {
                // Load FXML for the product dialog
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/coursework/ProductDialog.fxml"));
                DialogPane dialogPane = loader.load();

                // Get the controller for the dialog
                ProductDialogController dialogController = loader.getController();

                // Pass the selected product to the dialog before showing it
                dialogController.setProduct(selectedProduct);

                // Create and show the dialog
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setDialogPane(dialogPane);
                dialog.setTitle("Редагувати продукт");

                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                dialog.setResultConverter(button -> {
                    if (button == ButtonType.OK) {
                        try {
                            Product updatedProduct = dialogController.getUpdatedProduct();

                            // Validate fields and display specific error messages in Ukrainian
                            if (updatedProduct.getName() == null || updatedProduct.getName().isEmpty()) {
                                showErrorDialog("Помилка поля", "Поле 'Назва продукту' не може бути порожнім.");
                            } else if (updatedProduct.getType() == null || updatedProduct.getType().isEmpty()) {
                                showErrorDialog("Помилка поля", "Поле 'Тип продукту' не може бути порожнім.");
                            } else if (updatedProduct.getNumberOfUnits() <= 0) {
                                showErrorDialog("Помилка поля", "Кількість одиниць має бути більше нуля.");
                            } else if (updatedProduct.getDateOfManufacture() == null) {
                                showErrorDialog("Помилка поля", "Дата виробництва не може бути порожньою.");
                            } else if (updatedProduct.getExpirationDate() == null) {
                                showErrorDialog("Помилка поля", "Дата закінчення терміну придатності не може бути порожньою.");
                            } else if (updatedProduct.getPricePerUnit() <= 0) {
                                showErrorDialog("Помилка поля", "Ціна за одиницю повинна бути більше нуля.");
                            } else {
                                // If all fields are valid, proceed with updating the product
                                manager.productEdit(products, selectedProduct, updatedProduct.getName(), updatedProduct.getType(),
                                        String.valueOf(updatedProduct.getNumberOfUnits()), updatedProduct.getDateOfManufacture().toString(),
                                        updatedProduct.getExpirationDate().toString(), String.valueOf(updatedProduct.getPricePerUnit()));

                                ProductTable.refresh(); // Refresh the table with updated data
                                return ButtonType.OK;
                            }
                        } catch (IllegalArgumentException e) {
                            showErrorDialog("Некоректний ввід", "Якесь поле введено неправильно.");
                        } catch (Exception e) {
                            showErrorDialog("Помилка", "Виникла неочікувана помилка: " + e.getMessage());
                        }
                    }
                    return ButtonType.CANCEL;
                });


                dialog.showAndWait(); // Show the dialog and wait for user input

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No product selected for editing.");
            showErrorDialog("Не обрано", "Будь ласка, оберіть який продукт редагувати.");
        }
    }

    @FXML
    void AddProduct(MouseEvent event) {
        try {
            // Load FXML for the product dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/coursework/ProductDialog.fxml"));
            DialogPane dialogPane = loader.load();

            // Get the controller for the dialog
            ProductDialogController dialogController = loader.getController();

            // Create and show the dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Додавання продукту");

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    try {
                        // Manually get the field values to validate them before getting the final Product object
                        String name = dialogController.getNameField().getText();
                        String type = dialogController.getTypeField().getText();
                        int numberOfUnits;
                        LocalDate manufactureDate = dialogController.getManufactureDateField().getValue();
                        LocalDate expirationDate = dialogController.getExpirationDateField().getValue();
                        double pricePerUnit;

                        // Field-specific validations
                        if (name == null || name.isEmpty()) {
                            showErrorDialog("Помилка поля", "Поле 'Назва продукту' не може бути порожнім.");
                            return ButtonType.CANCEL;
                        }
                        if (type == null || type.isEmpty()) {
                            showErrorDialog("Помилка поля", "Поле 'Тип продукту' не може бути порожнім.");
                            return ButtonType.CANCEL;
                        }
                        try {
                            numberOfUnits = Integer.parseInt(dialogController.getNumberOfUnitsField().getText());
                            if (numberOfUnits <= 0) {
                                showErrorDialog("Помилка поля", "Кількість одиниць має бути більше нуля.");
                                return ButtonType.CANCEL;
                            }
                        } catch (NumberFormatException e) {
                            showErrorDialog("Помилка поля", "Кількість одиниць повинна бути числом.");
                            return ButtonType.CANCEL;
                        }

                        if (manufactureDate == null) {
                            showErrorDialog("Помилка поля", "Дата виробництва не може бути порожньою.");
                            return ButtonType.CANCEL;
                        }
                        if (expirationDate == null) {
                            showErrorDialog("Помилка поля", "Дата закінчення терміну придатності не може бути порожньою.");
                            return ButtonType.CANCEL;
                        }

                        try {
                            pricePerUnit = Double.parseDouble(dialogController.getPriceField().getText());
                            if (pricePerUnit <= 0) {
                                showErrorDialog("Помилка поля", "Ціна за одиницю повинна бути більше нуля.");
                                return ButtonType.CANCEL;
                            }
                        } catch (NumberFormatException e) {
                            showErrorDialog("Помилка поля", "Ціна повинна бути числом.");
                            return ButtonType.CANCEL;
                        }

                        // If validation passes, get the final Product object and add it to the list
                        Product newProduct = dialogController.getUpdatedProduct();
                        manager.productAdd(products, newProduct);
                        ProductTable.setItems(FXCollections.observableArrayList(products)); // Refresh table
                        return ButtonType.OK;

                    } catch (IllegalArgumentException e) {
                        showErrorDialog("Некоректний ввід", "Якесь поле введено неправильно.");
                    } catch (Exception e) {
                        showErrorDialog("Помилка", "Виникла неочікувана помилка: " + e.getMessage());
                    }
                }
                return ButtonType.CANCEL;
            });



            dialog.showAndWait();  // Show the dialog and wait for user input

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    @FXML
    void ExpireInputtedMonth(MouseEvent event) {
        try {
            // Get the input from the text field
            String monthString = monthInput.getText().trim();
            Month month = null;

            // Check if input is a number
            try {
                int monthNumber = Integer.parseInt(monthString);
                if (monthNumber >= 1 && monthNumber <= 12) {
                    month = Month.of(monthNumber);
                }
            } catch (NumberFormatException e) {
                // Continue to check for Ukrainian month names
            }

            // Check for Ukrainian month names
            if (month == null) {
                switch (monthString.toLowerCase()) {
                    case "січень":
                        month = Month.JANUARY;
                        break;
                    case "лютий":
                        month = Month.FEBRUARY;
                        break;
                    case "березень":
                        month = Month.MARCH;
                        break;
                    case "квітень":
                        month = Month.APRIL;
                        break;
                    case "травень":
                        month = Month.MAY;
                        break;
                    case "червень":
                        month = Month.JUNE;
                        break;
                    case "липень":
                        month = Month.JULY;
                        break;
                    case "серпень":
                        month = Month.AUGUST;
                        break;
                    case "вересень":
                        month = Month.SEPTEMBER;
                        break;
                    case "жовтень":
                        month = Month.OCTOBER;
                        break;
                    case "листопад":
                        month = Month.NOVEMBER;
                        break;
                    case "грудень":
                        month = Month.DECEMBER;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid month entered.");
                }
            }

            // Clear the table before showing results
            ProductTable.getItems().clear();

            // Get the products expiring in the given month
            List<Product> expiringProducts = manager.productsExpiringInMonth(month, products);

            // Display the results in the table
            ProductTable.getItems().addAll(expiringProducts);

            // Log or print the number of results
            System.out.println("Products expiring in " + monthString + ": " + expiringProducts.size());
        } catch (IllegalArgumentException e) {
            // Handle invalid month input
            System.out.println("Invalid month entered. Please enter a valid month.");
            // Display error message to the user in the UI
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid month (e.g., 1 for January, or 'січень' for January).");
            alert.showAndWait();
        }
    }



    @FXML
    void GroupWithSamePrice(MouseEvent event) {
        // Step 1: Group the products by price
        List<List<Product>> groupedProducts = manager.groupProductsByPrice(products);

        // Step 2: Clear the table before displaying new data
        ProductTable.getItems().clear();

        // Step 3: Iterate over each group and display them in the table
        for (List<Product> group : groupedProducts) {
            if (!group.isEmpty()) {
                // Add a label for the group
                Product groupLabelProduct = new Product("Згруповані продукти:", "Розмір групи ->",
                        group.size(), null, null, group.getFirst().getPricePerUnit());
                ProductTable.getItems().add(groupLabelProduct);

                // Add each product in the group
                for (Product product : group) {
                    ProductTable.getItems().add(product);
                }
            }
        }

        // Step 4: Refresh the table to show the updated data
        ProductTable.refresh();
    }





    @FXML
    void ReadFromFile(MouseEvent event) {
        // Зчитуємо продукти з файлу
        products = manager.readProductsFromDirectory("/Users/ihnore_ihor/IntelliJIDEAProjects/CourseWork/src/main/java/com/example/coursework/db");

        // Очищаємо попередні дані в таблиці
        ProductTable.getItems().clear();

        // Додаємо нові продукти до таблиці
        ProductTable.getItems().addAll(products);
    }

    @FXML
    void SameManufactureDateForTypes(MouseEvent event) {
        // Step 1: Group the products by manufacture date
        List<List<Product>> groupedProducts = manager.productsByManufactureDate(products);

        // Step 2: Clear the table before displaying new data
        ProductTable.getItems().clear();

        // Step 3: Iterate over each group and add a header for each type
        for (List<Product> group : groupedProducts) {
            if (!group.isEmpty()) {
                Product headerProduct = getHeaderProduct(group);
                ProductTable.getItems().add(headerProduct); // Add the header to the table

                // Add the actual products in the group
                for (Product product : group) {
                    ProductTable.getItems().add(product); // Add each product in the group to the table
                }
            }
        }

        // Step 4: Refresh the table to show the updated data
        ProductTable.refresh();
    }
    private @NotNull Product getHeaderProduct(List<Product> group) {
        String type = group.getFirst().getType(); // Assuming all products in the group have the same type
        LocalDate manufactureDate = group.getFirst().getDateOfManufacture(); // Assuming all products have the same manufacture date

        // Add a header to indicate the type and manufacture date
        return new Product(
                "Згруповані продукти:",
                "Розмір групи ->",
                group.size(), // Use the size of the group
                manufactureDate.toString(), // No specific date of manufacture for the header
                null, // No specific expiration date for the header
                0 // No specific price for the header
        );
    }


    @FXML
    void SearchProduct(MouseEvent event) {
        String searchName = searchProductInput.getText().trim();  // Get the search input

        // Search for the product in the list
        Product foundProduct = manager.productSearch(products, searchName);

        if (foundProduct != null) {
            // Highlight the product row in the table
            ProductTable.getSelectionModel().select(foundProduct);
            ProductTable.scrollTo(foundProduct);  // Optional: Scroll to the found product
        } else {
            // Show an error message if the product is not found
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Продукт не знайдено");
            alert.setHeaderText(null);
            alert.setContentText("Не знадено продукту з назвою: " + searchName);
            alert.showAndWait();
        }
    }


    @FXML
    void ShowInPriceRange(MouseEvent event) {
        try {
            // Get the input values
            String minPriceText = minPriceInput.getText();
            String maxPriceText = maxPriceInput.getText();

            // Parse the prices
            double minPrice = Double.parseDouble(minPriceText);
            double maxPrice = Double.parseDouble(maxPriceText);

            // Check if minPrice is less than maxPrice
            if (minPrice < maxPrice) {
                // Get the list of products in the price range
                List<Product> productsInRange = manager.productsInPriceRange(minPrice, maxPrice, products);

                // Clear existing items in the table
                ProductTable.getItems().clear();

                // Add new items to the table
                ProductTable.getItems().addAll(productsInRange);
            } else {
                // Show an error message if minPrice is not less than maxPrice
                showAlert("Помилка", "Мінімальна ціна мусить бути меншою ніж максимальна ціна.");
            }
        } catch (NumberFormatException e) {
            // Handle invalid number input
            showAlert("Некоректний ввід", "Будь ласка, введіть числове значення для ціни.");
        }
    }

    // Method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    void TypeSmallestShelfLife(MouseEvent event) {
        // Get the list of products with the smallest average shelf life
        List<Product> productsWithSmallestShelfLife = manager.typeWithSmallestAverageShelfLife(products);

        // Clear existing items in the table
        ProductTable.getItems().clear();

        // Add new items to the table
        ProductTable.getItems().addAll(productsWithSmallestShelfLife);
    }


    @FXML
    void ValueByType(MouseEvent event) {
        List<Map.Entry<String, Double>> totalValueByType = manager.calculateTotalValueByType(products);

        try {
            // Load FXML for the value by type dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/coursework/ValueByTypeDialog.fxml"));
            DialogPane dialogPane = loader.load();

            // Get the controller for the dialog
            ValueByTypeController dialogController = loader.getController();
            dialogController.setData(totalValueByType); // Pass the data to the controller

            // Create and show the dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Total Value by Product Type");

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait(); // Show the dialog and wait for user input

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    void WriteToFile(MouseEvent event) {
        manager.writeProductsToDirectory("/Users/ihnore_ihor/IntelliJIDEAProjects/CourseWork/src/main/java/com/example/coursework/db", products);
    }

    // Метод для активації/деактивації кнопок на основі введених даних
    private void setupButtonBindings() {
        // Прив'язка для кнопок DeleteProduct та EditProduct (активні, якщо вибраний продукт)
        BooleanBinding isProductSelected = ProductTable.getSelectionModel().selectedItemProperty().isNull();

        if (deleteButton != null) {
            deleteButton.disableProperty().bind(isProductSelected);
        }

        if (editButton != null) {
            editButton.disableProperty().bind(isProductSelected);
        }

        // Прив'язка для кнопки ExpireInputtedMonth (активна, якщо введено правильне число в поле monthInput)
        BooleanBinding isMonthInputValid = monthInput.textProperty().isEmpty()
                .or(monthInput.textProperty().isNotEmpty().and(monthInput.textProperty().isEqualTo("0")));

        if (expireButton != null) {
            expireButton.disableProperty().bind(isMonthInputValid);
        }

        // Прив'язка для кнопки ShowInPriceRange (активна, якщо обидва поля minPriceInput і maxPriceInput заповнені правильно)
        BooleanBinding isPriceInputValid = minPriceInput.textProperty().isEmpty().or(maxPriceInput.textProperty().isEmpty());

        if (showPriceRangeButton != null) {
            showPriceRangeButton.disableProperty().bind(isPriceInputValid);
        }
    }

    @FXML
    void PrintAllProducts(MouseEvent event) {
        // Clear the current table items
        ProductTable.getItems().clear();

        // Add all products from the list to the table
        ProductTable.getItems().addAll(products);

        // Optional: Log or print confirmation
        System.out.println("All products displayed in the table.");
    }

}