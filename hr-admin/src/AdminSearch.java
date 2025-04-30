import java.util.function.UnaryOperator;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.*;

public class AdminSearch extends GeneralScene {
    Label searchLabel, resultLabel;
    TextField searchField;
    Button searchButton, returnToMenu;
    ChoiceBox<String> searchType;
    String[] searchOptions = {"SSN", "Name", "Employee ID", "Date of Birth"};
    String searchTypeSelected;
    UnaryOperator<Change> filter;

    public AdminSearch() {
        super();
        setLabels();
        setFields();
        setButtons();
        setButtonActions();
        setSearchSelect();
        vbox.getChildren().addAll(searchLabel, searchField, resultLabel, searchType, searchButton, returnToMenu);
    }

    public void setLabels() {
        searchLabel = new Label("Search for employee data by selecting a search type and inputting the corresponding value");
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
            if (searchTypeSelected.equals("SSN") && searchField.getText().length() == 10 || (searchTypeSelected.equals("Name") && searchField.getText().length() > 0) || (searchTypeSelected.equals("Employee ID") && searchField.getText().length() == 10)) {
                // INCLUDE SEARCH LOGIC HERE
                App.adminSearchResults = new AdminDataDisplay();
                GeneralScene.setStage(GeneralScene.getStage(), App.adminSearchResults, "Search Results");
            } 
            else if (searchTypeSelected.equals("Name") && searchField.getText().length() > 0) {
                resultLabel.setText("Input valid data");
            }
        });
        returnToMenu.setOnAction(event -> {
            App.adminMenu = new AdminMainMenu();
            GeneralScene.setStage(GeneralScene.getStage(), App.employeeMenu, "Admin Utilities");
        });
    }
    
    public void setSearchSelect() {
        searchType = new ChoiceBox<>();
        searchType.getItems().addAll(searchOptions);
        searchType.setOnAction(event -> {
            searchTypeSelected = searchType.getValue();
            switch (searchTypeSelected) {
                case "SSN":
                    setFilter("SSN");
                    searchField.setTextFormatter(new TextFormatter<>(filter));
                    break;
                case "Name":
                    setFilter("Name");
                    searchField.setTextFormatter(new TextFormatter<>(filter));
                    // Set text formatter for name
                    break;
                case "Employee ID":
                    setFilter("Employee ID");
                    searchField.setTextFormatter(new TextFormatter<>(filter));
                    // Set text formatter for employee ID
                    break;
                case "Date of Birth":
                    setFilter("Date of Birth");
                    searchField.setTextFormatter(new TextFormatter<>(filter));
                    // Set text formatter for date of birth
                    break;
            }
        });
    }

    private void setFilter(String filterType) {
        if (filterType.equals("SSN")) {
            filter = change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d{0,10}")) {
                    return change;
                }
                return null;
            };
        }
    
        if (filterType.equals("Name")) {
            filter = change -> {
                String newText = change.getControlNewText();
                if (newText.matches("[a-zA-Z\\s]*")) {
                    return change;
                }
                return null;
            };
        }

        if (filterType.equals("Employee ID")) {
            filter = change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d{0,10}")) {
                    return change;
                }
                return null;
            };
        }
        if (filterType.equals("Date of Birth")) {
            filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9/]{0,10}")) {
                return change;
            }
            return null;
                };
        }
    }
    
}
