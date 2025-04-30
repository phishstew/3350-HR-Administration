import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.scene.control.*;

public class AccountCreation extends GeneralScene implements AccountHandler {
    Label usr, pwd, confirmPwd, role, result;
    TextField usrField;
    PasswordField pwdField, confirmPwdField;
    Button createUser, return_butt;
    ChoiceBox<String> roleSelect;

    public AccountCreation() {
        super();
        this.setLabels();
        this.setFields();
        this.setRoleSelect();
        this.setButtons();
        vbox.getChildren().addAll(usr, usrField, pwd, pwdField, confirmPwd, confirmPwdField, role, roleSelect, createUser, return_butt, result);
    }
    public void setFields() {
        usrField = new TextField();
        pwdField = new PasswordField();
        confirmPwdField = new PasswordField();
    }

    public void setLabels() {
        usr = new Label("Choose a username");
        pwd = new Label("Choose a password");
        confirmPwd = new Label("Confirm password");
        result = new Label("");
    }

    public void setButtons() {
        createUser = new Button("Create Account");
        return_butt = new Button("Return to home");

        createUser.setOnAction(event -> {
            String enteredUsername = usrField.getText();
            String enteredPassword = pwdField.getText();
            String confirmedPassword = confirmPwdField.getText();

            if (confirmedPassword.equals(enteredPassword)) {
                LoginRecords.addRecord(enteredUsername, confirmedPassword);
                LoginRecords.setEmpType(enteredUsername, roleSelect.getValue());
                result.setText("Success! Return to the login page");
            } else {
                result.setText("Your passwords do not match");
            }
        });

        return_butt.setOnAction(event -> {
            GeneralScene.setStage(GeneralScene.getStage(), App.loginScreen, "Employee Management System");
        });

    }

    public void setRoleSelect() {
        roleSelect = new ChoiceBox<>();
        role = new Label("Role");
        roleSelect.getItems().addAll("Administrator", "Employee");
    }
}
