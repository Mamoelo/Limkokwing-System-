package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogbookController {

    @FXML private TableView<LoginHistory> logbookTable;
    @FXML private TableColumn<LoginHistory, String> usernameColumn;
    @FXML private TableColumn<LoginHistory, String> loginDateColumn;
    @FXML private Button returnToMainMenuButton;

    private ObservableList<LoginHistory> logbookData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        loginDateColumn.setCellValueFactory(new PropertyValueFactory<>("loginDate"));


        loadLoginHistory();
    }

    private void loadLoginHistory() {
        String sql = "SELECT username, login_date FROM login_history ORDER BY login_date DESC";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String loginDate = resultSet.getTimestamp("login_date").toString();
                logbookData.add(new LoginHistory(username, loginDate));
            }
            logbookTable.setItems(logbookData);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load login history.");
        }
    }

    @FXML
    private void handleReturnToMainMenu() {
        try {

            if (returnToMainMenuButton == null) {
                showAlert("Error", "Return to Main Menu button is not initialized.");
                return;
            }

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
