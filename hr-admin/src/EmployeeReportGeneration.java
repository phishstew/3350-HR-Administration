import javafx.scene.control.*;
public class EmployeeReportGeneration extends GeneralScene {
    Button backButton;
    Button generateReportButton;
    Label reportLabel;

    public EmployeeReportGeneration() {
        super();
        setButtons();
        setLabels();
        setButtonActions();
        vbox.getChildren().addAll(reportLabel, generateReportButton, backButton);
    }

    public void setButtons() {
        backButton = new Button("Back to Main Menu");
        generateReportButton = new Button("Generate Report");
        buttons.add(backButton);
        buttons.add(generateReportButton);
    }

    public void setLabels() {
        reportLabel = new Label("Employee Report Generation");
        labels.add(reportLabel);
    }

    public void setButtonActions() {
        backButton.setOnAction(event -> {
            App.employeeMenu = new EmployeeMainMenu();
            GeneralScene.setStage(GeneralScene.getStage(), App.employeeMenu, "Employee Main Menu");
        });

        generateReportButton.setOnAction(event -> {
            // INSERT REPORT GENERATION LOGIC HERE
            System.out.println("Generating report...");
            App.employeeReport = new EmployeeReportResults();
            GeneralScene.setStage(GeneralScene.getStage(), App.employeeReport, "Report Results");
            // After generating the report, you can navigate to another scene if needed
        });
    }
    
}
