import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HosteliteMenuController {
    @FXML
    private int userId;

     @FXML
    private Button ReserveHostelButton;

    @FXML
    String selectedRole;

    @FXML
    public void receiveUserId(int id) {
        userId = id;
    }

    // GRASP: Information Expert
    private boolean isUserRegisteredToHostel() {
        // Perform a database query to check if the user is already registered to a hostel
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123")) {
            String query = "SELECT * FROM hostel_hostelites WHERE Hostelite_ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // If a row is found, the user is already registered
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false in case of an exception
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #0598ff;");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: white;");
        alert.showAndWait();
    }

    @FXML
    void ReseveHostelButton(ActionEvent event) {
        // Check if the user is already registered to a hostel
        if (isUserRegisteredToHostel()) {
            // User is already registered, show an alert
            showAlert("Registration Error", "You cannot be registered to more than one hostel at a time.");
        } else {
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReserveHostel.fxml"));
                Parent root = loader.load();

                ReserveHostelController reserveHostelController = loader.getController();
                reserveHostelController.receiveUserId(userId);

                currentStage.setScene(new Scene(root));
                currentStage.setTitle("Reserve A Hostel!");
                currentStage.setResizable(false);
                currentStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void HandleFeedbackButton(ActionEvent event){
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

        //Check if the user is already registered to a hostel
        if(isUserRegisteredToHostel()){
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FeedBack.fxml"));
                    Parent root = loader.load();

                    FeedbackController FeedbackController = loader.getController();
                    FeedbackController.receiveUserId(userId);

                    currentStage.setScene(new Scene(root));
                    currentStage.setTitle("Feedback Form");
                    currentStage.setResizable(false);
                    currentStage.show();

                } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            showAlert("Feedback Error", "You must be registered to a hostel.");

        }
    }
    
    @FXML
    void HandleExpenseButton(ActionEvent event) {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

         try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ExpenseTracking.fxml"));
                Parent root = loader.load();

                ExpenseTrackingController ExpenseTrackingController = loader.getController();
                ExpenseTrackingController.receiveUserId(userId);

                currentStage.setScene(new Scene(root));
                currentStage.setTitle("Manage Your Expenses!");
                currentStage.setResizable(false);
                currentStage.show();

                } catch (IOException e) {
                e.printStackTrace();
            }   
    }

    @FXML
    void HandleBillAutomationButton(ActionEvent event) {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

        //Check if the user is already registered to a hostel
        if(isUserRegisteredToHostel()){
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("BillAutomation.fxml"));
                    Parent root = loader.load();

                    BillAutomationController BillAutomationController = loader.getController();
                    BillAutomationController.receiveUserId(userId);

                    currentStage.setScene(new Scene(root));
                    currentStage.setTitle("Bill Automation");
                    currentStage.setResizable(false);
                    currentStage.show();

                } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            showAlert("Bill Automation Error", "You must be registered to a hostel.");
        }
    }

    @FXML
    void HandleRoomServiceButton(ActionEvent event) {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

         //Check if the user is already registered to a hostel
        if(isUserRegisteredToHostel()){
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("RoomHostelite.fxml"));
                    Parent root = loader.load();

                    RoomHosteliteController RoomHosteliteController = loader.getController();
                    RoomHosteliteController.receiveUserId(userId);

                    currentStage.setScene(new Scene(root));
                    currentStage.setTitle("Room Service");
                    currentStage.setResizable(false);
                    currentStage.show();

                } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            showAlert("Room Service Error", "You must be registered to a hostel.");
        }
    }

    @FXML
    private void CLOSEBUTTON(ActionEvent event) {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
    
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
            Parent root = loader.load();

            // Pass the selected role to the controller
            LoginController LoginController = loader.getController();
            LoginController.setSelectedRole(selectedRole);

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Login!");
            currentStage.setResizable(false);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
