import javafx.scene.control.*;
public class AdminMainMenu extends MainMenu {
    Button updateData;

    public AdminMainMenu() {
        super();
        this.setButtons();
        this.setButtonActions();
        vbox.getChildren().addAll(usr, search, reportGen, updateData, logout);
    }
    
    public void setButtons() {
        super.setButtons();
        updateData = new Button("Update Data");
    }

        public void setButtonActions() {
        search.setOnAction(event -> {
            App.adminSearchMenu = new AdminSearch();
            GeneralScene.setStage(GeneralScene.getStage(), App.adminSearchMenu, "Admin Search");
        });
        reportGen.setOnAction(event -> {
            // INSERT REPORT GENERATION LOGIC HERE
            App.adminReportGen = new AdminReportGeneration();
            GeneralScene.setStage(GeneralScene.getStage(), App.adminReportGen, "Admin Report Generation");
        });
        updateData.setOnAction(event -> {
            App.adminUpdateData = new AdminDataUpdater();
            GeneralScene.setStage(GeneralScene.getStage(), App.adminUpdateData, "Admin Update Data");
        });
        logout.setOnAction(event -> {
            App.loginScreen = new Login();
            GeneralScene.setStage(GeneralScene.getStage(), App.loginScreen, "Log in");
        });
    }

}
