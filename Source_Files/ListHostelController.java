import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ListHostelController {

    @FXML
    private TextField HostelName;

    @FXML
    private TextField cityfield;

    @FXML
    private TextField numRoomsField;

    @FXML
    private TextField roomservicesField;

    @FXML
    private TextField mealPriceField;

    @FXML
    private CheckBox breakfastCheckbox;

    @FXML
    private CheckBox lunchCheckbox;

    @FXML
    private CheckBox dinnerCheckbox;

    @FXML
    private Button registerButton;

    private int userId;

    public void receiveUserId(int id) {
        userId = id;
    }

    @FXML
    private void handleRegisterButton() {
        if (validateFields()) {
            insertHostelData();
        } else {
            showAlert("Invalid Input", "Please enter valid data in all fields.");
        }
    }

    private boolean validateFields() {
        try {
            // GRASP: Information Expert (validating fields)
            Integer.parseInt(numRoomsField.getText());
            Double.parseDouble(roomservicesField.getText());
            Double.parseDouble(mealPriceField.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void insertHostelData() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123");
    
            // Check if the hostel with the same name already exists
            if (hostelExists(connection, HostelName.getText())) {
                showAlert("Error", "Hostel with the same name already exists. Please choose a different name.");
                return;
            }
    
            String insertHostelQuery = "INSERT INTO Hostel (Hostel_Name, City, Rooms, Room_Price, breakfast, Lunch, Dinner, Meal_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
            PreparedStatement preparedStatement = connection.prepareStatement(insertHostelQuery);
            preparedStatement.setString(1, HostelName.getText());
            preparedStatement.setString(2, cityfield.getText());
            preparedStatement.setInt(3, Integer.parseInt(numRoomsField.getText()));
            preparedStatement.setDouble(4, Double.parseDouble(roomservicesField.getText()));
            preparedStatement.setBoolean(5, breakfastCheckbox.isSelected());
            preparedStatement.setBoolean(6, lunchCheckbox.isSelected());
            preparedStatement.setBoolean(7, dinnerCheckbox.isSelected());
            preparedStatement.setDouble(8, Double.parseDouble(mealPriceField.getText()));
    
            int rowsAffected = preparedStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                // If insertion is successful, insert into Hostel_Owners table
                insertHostelOwnersData(connection);
            } else {
                showAlert("Error", "Failed to register hostel. Please try again.");
            }
    
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred. Please try again.");
        }
    }
    
    private boolean hostelExists(Connection connection, String hostelName) throws SQLException {
        String checkHostelQuery = "SELECT * FROM Hostel WHERE Hostel_Name = ?";
        PreparedStatement checkStatement = connection.prepareStatement(checkHostelQuery);
        checkStatement.setString(1, hostelName);
        return checkStatement.executeQuery().next();
    }
    private void insertHostelOwnersData(Connection connection) throws SQLException {
        String insertHostelOwnersQuery = "INSERT INTO Hostel_Owners (Hostel_id, Admin_ID) VALUES (LAST_INSERT_ID(), ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertHostelOwnersQuery);
        preparedStatement.setInt(1, userId);

        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected <= 0) {
            showAlert("Error", "Failed to register hostel owner. Please try again.");
        }
        else{
            preparedStatement.close();
            refreshPage();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.getDialogPane().setStyle("-fx-background-color: #0598ff;");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: white;");
        alert.showAndWait();
    }

    private void refreshPage() {
        // Reload the current FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ListHostel.fxml"));
        Parent root;
        // Pass the selected role to the controller
        try {
            // GRASP: Low Coupling (using FXML to separate UI and controller)
            root = loader.load();
            Scene scene = new Scene(root);

            // Set the controller for the current stage
            Stage stage = (Stage) registerButton.getScene().getWindow();
            ListHostelController ListHostelController = loader.getController();
            ListHostelController.receiveUserId(userId);
            ListHostelController.clearFields(); // Assuming you have a method to clear fields
            stage.setTitle("Register!");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void clearFields() {
       HostelName.clear();
       cityfield.clear();
       numRoomsField.clear();
       roomservicesField.clear();
       mealPriceField.clear();
       breakfastCheckbox.setSelected(false);
       lunchCheckbox.setSelected(false);
       dinnerCheckbox.setSelected(false);
    }

    @FXML
    private void CLOSEBUTTON(ActionEvent event) {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
    
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminMenu.fxml"));
            Parent root = loader.load();

             // Pass the selected role to the controller
             AdminMenuController AdminMenu = loader.getController();
             AdminMenu.selectedRole = "Admin";
             AdminMenu.receiveUserId(userId);

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Login!");
            currentStage.setResizable(false);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
