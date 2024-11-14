package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    public void initialize() {

        roleComboBox.setItems(FXCollections.observableArrayList("Lecturer", "Principal Lecturer", "Admin"));
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String selectedRole = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || selectedRole == null) {
            showAlert("Login Error", "Please enter username, password, and select a role.");
            return;
        }

        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "SELECT role FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                String roleInDatabase = resultSet.getString("role");

                if (roleInDatabase.equals(selectedRole)) {

                    saveLoginHistory(username);

                    loadDashboardScreen(selectedRole);
                } else {
                    showAlert("Role Error", "Selected role does not match your registered role.");
                }
            } else {

                showAlert("Login Error", "Incorrect username or password. Please try again or register.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while accessing the database.");
        }
    }

    private void saveLoginHistory(String username) {
        String sql = "INSERT INTO login_history (login_date, username) VALUES (?, ?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis())); // Current timestamp
            statement.setString(2, username);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save login history.");
        }
    }

    @FXML
    private void handleCancel() {

        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void handleRegister() {
        loadRegisterScreen();
    }

    private void loadDashboardScreen(String role) {
        String fxmlFile;
        switch (role) {
            case "Lecturer":
                fxmlFile = "/com/example/demo1/Lecturer.fxml";
                break;
            case "Principal Lecturer":
                fxmlFile = "/com/example/demo1/principal_lecturer.fxml";
                break;
            case "Admin":
                fxmlFile = "/com/example/demo1/admin.fxml";
                break;
            default:
                showAlert("Error", "Invalid role selection.");
                return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the dashboard screen for " + role + ".");
        }
    }

    private void loadRegisterScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo1/register.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the registration screen.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
