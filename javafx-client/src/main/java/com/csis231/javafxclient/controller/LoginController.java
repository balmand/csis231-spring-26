package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.service.ApiClient;
import com.csis231.javafxclient.model.LoginDto;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private final ApiClient apiClient = new ApiClient();

    private Runnable onLoginSuccess;

    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }
    @FXML
    private void handleLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.isEmpty() || password.isEmpty()){
            statusLabel.setText("Username and password cannot be empty");
            return;
        }
        try {
            boolean success = apiClient.authenticate(new LoginDto(username, password));
            if (success) {
                if (onLoginSuccess != null) {
                    onLoginSuccess.run();
                }
            } else {
                showError();
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error connecting to server");
        }


    }

    private void showError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);
        alert.setContentText("Wrong Username or Password");
        alert.showAndWait();
    }

}