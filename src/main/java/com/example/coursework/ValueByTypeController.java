package com.example.coursework;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Map;
import com.example.coursework.ProductManagment.ProductTypeValue;

public class ValueByTypeController {

    @FXML
    private TableView<ProductTypeValue> valueByTypeTable;

    @FXML
    private TableColumn<ProductTypeValue, String> typeColumn;

    @FXML
    private TableColumn<ProductTypeValue, Double> totalValueColumn;

    public void initialize() {
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        totalValueColumn.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
    }

    public void setData(List<Map.Entry<String, Double>> totalValueByType) {
        for (Map.Entry<String, Double> entry : totalValueByType) {
            // Format the value to 2 decimal places
            String formattedValue = String.format("%.2f", entry.getValue());

            // Replace comma with dot if needed
            formattedValue = formattedValue.replace(',', '.');

            // Parse the formatted string back to Double
            valueByTypeTable.getItems().add(new ProductTypeValue(entry.getKey(), Double.valueOf(formattedValue)));
        }
    }


}
