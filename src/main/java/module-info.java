module project.projecte {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens project.projecte to javafx.fxml;
    exports project.projecte.Main;
}
