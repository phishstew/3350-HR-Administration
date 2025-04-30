import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.ArrayList;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public abstract class GeneralScene {
    Scene scene;
    VBox vbox;
    public static Stage globalStage;
    ArrayList<Button> buttons = new ArrayList<>();
    ArrayList<Label> labels = new ArrayList<>();

    public GeneralScene() {
        vbox = new VBox(10);
        scene = new Scene(vbox, 640, 480);
    }

    public static void initializeStage(Stage stage) {
        globalStage = stage;
    }
    
    public static Stage getStage() {
        return globalStage;
    }

    public Scene getScene() {
        return this.scene;
    }

    public static void setStage(Stage stage, GeneralScene scene, String title) {
        stage.setScene(scene.getScene());
        stage.setTitle(title);
    }

    public void setFields() {};

    public void setButtons() {
    };
    
    public void setLabels() {
    };

    public void setButtonActions() {
    };

}