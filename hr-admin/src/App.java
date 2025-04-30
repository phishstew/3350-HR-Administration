import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    static Login loginScreen = new Login();
    static AccountCreation accountCreator = new AccountCreation();
    static MainMenu employeeMenu;
    static MainMenu adminMenu;
    static EmployeeSearch employeeSearchMenu;
    static EmployeeDataDisplay employeeSearchResults;
    static EmployeeReportGeneration employeeReportGen;
    static EmployeeReportResults employeeReport;
    static AdminSearch adminSearchMenu;
    static AdminDataDisplay adminSearchResults;
    static AdminDataDisplay adminQueryResults;
    static AdminReportGeneration adminReportGen;
    static AdminDataUpdater adminUpdateData;

    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GeneralScene.initializeStage(primaryStage);
        GeneralScene.setStage(primaryStage, loginScreen, "Log in");
        primaryStage.show();
    }
}