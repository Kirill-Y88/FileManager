module y88.kirill.filemanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens y88.kirill.filemanager to javafx.fxml;
    exports y88.kirill.filemanager;
    exports y88.kirill.filemanager.controller;
    opens y88.kirill.filemanager.controller to javafx.fxml;
}