import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

import javafx.scene.control.*;
public class AdminReportGeneration extends GeneralScene {
    Button backButton;
    Button generateReportButton;
    Label reportLabel;
    ChoiceBox<String> reportTypeChoiceBox;
    String[] reportTypes = {"Pay for Month by Job Title", "Pay for Month by Division", "Pay Statement History"};
    String selectedReportType;
    ReportService reportService;
    DataSource dataSource = new BasicDataSource();

    public AdminReportGeneration() {
        super();
        ((BasicDataSource) dataSource).setDriverClassName("com.mysql.cj.jdbc.Driver");
        ((BasicDataSource)dataSource).setUsername("username");
        ((BasicDataSource)dataSource).setPassword("password");
        ((BasicDataSource)dataSource).setUrl("jdbc:mysql://<host>:<port>/<database>");
        reportService = new ReportService(dataSource);
        setButtons();
        setLabels();
        setReportTypeChoiceBox();
        setButtonActions();
        vbox.getChildren().addAll(reportLabel, generateReportButton, reportTypeChoiceBox, backButton);
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
            App.adminMenu = new AdminMainMenu();
            GeneralScene.setStage(GeneralScene.getStage(), App.employeeMenu, "Admin Main Menu");
        });

        generateReportButton.setOnAction(event -> {
            // INSERT REPORT GENERATION LOGIC HERE
            System.out.println("Generating report...");
            try {
                reportService.generatePayStatementHistory(true, 999);
            } catch (ReportException e) {
                e.printStackTrace();
                // Optionally, show an error message to the user
                System.out.println("Failed to generate report: " + e.getMessage());
            }
            App.employeeReport = new EmployeeReportResults();
            GeneralScene.setStage(GeneralScene.getStage(), App.employeeReport, "Report Results");
        });
    }
    public void setReportTypeChoiceBox() {
        reportTypeChoiceBox = new ChoiceBox<>();
        reportTypeChoiceBox.getItems().addAll(reportTypes);
        reportTypeChoiceBox.setOnAction(event -> {
            selectedReportType = reportTypeChoiceBox.getValue();
            System.out.println("Selected report type: " + selectedReportType);
        });
    }
    
}
