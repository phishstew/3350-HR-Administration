import javafx.scene.control.*;

public class Login extends GeneralScene implements AccountHandler {
    Label usr, pwd, result;
    TextField usrField;
    PasswordField pwdField;
    Button login, createUser;

    public Login() {
        super();
        this.setFields();
        this.setLabels();
        this.setButtons();
        this.setButtonActions();
        vbox.getChildren().addAll(usr, usrField, pwd, pwdField, login, createUser, result);
    }

    public void setFields() {
        usrField = new TextField();
        pwdField = new PasswordField();
    }

    public void setLabels() {
        usr = new Label("Username");
        pwd = new Label("Password");
        result = new Label("");
    }

    public void setButtons() {
        login = new Button("Login");
        createUser = new Button("Create Account");
    }
    
    public void setButtonActions() {
        login.setOnAction(event -> {
            String enteredUsername = usrField.getText();
            String enteredPassword = pwdField.getText();

            if (LoginRecords.validateLogin(enteredUsername, enteredPassword)) {
                result.setText("Login successful!");
                LoginRecords.activeUser = LoginRecords.credentials.get(enteredUsername);
                if (LoginRecords.activeUser.getEmpType().equals("Administrator")) {
                    App.adminMenu = new AdminMainMenu();
                    GeneralScene.setStage(GeneralScene.getStage(), App.adminMenu, "Admin Utilities");
                } else if (LoginRecords.activeUser.getEmpType().equals("Employee")) {
                    App.employeeMenu = new EmployeeMainMenu();
                    GeneralScene.setStage(GeneralScene.getStage(), App.employeeMenu, "Employee Utilities");
                }
                vbox.getChildren().add(0, new Label("Welcome, " + enteredUsername));
            } else {
                result.setText("Login failed. Please check your credentials.");
            }
        });

        createUser.setOnAction(event -> {
            GeneralScene.setStage(GeneralScene.getStage(), App.accountCreator, "Create an account");
        });

    }
}
