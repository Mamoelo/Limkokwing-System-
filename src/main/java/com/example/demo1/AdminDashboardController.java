package com.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboardController {

    @FXML
    private Button logoutButton;
    @FXML
    private Button manageLecturersButton;
    @FXML
    private Button manageStudentsButton;
    @FXML
    private Button manageModulesButton;
    @FXML
    private Button manageSemestersButton;
    @FXML
    private Button manageClassesButton;
    @FXML
    private Button manageAcademicYearButton;

    // Labels to display the user details
    @FXML
    private Label userIdLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label roleLabel;

    private String userId;
    private String username;
    private String role;

    // method used to load user details after login
    public void initializeUserDetails(String username) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            // Query to get user details from the database
            String query = "SELECT user_id, username, role FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getString("user_id");
                this.username = resultSet.getString("username");
                role = resultSet.getString("role");

                // Updating the labels with the retrieved data
                userIdLabel.setText("User ID: " + userId);
                usernameLabel.setText("Username: " + this.username);
                roleLabel.setText("Role: " + role);
            } else {
                showErrorDialog("User not found", "No user found with the username: " + username);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "An error occurred while fetching user details.");
        }
    }

    // Handling the logout action
    @FXML
    public void handleLogout() {
        goToPage("/com/example/demo1/login.fxml");
    }

    // Navigating to the Manage Lecturers page
    @FXML
    public void handleLecturers() {
        goToPage("/com/example/demo1/manageLecturers.fxml");
    }

    // Navigating to the Manage Academic Year page
    @FXML
    public void handleAcademicYear() {
        goToPage("/com/example/demo1/manageAcademicYear.fxml");
    }

    // Navigate to the Manage Modules page
    @FXML
    public void handleModules() {
        goToPage("/com/example/demo1/manageModules.fxml");
    }

    // Navigating to the Manage Semesters page
    @FXML
    public void handleSemesters() {
        goToPage("/com/example/demo1/manageSemesters.fxml");
    }

    // Navigating to the Manage Classes page
    @FXML
    public void handleClasses() {
        goToPage("/com/example/demo1/manageClasses.fxml");
    }

    // Navigating to the Manage Students page
    @FXML
    public void handleStudents() {
        goToPage("/com/example/demo1/managePupils.fxml");
    }

    @FXML
    public void handleLogBook() {
        goToPage("/com/example/demo1/Logbook.fxml");
    }
    // Common method for navigation to different pages
    private void goToPage(String fxmlFilePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Logs details of the error
            showErrorDialog("Error loading page", "Could not load " + fxmlFilePath);
        }
    }

    // Method to show an error dialog
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
