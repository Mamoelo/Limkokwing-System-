package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentsManagementController {

    @FXML private VBox studentsVBox; // The VBox where CheckBoxes will be added
    @FXML private Button submitAttendanceButton;
    @FXML private Button returnButton;

    private ObservableList<CheckBox> studentCheckBoxes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Load students and create CheckBoxes for each student
        loadStudents();
    }

    private void loadStudents() {
        String sql = "SELECT student_id, student_name FROM students"; // Adjust query as needed

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String studentName = resultSet.getString("student_name");
                int studentId = resultSet.getInt("student_id");

                // Create a new CheckBox for each student
                CheckBox checkBox = new CheckBox(studentName);
                checkBox.setUserData(studentId);  // Store student ID in the CheckBox's user data
                studentCheckBoxes.add(checkBox);
            }

            // Add all CheckBoxes to the VBox
            studentsVBox.getChildren().setAll(studentCheckBoxes);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load students.");
        }
    }

    @FXML
    private void handleSubmitAttendance() {
        // Flag to track if any attendance is marked
        boolean attendanceSubmitted = false;

        // Get the selected students (those with checked CheckBoxes)
        for (CheckBox checkBox : studentCheckBoxes) {
            if (checkBox.isSelected()) {
                int studentId = (int) checkBox.getUserData();
                // Insert the selected student into the attendance table
                markAttendance(studentId);
                attendanceSubmitted = true;  // Set flag to true if at least one student is marked
            }
        }

        // If attendance was marked, show a success message
        if (attendanceSubmitted) {
            showAlert("Success", "Attendance has been successfully submitted.");
        } else {
            showAlert("No Attendance", "No students were selected for attendance.");
        }
    }


    private void markAttendance(int studentId) {
        String sql = "INSERT INTO attendance (student_id, attendance_date) VALUES (?, CURRENT_TIMESTAMP)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to mark attendance.");
        }
    }

    @FXML
    private void handleReturnToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/lecturer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return to the main dashboard.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
