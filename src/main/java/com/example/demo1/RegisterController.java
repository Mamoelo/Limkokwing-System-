package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    public void initialize() {
        // Populate the ComboBox with roles
        roleComboBox.setItems(FXCollections.observableArrayList("Lecturer", "Principal Lecturer", "Admin"));
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            showAlert("Registration Error", "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Registration Error", "Passwords do not match.");
            return;
        }

        // Generate a new user ID (could be done in the database or application logic)
        int userId = generateUserId();

        // Save the user data to the database
        if (saveUserToDatabase(userId, username, password, role)) {
            showAlert("Registration Successful", "You have registered successfully!");
            clearFields();
            loadLoginScreen();
        } else {
            showAlert("Registration Error", "Could not register the user. Please try again.");
        }
    }

    private int generateUserId() {
        // For simplicity, using a random number; this should be replaced with a proper ID generation method
        return (int) (Math.random() * 10000); // Replace with actual logic as needed
    }

    private boolean saveUserToDatabase(int userId, String username, String password, String role) {
        String url = "jdbc:postgresql://localhost:5432/postgres"; // Update with your database URL
        String user = "postgres"; // Your database username
        String pass = "041024"; // Your database password

        String sql = "INSERT INTO users (user_id, username, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, password); // Ensure to hash the password in production
            pstmt.setString(4, role);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleCancel() {
        loadLoginScreen();
    }

    @FXML
    private void handleLogin() {
        loadLoginScreen();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleComboBox.setValue(null);
    }

    private void loadLoginScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo1/login.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the login screen.");
        }
    }
}
