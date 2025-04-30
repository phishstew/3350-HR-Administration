import java.util.function.UnaryOperator;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.*;

public class EmployeeSearch extends GeneralScene {
    Label searchLabel, resultLabel;
    TextField searchField;
    Button searchButton, returnToMenu;

    public EmployeeSearch() {
        super();
        setLabels();
        setFields();
        setButtons();
        setButtonActions();
        vbox.getChildren().setAll(searchLabel, searchField, resultLabel, searchButton, returnToMenu);
    }

    public void setLabels() {
        searchLabel = new Label("Search for your data by inputting your SSN in a 10-digit numerical format");
        resultLabel = new Label("");
    }

    public void setFields() {
        searchField = new TextField();

        UnaryOperator<Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,10}")) {
                return change;
            }
            return null;
        };

        searchField.setTextFormatter(new TextFormatter<>(filter));
    }

    public void setButtons() {
        searchButton = new Button("Search");
        returnToMenu = new Button("Return to main menu");
    }

    public void setButtonActions() {
        searchButton.setOnAction(event -> {
            if (searchField.getText().length() == 10) {
                // INCLUDE SEARCH LOGIC HERE
                App.employeeSearchResults = new EmployeeDataDisplay();
                GeneralScene.setStage(GeneralScene.getStage(), App.employeeSearchResults, "Search Results");
            }
            else {
                resultLabel.setText("Enter a 10-digit value");
            }
        });
        returnToMenu.setOnAction(event -> {
            App.employeeMenu = new EmployeeMainMenu();
            GeneralScene.setStage(GeneralScene.getStage(), App.employeeMenu, "Employee Utilities");
        });
    }
    
}
