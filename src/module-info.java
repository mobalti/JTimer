module JTimer.V {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    opens model to javafx.base;
    opens app;
}