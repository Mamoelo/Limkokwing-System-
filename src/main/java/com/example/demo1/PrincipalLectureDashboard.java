package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class PrincipalLectureDashboard {

    @FXML
    private Button reportSubmissionButton; // Added field for report submission button
    @FXML
    private Button weeklyReportsButton; // Button for weekly reports
    @FXML
    private Button dataRestrictionButton; // Updated name
    @FXML
    private Button profileButton; // Button for profile
    @FXML
    private Button logoutButton; // Button for logout

    // Generalized navigation method
    private void goToPage(String page) {
        try {
            AnchorPane newPage = FXMLLoader.load(getClass().getResource(page));
            Stage stage = (Stage) reportSubmissionButton.getScene().getWindow(); // Changed to a valid button
            stage.setScene(new Scene(newPage));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReportSubmissionAction() {
        goToPage("reportSubmission.fxml"); // Navigate to report submission page
    }

    @FXML
    private void handleWeeklyReportsAction() {
        goToPage("weeklyReports.fxml"); // Navigate to weekly reports page
    }

    @FXML
    private void handleDataRestrictionAction() {
        goToPage("dataRestriction.fxml"); // Navigate to data restriction page
    }

    @FXML
    private void handleProfileAction() {
        goToPage("profile.fxml"); // Navigate to profile page
    }

    @FXML
    private void handleLoginAction() {
        goToPage("login.fxml"); // Navigate back to login page
    }
}
