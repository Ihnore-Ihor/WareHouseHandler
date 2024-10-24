package com.example.coursework;

import com.example.coursework.ProductManagment.Product;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class ProductDialogController {

    @FXML
    private Label errorLabel;

    @FXML
    private DialogPane productDialog;

    @FXML
    private TextField productNameInput;

    @FXML
    private TextField productTypeInput;

    @FXML
    private TextField productUnitsInput;

    @FXML
    private DatePicker productDateInput;

    @FXML
    private DatePicker productExpirationInput;

    @FXML
    private TextField productPriceInput;

    private Product selectedProduct;

    private boolean isOkClicked = false;

    // Метод для ініціалізації продукту в діалозі
    public void setProduct(Product product) {
        this.selectedProduct = product;
        productNameInput.setText(product.getName());
        productTypeInput.setText(product.getType());
        productUnitsInput.setText(String.valueOf(product.getNumberOfUnits()));
        productDateInput.setValue(product.getDateOfManufacture());
        productExpirationInput.setValue(product.getExpirationDate());
        productPriceInput.setText(String.valueOf(product.getPricePerUnit()));
    }

    // Метод для повернення оновленого продукту після натискання ОК
    public Product getUpdatedProduct() {
        return new Product(
                productNameInput.getText(),
                productTypeInput.getText(),
                Integer.parseInt(productUnitsInput.getText()),
                productDateInput.getValue().toString(),
                productExpirationInput.getValue().toString(),
                Double.parseDouble(productPriceInput.getText())
        );
    }

    // Метод для обробки натискання ОК
    @FXML
    void handleOkButton() {
        if (validateInput()) {
            selectedProduct = getUpdatedProduct(); // Оновлюємо продукт
            isOkClicked = true;
            productDialog.setVisible(false); // Закриваємо діалог
        }
    }


    private boolean validateInput() {
        String errorMessage = "";

        if (productNameInput.getText() == null || productNameInput.getText().isEmpty()) {
            errorMessage += "Product name cannot be empty!\n";
        }
        if (productTypeInput.getText() == null || productTypeInput.getText().isEmpty()) {
            errorMessage += "Product type cannot be empty!\n";
        }
        if (productUnitsInput.getText() == null || !productUnitsInput.getText().matches("\\d+")) {
            errorMessage += "Number of units must be a valid number!\n";
        }
        if (productPriceInput.getText() == null || !productPriceInput.getText().matches("\\d+(\\.\\d{1,2})?")) {
            errorMessage += "Price must be a valid number (e.g., 12.34)!\n";
        }
        if (productDateInput.getValue() == null) {
            errorMessage += "Manufacture date must be selected!\n";
        }
        if (productExpirationInput.getValue() == null) {
            errorMessage += "Expiration date must be selected!\n";
        }

        // Check if there's any error
        if (errorMessage.isEmpty()) {
            errorLabel.setText(""); // No error, clear the label
            return true;
        } else {
            errorLabel.setText(errorMessage); // Set the error message in the label
            return false;
        }
    }

    public TextField getNameField() {
        return productNameInput; // assuming this is the TextField for the product's name
    }

    public TextField getTypeField() {
        return productTypeInput; // assuming this is the TextField for the product's type
    }

    public TextField getNumberOfUnitsField() {
        return productUnitsInput; // assuming this is the TextField for the number of units
    }

    public DatePicker getManufactureDateField() {
        return productDateInput; // assuming this is the DatePicker for the manufacture date
    }

    public DatePicker getExpirationDateField() {
        return productExpirationInput; // assuming this is the DatePicker for the expiration date
    }

    public TextField getPriceField() {
        return productPriceInput; // assuming this is the TextField for the price
    }

}
