package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ManageSemesterController {

    @FXML private ListView<String> semesterListView;
    @FXML private Button addSemesterButton;
    @FXML private Button returnToMainMenuButton;

    private ObservableList<String> semesterList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadSemesters();
        semesterListView.setItems(semesterList);
    }

    private void loadSemesters() {
        String query = "SELECT semester_name FROM semesters";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            semesterList.clear();
            while (resultSet.next()) {
                String semesterName = resultSet.getString("semester_name");
                semesterList.add(semesterName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while loading semesters.");
        }
    }

    @FXML
    private void handleAddSemester() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Semester");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Semester Name (e.g., Semester 1):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(semesterName -> {
            if (semesterName.isEmpty()) {
                showAlert("Input Error", "Please enter a valid semester name.");
                return;
            }

            String insertQuery = "INSERT INTO semesters (semester_name) VALUES (?)";
            try (Connection connection = DatabaseUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(insertQuery)) {

                statement.setString(1, semesterName);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    semesterList.add(semesterName);
                    showAlert("Success", "Semester added successfully.");
                } else {
                    showAlert("Failure", "Failed to add semester.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while adding semester.");
            }
        });
    }

    @FXML
    private void handleReturnToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/admin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnToMainMenuButton.getScene().getWindow();
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
