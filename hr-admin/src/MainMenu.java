import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.scene.control.*;

public abstract class MainMenu extends GeneralScene {
    Label usr;
    static String name = "";
    Button search, reportGen, logout;
    
    public MainMenu() {
        super();
        setGreeting();
        setButtons();
        setButtonActions();
    }

    public void setGreeting() {
        usr = new Label("Welcome, " + name);
    }

    public void setButtons() {
        search = new Button("Search Records");
        reportGen = new Button("Generate Report");
        logout = new Button("Log out");
    }

    public static void transmitUsername(String receivedName) {
        name = receivedName;
    }

    public void setButtonActions() {
        logout.setOnAction(event -> {
            GeneralScene.setStage(GeneralScene.getStage(), App.loginScreen, "Employee Management System");
        });
    }
}
