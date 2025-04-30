module com.hr_project {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.hr_project to javafx.fxml;
    exports com.hr_project;
}
