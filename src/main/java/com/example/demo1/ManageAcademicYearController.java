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

public class ManageAcademicYearController {

    @FXML private ListView<String> academicYearListView;
    @FXML private Button addAcademicYearButton;
    @FXML private Button exitToMainMenuButton;

    private ObservableList<String> academicYearList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadAcademicYears();
        academicYearListView.setItems(academicYearList);
    }

    private void loadAcademicYears() {
        String query = "SELECT academic_year FROM academic_year";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            academicYearList.clear();
            while (resultSet.next()) {
                String academicYear = resultSet.getString("academic_year");
                academicYearList.add(academicYear);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while loading academic years.");
        }
    }

    @FXML
    private void handleAddAcademicYear() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Academic Year");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Academic Year (e.g., 2024/2025):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(academicYear -> {
            if (academicYear.isEmpty() || !academicYear.matches("\\d{4}/\\d{4}")) {
                showAlert("Input Error", "Please enter a valid academic year format (e.g., 2024/2025).");
                return;
            }

            String insertQuery = "INSERT INTO academic_year (academic_year) VALUES (?)";
            try (Connection connection = DatabaseUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(insertQuery)) {

                statement.setString(1, academicYear);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    academicYearList.add(academicYear);
                    showAlert("Success", "Academic year added successfully.");
                } else {
                    showAlert("Failure", "Failed to add academic year.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while adding academic year.");
            }
        });
    }

    @FXML
    private void handleExitToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/admin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) exitToMainMenuButton.getScene().getWindow();
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
