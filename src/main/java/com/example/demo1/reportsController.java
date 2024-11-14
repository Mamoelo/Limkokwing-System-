package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class reportsController {

    @FXML
    private ComboBox<String> classComboBox;
    @FXML
    private ComboBox<String> moduleComboBox;
    @FXML
    private TextArea challengesTextArea;
    @FXML
    private TextField usernameField; // New TextField for username
    @FXML
    private Button submitButton;
    @FXML
    private Button returnButton;

    private Connection connection;

    // Initialize method, fetching class and module names, and setting up database connection
    public void initialize() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "041024");

            // Fetch class names and modules from the database
            List<String> classes = fetchClassNames();
            classComboBox.getItems().addAll(classes);

            List<String> modules = fetchModuleNames();
            moduleComboBox.getItems().addAll(modules);

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "An error occurred while fetching data.");
        }
    }

    // Fetch the list of classes from the database
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
            showErrorDialog("Database Error", "An error occurred while fetching class names.");
        }
        return classNames;
    }

    // Fetch the list of modules from the database
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
            showErrorDialog("Database Error", "An error occurred while fetching module names.");
        }
        return moduleNames;
    }

    // Handle the submission of the report
    @FXML
    private void handleSubmit(ActionEvent event) {
        String username = usernameField.getText(); // Get the username
        String selectedClass = classComboBox.getValue();
        String selectedModule = moduleComboBox.getValue();
        String challenges = challengesTextArea.getText();

        // Check if all fields are filled
        if (username.trim().isEmpty() || selectedClass == null || selectedModule == null || challenges.trim().isEmpty()) {
            showErrorDialog("Incomplete Information", "Please complete all fields before submitting.");
            return;
        }

        try {
            // Insert report with the username, class, module, and challenges
            String insertQuery = "INSERT INTO lecturer_reports (username, class_name, module_name, challenges) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setString(1, username);
                statement.setString(2, selectedClass);
                statement.setString(3, selectedModule);
                statement.setString(4, challenges);
                statement.executeUpdate();

                showInfoDialog("Report Submitted", "Your report has been submitted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "An error occurred while saving the report.");
        }
    }

    // Handle returning to the main menu
    @FXML
    private void handleReturn(ActionEvent event) {
        try {
            Stage stage = (Stage) returnButton.getScene().getWindow();
            Scene mainMenuScene = new Scene(new javafx.fxml.FXMLLoader(getClass().getResource("Lecturer.fxml")).load());
            stage.setScene(mainMenuScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Navigation Error", "An error occurred while navigating.");
        }
    }

    // Method to show error dialogs
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to show information dialogs
    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
