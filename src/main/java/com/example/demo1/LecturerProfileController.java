package com.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LecturerProfileController {

    @FXML private TextField usernameField;
    @FXML private TextField nameField;
    @FXML private TextField roleField;
    @FXML private TextField moduleField;

    private int user_id;
    private String username;
    @FXML
    private void handleSaveProfile() {
        String updatedUsername = usernameField.getText();
        String updatedName = nameField.getText();
        String updatedRole = roleField.getText();
        String updatedModule = moduleField.getText();

        if (updatedUsername.isEmpty() || updatedName.isEmpty() || updatedRole.isEmpty() || updatedModule.isEmpty()) {
            showAlert("Error", "Please fill all fields.");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {

            String checkProfileSQL = "SELECT * FROM users WHERE user_id = ? AND username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkProfileSQL)) {
                checkStmt.setInt(1, user_id);
                checkStmt.setString(2, username);
                ResultSet resultSet = checkStmt.executeQuery();

                if (resultSet.next()) {

                    String updateProfileSQL = "UPDATE profile SET name = ?, role = ?, module_taught = ? WHERE user_id = ? AND username = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateProfileSQL)) {
                        updateStmt.setString(1, updatedName);
                        updateStmt.setString(2, updatedRole);
                        updateStmt.setString(3, updatedModule);
                        updateStmt.setInt(4, user_id);
                        updateStmt.setString(5, username);
                        updateStmt.executeUpdate();
                    }
                } else {
                    String insertProfileSQL = "INSERT INTO profile (user_id, username, name, role, module_taught) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertProfileSQL)) {
                        insertStmt.setInt(1, user_id);
                        insertStmt.setString(2, username);
                        insertStmt.setString(3, updatedName);
                        insertStmt.setString(4, updatedRole);
                        insertStmt.setString(5, updatedModule);
                        insertStmt.executeUpdate();
                    }
                }
            }

            showAlert("Success", "Profile updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save profile. Please try again.");
        }
    }

    public void setUserDetails(int user_id, String username) {
        // Checking if the user_id is valid
        if (user_id <= 0) {
            showAlert("Error", "Invalid user ID.");
            return;
        }

        int[] validUserIds = {297, 749, 1014, 2556, 3162, 3941, 5273, 8149};

        boolean isValidUser = false;
        for (int validUserId : validUserIds) {
            if (user_id == validUserId) {
                isValidUser = true;
                break;
            }
        }

        if (isValidUser) {
            this.user_id = user_id;
            this.username = username;
            if (usernameField != null) {
                usernameField.setText(username);  // Display the username in the field
                usernameField.setEditable(false); // Make the username field non-editable
            }
        } else {
            showAlert("Error", "Invalid user ID.");
        }
    }

    @FXML
    private void handleReturn() {
        loadPage("/com/example/demo1/Lecturer.fxml");
    }

    private void loadPage(String fxmlFilePath) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();

            Stage stage = (Stage) nameField.getScene().getWindow();  // or use any other node from the current page
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error", "Could not load the page.");
        }
    }

    // Helper method to show error alerts
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    

}
