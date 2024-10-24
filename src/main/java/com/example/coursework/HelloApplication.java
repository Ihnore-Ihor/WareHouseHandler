package com.example.coursework;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.example.coursework.FileInterpreter.*;
import com.example.coursework.ProductManagment.*;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Component.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Складник");

        Image image = new Image(Objects.requireNonNull(getClass().getResource("/assets/icon.png")).toExternalForm());
        stage.getIcons().add(image);

        LoggingHandler logger = new LoggingHandler();
        List<Product> products = null;

        ProductManager manager = new ProductManager(logger);

        // Отримуємо контролер з FXMLLoader
        Controller controller = fxmlLoader.getController();

        // Передаємо об'єкти контролеру
        controller.initializeData(products, manager);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}