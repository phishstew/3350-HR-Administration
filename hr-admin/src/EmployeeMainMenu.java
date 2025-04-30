public class EmployeeMainMenu extends MainMenu {

    public EmployeeMainMenu() {
        super();
        this.setButtonActions();
        vbox.getChildren().addAll(usr, search, reportGen, logout);
    }
    public void setButtonActions() {
        search.setOnAction(event -> {
            App.employeeSearchMenu = new EmployeeSearch();
            GeneralScene.setStage(GeneralScene.getStage(), App.employeeSearchMenu, "Search");
        });
        reportGen.setOnAction(event -> {
            // INSERT REPORT GENERATION LOGIC HERE
            App.employeeReportGen = new EmployeeReportGeneration();
            GeneralScene.setStage(GeneralScene.getStage(), App.employeeReportGen, "Employee Report Generation");
        });
        logout.setOnAction(event -> {
            App.loginScreen = new Login();
            GeneralScene.setStage(GeneralScene.getStage(), App.loginScreen, "Log in");
        });
    }
    
}
