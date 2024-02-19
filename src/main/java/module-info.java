module org.example.program {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.program4 to javafx.fxml;
    exports org.example.program4;
}