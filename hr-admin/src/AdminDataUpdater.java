import javafx.scene.control.*;
public class AdminDataUpdater extends GeneralScene {
    TextField lowerSalary;
    TextField upperSalary;
    TextField percentDelta;
    Button backButton;
    Button updateButton;
    public AdminDataUpdater() {
        super();
        setButtons();
        setButtonActions();
        vbox.getChildren().addAll(lowerSalary, upperSalary, percentDelta, backButton, updateButton);
    }
    public void setButtons() {
        backButton = new Button("Back to Main Menu");
        updateButton = new Button("Update Data");
    }
    public void setButtonActions() {
        backButton.setOnAction(event -> {
            App.adminMenu = new AdminMainMenu();
            GeneralScene.setStage(GeneralScene.getStage(), App.adminMenu, "Admin Main Menu");
        });

        updateButton.setOnAction(event -> {
            // INSERT DATA UPDATE LOGIC HERE
            System.out.println("Updating data...");
            App.adminQueryResults = new AdminDataDisplay();
            GeneralScene.setStage(GeneralScene.getStage(), App.adminMenu, "Results");
        });
    }

    public void setTextFields() {
        lowerSalary = new TextField();
        upperSalary = new TextField();
        percentDelta = new TextField();
        lowerSalary.setPromptText("Enter lower salary bound");
        upperSalary.setPromptText("Enter upper salary bound");
        percentDelta.setPromptText("Enter percent increase");
    }
}
