package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LecturerDashboardController {

    @FXML
    private Button manageStudentsButton;
    @FXML
    private Button lectureProfileButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button manageCourseButton;
    @FXML
    private Button dataRestrictionsButton;
    @FXML
    private Button formValidationButton;


    @FXML
    private TextField studentIdField;
    @FXML
    private TextField studentNameField;
    @FXML
    private Button addStudentButton;


    private void goToPage(String page) {
        try {
            AnchorPane newPage = FXMLLoader.load(getClass().getResource(page));
            Stage stage = (Stage) manageStudentsButton.getScene().getWindow();
            stage.setScene(new Scene(newPage));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageStudentsAction() {
        goToPage("manageStudents.fxml");
    }

    @FXML
    private void handleLectureProfileAction() {
        goToPage("lectureProfile.fxml");
    }

    @FXML
    private void handleLogoutAction() {
        goToPage("login.fxml");
    }

    @FXML
    private void handleManageCourseAction() {
        goToPage("manageCourse.fxml");
    }



    @FXML
    private void handleReportsAction() {
        goToPage("/com/example/demo1/reports.fxml");
    }

    // Method to handle adding a student to the database
    @FXML
    private void handleAddStudentAction() {

        long studentId;
        try {
            studentId = Long.parseLong(studentIdField.getText().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Student ID format. It must be a number.");
            return;
        }

        String studentName = studentNameField.getText().trim();


        if (studentName.isEmpty()) {
            System.out.println("Please fill in the Student Name.");
            return;
        }


        String url = "jdbc:postgresql://localhost:5432/postgres"; // replace with your database name
        String user = "postgres"; // replace with your database username
        String password = "041024"; // replace with your database password


        String query = "INSERT INTO students (\"student_Id\", \"student_name\") VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {


            statement.setLong(1, studentId);
            statement.setString(2, studentName);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Student added successfully!");
                // Clear the fields after successful addition
                studentIdField.clear();
                studentNameField.clear();
            } else {
                System.out.println("Failed to add student.");
            }

        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
    }

    public void testDatabaseConnection() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "041024";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "041024")) {
            if (connection != null) {
                System.out.println("Connected to the database successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
}
