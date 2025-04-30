import javafx.scene.control.*;
public class AdminDataDisplay extends GeneralScene {
    Button backButton;
    TableView results;
    public AdminDataDisplay() {
        super();
        setButtons();
        setButtonActions();
        vbox.getChildren().addAll(results, backButton);
    }
    public void setButtons() {
        backButton = new Button("Back to Search");
    };
    public void setButtonActions() {
        backButton.setOnAction(event -> {
            App.adminSearchMenu = new AdminSearch();
            GeneralScene.setStage(GeneralScene.getStage(), App.adminSearchMenu, "Admin Search");
        });
    };

    public void setResults() {
        results = new TableView();
        results.setEditable(true);
        // TO BE COMPLETED
    }
    
}
