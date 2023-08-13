module com.mediamanager.mediamanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;


    opens com.mediamanager.mediamanager to javafx.fxml;
    exports com.mediamanager.mediamanager;
}