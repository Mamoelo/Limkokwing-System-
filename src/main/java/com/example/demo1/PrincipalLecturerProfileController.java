package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PrincipalLecturerProfileController {

    @FXML private TextField usernameField;
    @FXML private TextField nameField;
    @FXML private TextField roleField;
    @FXML private Button saveButton;
    @FXML private Button editProfileButton;
    @FXML private Button returnButton;

    @FXML
    private void initialize() {
        // Ensure that the fields are non-editable when the profile is initially loaded
        setFieldsEditable(false);
    }

    @FXML
    private void handleSaveProfile() {
        String username = usernameField.getText();
        String name = nameField.getText();
        String role = roleField.getText();

        if (username.isEmpty() || name.isEmpty() || role.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        String sql = "INSERT INTO profiles (username, name, role) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, name);
            statement.setString(3, role);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Success", "Profile saved successfully.");
                setFieldsEditable(false);  // Make fields non-editable after saving
            } else {
                showAlert("Failure", "Failed to save profile.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while saving profile details.");
        }
    }

    @FXML
    private void handleEditProfile() {
        // Enable fields for editing when the user clicks "Edit Profile"
        setFieldsEditable(true);
    }

    @FXML
    private void handleReturnToMainDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/principal_lecturer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return to the main dashboard.");
        }
    }

    private void setFieldsEditable(boolean editable) {
        // Enable or disable fields based on the passed argument
        usernameField.setEditable(editable);
        nameField.setEditable(editable);
        roleField.setEditable(editable);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
