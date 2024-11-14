package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Add this import
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageModulesController {

    @FXML private ComboBox<String> academicYearComboBox;
    @FXML private ComboBox<String> semesterComboBox;
    @FXML private ComboBox<String> classComboBox;
    @FXML private ListView<String> moduleListView;
    @FXML private TextField newModuleTextField;
    @FXML private Button addModuleButton;
    @FXML private Button saveButton;
    @FXML private Button returnToDashboardButton;

    private ObservableList<String> academicYearList = FXCollections.observableArrayList();
    private ObservableList<String> semesterList = FXCollections.observableArrayList();
    private ObservableList<String> classList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadAcademicYears();
        loadSemesters();
        loadClasses();
    }

    private void loadAcademicYears() {
        String sql = "SELECT DISTINCT academic_year FROM academic_year ORDER BY academic_year";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                academicYearList.add(rs.getString("academic_year"));
            }

            academicYearComboBox.setItems(academicYearList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load academic years.");
        }
    }

    private void loadSemesters() {
        String sql = "SELECT DISTINCT semester_name FROM semesters ORDER BY semester_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                semesterList.add(rs.getString("semester_name"));
            }

            semesterComboBox.setItems(semesterList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load semesters.");
        }
    }

    private void loadClasses() {
        String sql = "SELECT DISTINCT class_name FROM classes ORDER BY class_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                classList.add(rs.getString("class_name"));
            }

            classComboBox.setItems(classList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load class names.");
        }
    }

    @FXML
    private void handleAddModule() {
        String academicYear = academicYearComboBox.getValue();
        String semester = semesterComboBox.getValue();
        String className = classComboBox.getValue();
        String moduleName = newModuleTextField.getText().trim();

        if (academicYear == null || semester == null || className == null || moduleName.isEmpty()) {
            showAlert("Error", "Please select all fields and enter a module name.");
            return;
        }

        // Insert the new module into the database
        String sql = "INSERT INTO modules (academic_year, semester, class_name, module_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set parameters for the prepared statement
            stmt.setString(1, academicYear);
            stmt.setString(2, semester);
            stmt.setString(3, className);
            stmt.setString(4, moduleName);

            // Execute the insert
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                showAlert("Success", "Module added successfully.");
                // Optionally, update ListView with the new module
                moduleListView.getItems().add(moduleName);
            } else {
                showAlert("Error", "Failed to add the module.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while adding the module.");
        }
    }

    @FXML
    private void handleSaveModule() {
        // Implement logic for saving the current module data
        // This can be useful if you want to update or finalize the changes made to a module.

        String academicYear = academicYearComboBox.getValue();
        String semester = semesterComboBox.getValue();
        String className = classComboBox.getValue();
        String moduleName = newModuleTextField.getText().trim();

        if (academicYear == null || semester == null || className == null || moduleName.isEmpty()) {
            showAlert("Error", "Please ensure all fields are selected before saving.");
            return;
        }

        // Here, you can add a method to save or update the module details in the database.
        String sql = "UPDATE modules SET academic_year = ?, semester = ?, class_name = ?, module_name = ? WHERE module_name = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, academicYear);
            stmt.setString(2, semester);
            stmt.setString(3, className);
            stmt.setString(4, moduleName);
            stmt.setString(5, moduleName); // Assuming we want to update the module by its name

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                showAlert("Success", "Module saved successfully.");
            } else {
                showAlert("Error", "Failed to save the module.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while saving the module.");
        }
    }

    @FXML
    private void handleReturnToDashboard() {
        // Logic to navigate back to the main menu/dashboard
        // Typically, you would use FXMLLoader to load the main menu FXML

        try {
            // Example of switching scenes back to dashboard (replace with your specific logic):
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/admin.fxml"));
            Parent root = loader.load();
            Scene dashboardScene = new Scene(root);

            // Assuming you have access to the current Stage (window)
            Stage currentStage = (Stage) returnToDashboardButton.getScene().getWindow();
            currentStage.setScene(dashboardScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while returning to the dashboard.");
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
