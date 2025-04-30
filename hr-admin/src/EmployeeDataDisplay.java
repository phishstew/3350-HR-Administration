import javafx.scene.control.*;
public class EmployeeDataDisplay extends GeneralScene {

    Button backButton;

    public EmployeeDataDisplay() {
        super();
        setButtons();
        setButtonActions();
        vbox.getChildren().add(backButton);
    }
    
    public void setButtons() {
        backButton = new Button("Back to Search");
        buttons.add(backButton);
    };

    public void setButtonActions() {
        backButton.setOnAction(event -> {
            App.employeeSearchMenu = new EmployeeSearch();
            GeneralScene.setStage(GeneralScene.getStage(), App.employeeSearchMenu, "Employee Search");
        });
    };

}
