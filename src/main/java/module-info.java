module com.example.coursework {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.logging;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires annotations;

    opens com.example.coursework to javafx.fxml;  // відкриття пакета для FXML
    opens com.example.coursework.ProductManagment to javafx.base;  // для доступу до класів продуктів

    exports com.example.coursework;
    exports com.example.coursework.FileInterpreter;
}