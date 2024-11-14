package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PupilManagementController {

    @FXML private TextField studentNameField;

    // Method to handle adding a student to the database
    @FXML
    private void handleAddStudent() {
        String name = studentNameField.getText();
        if (name.isEmpty()) {
            showAlert("Error", "Please enter the student's name.");
            return;
        }

        try (Connection connection = DatabaseUtil.getConnection()) {
            // Insert new student into the database, student_id will be auto-generated
            String sql = "INSERT INTO students (student_name) VALUES (?)";  // Do not insert student_id
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert("Success", "Student added successfully!");
                    studentNameField.clear();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add student. Please try again.");
        }
    }


    // Method to handle returning to the main menu
    @FXML
    private void handleReturnToMainMenu() {
        loadPage("/com/example/demo1/admin.fxml");
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to load a new page
    private void loadPage(String fxmlFilePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();

            Stage stage = (Stage) studentNameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the page.");
        }
    }

    // Method to initialize the page
    @FXML
    public void initialize() {
        // Set a default value for the student name field
        studentNameField.setText("Enter student name");


    }
}
