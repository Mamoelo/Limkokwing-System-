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
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ManageClassesController {

    @FXML private ListView<String> classListView;
    @FXML private Button addClassButton;
    @FXML private Button returnToMainMenuButton;

    private ObservableList<String> classList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadClasses();
        classListView.setItems(classList);
    }

    private void loadClasses() {
        String query = "SELECT class_name FROM classes";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            classList.clear();
            while (resultSet.next()) {
                String className = resultSet.getString("class_name");
                classList.add(className);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while loading classes.");
        }
    }

    @FXML
    private void handleAddClass() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Class");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Class Name (e.g., BSCSMS1Y2):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(className -> {
            if (className.isEmpty() || !className.matches("^[A-Za-z0-9]+$")) {
                showAlert("Input Error", "Please enter a valid class name.");
                return;
            }

            String insertQuery = "INSERT INTO classes (class_name) VALUES (?)";
            try (Connection connection = DatabaseUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(insertQuery)) {

                statement.setString(1, className);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    classList.add(className);
                    showAlert("Success", "Class added successfully.");
                } else {
                    showAlert("Failure", "Failed to add class.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while adding class.");
            }
        });
    }

    @FXML
    private void handleReturnToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/admin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnToMainMenuButton.getScene().getWindow();
            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                showAlert("Error", "Stage not found.");
            }
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
