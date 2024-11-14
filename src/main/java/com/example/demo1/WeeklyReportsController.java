package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.sql.*;

public class WeeklyReportsController {

    @FXML
    private TableView<Report> reportsTableView;
    @FXML
    private TableColumn<Report, String> weekColumn;
    @FXML
    private TableColumn<Report, String> lecturerColumn;
    @FXML
    private TableColumn<Report, String> reportColumn;
    @FXML
    private TextField reportBackTextField;
    @FXML
    private Button sendReportButton;
    @FXML
    private Button returnButton;

    private Connection connection;

    private ObservableList<Report> reportsList = FXCollections.observableArrayList();

    // Initialize method to set up the columns and fetch reports from the database
    public void initialize() {
        // Map the columns to the Report class properties
        weekColumn.setCellValueFactory(new PropertyValueFactory<>("submissionDate"));
        lecturerColumn.setCellValueFactory(new PropertyValueFactory<>("lecturer"));
        reportColumn.setCellValueFactory(new PropertyValueFactory<>("report"));

        // Establish the database connection and load reports
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "041024");
            loadReportsFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while connecting to the database.");
        }
    }

    // Load reports from the database
    private void loadReportsFromDatabase() {
        String query = "SELECT submission_date, username AS lecturer, challenges AS report FROM lecturer_reports"; // Updated query
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String week = resultSet.getString("submission_date"); // Fetch submission_date as week
                String lecturer = resultSet.getString("lecturer"); // Fetch username as lecturer
                String report = resultSet.getString("report"); // Fetch challenges as report
                reportsList.add(new Report(week, lecturer, report));
            }
            reportsTableView.setItems(reportsList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load reports from the database.");
        }
    }

    // Handle the action for sending feedback to the lecturers
    @FXML
    private void handleSendReport(ActionEvent event) {
        String feedback = reportBackTextField.getText().trim();
        if (feedback.isEmpty()) {
            showAlert("Input Error", "Please enter feedback before sending.");
            return;
        }

        // Get the selected report from the table view
        Report selectedReport = reportsTableView.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            showAlert("Selection Error", "Please select a report to send feedback.");
            return;
        }

        // Insert the feedback into the response table
        String insertQuery = "INSERT INTO response (lecturer_username, response_text, submission_date) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Set parameters for the query
            preparedStatement.setString(1, selectedReport.getLecturer());
            preparedStatement.setString(2, feedback);
            preparedStatement.setString(3, selectedReport.getSubmissionDate());

            // Execute the update query
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Feedback Sent", "Your feedback has been sent to the lecturers.");
                reportBackTextField.clear();  // Clear the text field after sending
            } else {
                showAlert("Error", "Failed to send feedback.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while saving the response.");
        }
    }

    // Handle the return action to go back to the main menu
    @FXML
    private void handleReturn(ActionEvent event) {
        try {
            Stage stage = (Stage) returnButton.getScene().getWindow();
            Scene mainMenuScene = new Scene(new javafx.fxml.FXMLLoader(getClass().getResource("principal_lecturer.fxml")).load());
            stage.setScene(mainMenuScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "An error occurred while returning to the main menu.");
        }
    }

    // Method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
