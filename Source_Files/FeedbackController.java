import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class FeedbackController {

    @FXML
    private int userId;

    @FXML
    private TextArea CommentBox;

    @FXML
    private RadioButton FoodMain_Excellent;
    @FXML
    private RadioButton FoodMain_Poor;
    @FXML
    private RadioButton FoodMain_Satisfactory;

    @FXML
    private RadioButton FoodQual_Excellent;
    @FXML
    private RadioButton FoodQual_Poor;
    @FXML
    private RadioButton FoodQual_Satisfactory;

    @FXML
    private RadioButton RoomMain_Excellent;
    @FXML
    private RadioButton RoomMain_Poor;
    @FXML
    private RadioButton RoomMain_Satisfactory;

    @FXML
    private RadioButton RoomQual_Excellent;
    @FXML
    private RadioButton RoomQual_Poor;
    @FXML
    private RadioButton RoomQual_Satisfactory;

    @FXML
    private Button SubmitButton;

    private ToggleGroup foodQualityToggleGroup;
    private ToggleGroup foodMaintenanceToggleGroup;
    private ToggleGroup roomQualityToggleGroup;
    private ToggleGroup roomMaintenanceToggleGroup;

    @FXML
    public void receiveUserId(int id) {
        userId = id;
    }

    @FXML
    void initialize() {
        // Initialize ToggleGroups
        foodQualityToggleGroup = new ToggleGroup();
        FoodQual_Excellent.setToggleGroup(foodQualityToggleGroup);
        FoodQual_Satisfactory.setToggleGroup(foodQualityToggleGroup);
        FoodQual_Poor.setToggleGroup(foodQualityToggleGroup);

        foodMaintenanceToggleGroup = new ToggleGroup();
        FoodMain_Excellent.setToggleGroup(foodMaintenanceToggleGroup);
        FoodMain_Satisfactory.setToggleGroup(foodMaintenanceToggleGroup);
        FoodMain_Poor.setToggleGroup(foodMaintenanceToggleGroup);

        roomQualityToggleGroup = new ToggleGroup();
        RoomQual_Excellent.setToggleGroup(roomQualityToggleGroup);
        RoomQual_Satisfactory.setToggleGroup(roomQualityToggleGroup);
        RoomQual_Poor.setToggleGroup(roomQualityToggleGroup);

        roomMaintenanceToggleGroup = new ToggleGroup();
        RoomMain_Excellent.setToggleGroup(roomMaintenanceToggleGroup);
        RoomMain_Satisfactory.setToggleGroup(roomMaintenanceToggleGroup);
        RoomMain_Poor.setToggleGroup(roomMaintenanceToggleGroup);
    }

    @FXML
    void handleSubmitButton(ActionEvent event) {
        if (isAnyToggleGroupEmpty()) {
            // Show an alert if any group is empty
            showAlert("Feedback Error", "Please provide feedback for all categories.",1);
        } else {
            // Retrieve selected values
            String foodQuality = getSelectedRadioButtonValue(foodQualityToggleGroup);
            String foodMaintenance = getSelectedRadioButtonValue(foodMaintenanceToggleGroup);
            String roomQuality = getSelectedRadioButtonValue(roomQualityToggleGroup);
            String roomMaintenance = getSelectedRadioButtonValue(roomMaintenanceToggleGroup);
            String comments = CommentBox.getText();

            // Check if the user has already provided feedback
            boolean feedbackExists = checkFeedbackExists(userId);

            //Get hostel ID
            int hostelId = getHostelIdForHostelite(userId);

            // Perform the appropriate database operation
            if (feedbackExists) {
                // Update existing feedback
                updateFeedback(userId, hostelId, foodQuality, foodMaintenance, roomQuality, roomMaintenance, comments);
                showAlert("Feedback Updated", "Your feedback has been updated successfully.",2);
            } else {
                // Insert new feedback
                if (hostelId != -1) {
                    insertFeedback(userId, hostelId, foodQuality, foodMaintenance, roomQuality, roomMaintenance, comments);
                    showAlert("Feedback Submitted", "Thank you for your feedback!",2);
                } else {
                    System.out.println("Failed to retrieve hostel_id for the hostelite.");
                }
            }
        }
    }

    private void updateFeedback(int userId, int hostelId, String foodQuality, String foodMaintenance,
        String roomQuality, String roomMaintenance, String comments) {
        String updateQuery = "UPDATE Feedback SET food_quality = ?, food_maintenance = ?, " +
                "room_quality = ?, room_maintenance = ?, comments = ? WHERE hostelite_id = ? AND hostel_id = ?";
        executeFeedbackQuery_Update(updateQuery, userId, hostelId, foodQuality, foodMaintenance, roomQuality, roomMaintenance, comments);
    }   

    private void insertFeedback(int userId, int hostelId, String foodQuality, String foodMaintenance,
                                String roomQuality, String roomMaintenance, String comments) {
        String insertQuery = "INSERT INTO Feedback (hostelite_id, hostel_id, food_quality, food_maintenance, " +
                "room_quality, room_maintenance, comments) VALUES (?, ?, ?, ?, ?, ?, ?)";
        executeFeedbackQuery_Insert(insertQuery, userId, hostelId, foodQuality, foodMaintenance, roomQuality, roomMaintenance, comments);
    }

    private int getHostelIdForHostelite(int hosteliteId) {
        String query = "SELECT hostel_id FROM hostel_hostelites WHERE Hostelite_ID = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS",
                "root", "Studyroom@123");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, hosteliteId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("hostel_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; 
    }

     private void executeFeedbackQuery_Insert(String query, int userId, int hostelId, String foodQuality, String foodMaintenance,
                                  String roomQuality, String roomMaintenance, String comments) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS",
                "root", "Studyroom@123");
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, hostelId);
            preparedStatement.setString(3, foodQuality);
            preparedStatement.setString(4, foodMaintenance);
            preparedStatement.setString(5, roomQuality);
            preparedStatement.setString(6, roomMaintenance);
            preparedStatement.setString(7, comments);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while saving feedback. Please try again.", 1);
        }
    }

    private void executeFeedbackQuery_Update(String query, int userId, int hostelId, String foodQuality, String foodMaintenance,
                                  String roomQuality, String roomMaintenance, String comments) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS",
                "root", "Studyroom@123");
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, foodQuality);
            preparedStatement.setString(2, foodMaintenance);
            preparedStatement.setString(3, roomQuality);
            preparedStatement.setString(4, roomMaintenance);
            preparedStatement.setString(5, comments);
            preparedStatement.setInt(6, userId);
            preparedStatement.setInt(7, hostelId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while saving feedback. Please try again.", 1);
        }
    }


    private boolean isAnyToggleGroupEmpty() {
        return foodQualityToggleGroup.getSelectedToggle() == null ||
                foodMaintenanceToggleGroup.getSelectedToggle() == null ||
                roomQualityToggleGroup.getSelectedToggle() == null ||
                roomMaintenanceToggleGroup.getSelectedToggle() == null;
    }

     private boolean checkFeedbackExists(int userId) {
        String query = "SELECT * FROM Feedback WHERE hostelite_id = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS",
                                                                 "root", "Studyroom@123");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if feedback exists for the user
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Assume no feedback exists in case of an exception
        }
    }

    private String getSelectedRadioButtonValue(ToggleGroup toggleGroup) {
        RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            return selectedRadioButton.getText();
        }
        return null;
    }

    private void showAlert(String title, String content, int type) {
        Alert alert = null;
        if(type == 1)
            alert = new Alert(Alert.AlertType.ERROR);
        else if(type == 2)
            alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getButtonTypes().removeIf(buttonType -> buttonType == ButtonType.CANCEL);
        alert.getDialogPane().setStyle("-fx-background-color: #0598ff;");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: white;");
        alert.showAndWait();
    }

      @FXML
    private void CLOSEBUTTON(ActionEvent event) {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
    
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HosteliteMenu.fxml"));
            Parent root = loader.load();

             // Pass the selected role to the controller
             HosteliteMenuController HosteliteMenuController = loader.getController();
             HosteliteMenuController.receiveUserId(userId);
             HosteliteMenuController.selectedRole = "Hostelite";

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Hostelite Menu");
            currentStage.setResizable(false);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
