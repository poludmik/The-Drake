module java.myfolder.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens myfolder.demo to javafx.fxml;
    exports myfolder.demo;
}