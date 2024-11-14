package com.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import com.example.demo1.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerManagementController {

    @FXML
    private ListView<CheckBox> lecturerListView;  // ListView to display checkboxes
    @FXML
    private Button addLecturerButton;
    @FXML
    private Button returnToMainMenuButton;
    @FXML
    private Button deleteLecturerButton;

    private List<String> selectedUsers = new ArrayList<>();

    public void loadUsersList() {
        lecturerListView.getItems().clear();

        try (Connection connection = DatabaseUtil.getConnection()) {

            String query = "SELECT user_id, username FROM users WHERE role IN ('Lecturer', 'Principal Lecturer')";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String userId = resultSet.getString("user_id");
                String username = resultSet.getString("username");


                CheckBox checkBox = new CheckBox(username);
                checkBox.setUserData(userId);


                checkBox.setOnAction(event -> {
                    if (checkBox.isSelected()) {
                        selectedUsers.add(userId);
                    } else {
                        selectedUsers.remove(userId);
                    }
                });

                lecturerListView.getItems().add(checkBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "An error occurred while fetching users.");
        }
    }

    @FXML
    private void handleAddLecturer() {
        if (selectedUsers.isEmpty()) {
            showErrorDialog("Selection Error", "Please select at least one user to add as a lecturer.");
            return;
        }

        addLecturersToDatabase(selectedUsers);  // Add selected users to the lecturers table
    }

    @FXML
    private void handleDeleteLecturer() {
        if (selectedUsers.isEmpty()) {
            showErrorDialog("Selection Error", "Please select at least one user to delete.");
            return;
        }

        deleteLecturersFromDatabase(selectedUsers);  // Delete selected users from the lecturers table
    }

    @FXML
    private void handleReturnToMainMenu() {
        loadPage("/com/example/demo1/admin.fxml");
    }

    private void addLecturersToDatabase(List<String> userIds) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String insertQuery = "INSERT INTO lecturers (user_id) VALUES (?)";

            for (String userId : userIds) {

                PreparedStatement statement = connection.prepareStatement(insertQuery);
                statement.setInt(1, Integer.parseInt(userId));  // Parse userId to integer
                statement.executeUpdate();
            }

            showInfoDialog("Lecturer Added", "Selected users have been added as lecturers.");
            loadUsersList();

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "An error occurred while adding users to the lecturers table.");
        }
    }

    private void deleteLecturersFromDatabase(List<String> userIds) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String deleteQuery = "DELETE FROM lecturers WHERE user_id = CAST(? AS INTEGER)";  // Use CAST if needed

            for (String userId : userIds) {
                PreparedStatement statement = connection.prepareStatement(deleteQuery);
                statement.setString(1, userId);
                statement.executeUpdate();
            }

            showInfoDialog("Lecturer Deleted", "Selected users have been removed from lecturers.");
            loadUsersList();

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "An error occurred while deleting users from the lecturers table.");
        }
    }

    private void loadPage(String fxmlFilePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) returnToMainMenuButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error", "Could not load the page.");
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        loadUsersList();
    }
}
