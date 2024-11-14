package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class CourseManagementController {

    @FXML
    private TextField chapterTitleField;
    @FXML
    private TextArea learningOutcomesArea;
    @FXML
    private Button addChapterButton;
    @FXML
    private Button returnButton;

    private String lecturerUsername; // This should be set when the lecturer logs in

    public void setLecturerUsername(String lecturerUsername) {
        this.lecturerUsername = lecturerUsername;
    }


    @FXML
    private void addChapter() {
        String chapterTitle = chapterTitleField.getText().trim();
        String learningOutcomes = learningOutcomesArea.getText().trim();

        if (chapterTitle.isEmpty() || learningOutcomes.isEmpty()) {
            showAlert("Please fill in both fields.");
            return;
        }

        try {
            // Calling the method to add the chapter to the database
            addChapterToDatabase(chapterTitle, learningOutcomes, lecturerUsername);
            showAlert("Chapter added successfully!");

            // Clearing the fields after adding
            chapterTitleField.clear();
            learningOutcomesArea.clear();
        } catch (SQLException e) {
            showAlert("Failed to add chapter: " + e.getMessage());
        }
    }

    private void addChapterToDatabase(String title, String outcomes, String username) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "041024";


        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "041024");
        String sql = "INSERT INTO chapters (title, outcomes, username) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, outcomes);
            pstmt.setString(3, username);
            pstmt.executeUpdate();
        } finally {

            conn.close();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void returnToMainMenu() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Lecturer.fxml")); // Ensure the path is correct
            Parent dashboardRoot = loader.load();


            Stage currentStage = (Stage) returnButton.getScene().getWindow();
            currentStage.setScene(new Scene(dashboardRoot));
            currentStage.setTitle("Dashboard");
            currentStage.show();
        } catch (IOException e) {
            showAlert("Failed to load the dashboard: " + e.getMessage());
        }
    }
}
