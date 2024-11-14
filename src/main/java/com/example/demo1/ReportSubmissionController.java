package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportSubmissionController {

    @FXML
    private ComboBox<String> classComboBox;
    @FXML
    private ComboBox<String> moduleComboBox;
    @FXML
    private TextArea challengesTextArea;
    @FXML
    private TextArea recommendationsTextArea;
    @FXML
    private Button submitButton;
    @FXML
    private Button returnButton;

    private Connection connection;

    public void initialize() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "041024");
            List<String> classes = fetchClassNames();
            classComboBox.getItems().addAll(classes);
            List<String> modules = fetchModuleNames();
            moduleComboBox.getItems().addAll(modules);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> fetchClassNames() {
        List<String> classNames = new ArrayList<>();
        String query = "SELECT class_name FROM classes";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                classNames.add(resultSet.getString("class_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classNames;
    }

    private List<String> fetchModuleNames() {
        List<String> moduleNames = new ArrayList<>();
        String query = "SELECT module_name FROM modules";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                moduleNames.add(resultSet.getString("module_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return moduleNames;
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String selectedClass = classComboBox.getValue();
        String selectedModule = moduleComboBox.getValue();
        String challenges = challengesTextArea.getText();
        String recommendations = recommendationsTextArea.getText();

        if (selectedClass == null || selectedModule == null || challenges.trim().isEmpty() || recommendations.trim().isEmpty()) {
            showAlert(AlertType.ERROR, "Submission Error", "All fields are required. Please fill in all fields.");
            return;
        }

        try {
            String insertQuery = "INSERT INTO reports (class_name, module_name, challenges, recommendations) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setString(1, selectedClass);
                statement.setString(2, selectedModule);
                statement.setString(3, challenges);
                statement.setString(4, recommendations);
                statement.executeUpdate();

                showAlert(AlertType.INFORMATION, "Success", "Your report has been successfully submitted!");
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Submission Error", "There was an error submitting the report. Please try again.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReturn(ActionEvent event) {
        try {
            Stage stage = (Stage) returnButton.getScene().getWindow(); // Get the current window
            Scene mainMenuScene = new Scene(new javafx.fxml.FXMLLoader(getClass().getResource("principal_lecturer.fxml")).load());
            stage.setScene(mainMenuScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to show alert dialog
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
