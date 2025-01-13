import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Information Expert
public class ViewFeebackController {

    private int userId;

    @FXML
    private Text feedbackText;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/HMS";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Studyroom@123";

    @FXML
    private ComboBox<String> cityComboBox;

    @FXML
    private ComboBox<String> hostelComboBox;

    @FXML
    private TableView<FeedbackInfo> feedbackTable;

    @FXML
    private TableColumn<FeedbackInfo, String> foodQualityColumn;

    @FXML
    private TableColumn<FeedbackInfo, String> foodMaintenanceColumn;

    @FXML
    private TableColumn<FeedbackInfo, String> roomQualityColumn;

    @FXML
    private TableColumn<FeedbackInfo, String> roomMaintenanceColumn;

    @FXML
    private TableColumn<FeedbackInfo, String> commentsColumn;

    public void receiveUserId(int id) {
        userId = id;
        initializeCityComboBox();
    }

    private void initializeCityComboBox() {
        // your query and populate the cityComboBox
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT DISTINCT hostel.City FROM hostel JOIN hostel_owners ON hostel.Hostel_id = hostel_owners.Hostel_ID WHERE hostel_owners.Admin_ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        cityComboBox.getItems().add(resultSet.getString("City"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCitySelected() {
        // Handle city selection
        String selectedCity = cityComboBox.getValue();
        if (selectedCity != null && !selectedCity.isEmpty()) {
            // Update hostelComboBox based on the selected city
            updateHostelComboBox(selectedCity);
        }
    }

    private void updateHostelComboBox(String selectedCity) {
        hostelComboBox.getItems().clear(); // Clear previous items

        String query = "SELECT DISTINCT Hostel_Name FROM hostel " +
                "JOIN hostel_owners ON hostel.Hostel_id = hostel_owners.Hostel_ID " +
                "WHERE hostel_owners.Admin_ID = ? AND hostel.City = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId); // userId is available in the controller
            preparedStatement.setString(2, selectedCity);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String hostelName = resultSet.getString("Hostel_Name");
                    hostelComboBox.getItems().add(hostelName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onHostelSelected() {
        String selectedHostel = hostelComboBox.getValue();
        if (selectedHostel != null && !selectedHostel.isEmpty()) {
            displayFeedbackInfo(selectedHostel);
        }
    }

    private void displayFeedbackInfo(String hostelName) {
        String query = "SELECT food_quality, food_maintenance, room_quality, room_maintenance, comments FROM feedback " +
                "JOIN hostel ON hostel.Hostel_id = feedback.hostel_id " +
                "WHERE Hostel_Name = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, hostelName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ObservableList<FeedbackInfo> feedbackList = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    // Extract feedback information from the result set
                    String foodQuality = resultSet.getString("food_quality");
                    String foodMaintenance = resultSet.getString("food_maintenance");
                    String roomQuality = resultSet.getString("room_quality");
                    String roomMaintenance = resultSet.getString("room_maintenance");
                    String comments = resultSet.getString("comments");

                    feedbackList.add(new FeedbackInfo(foodQuality, foodMaintenance, roomQuality, roomMaintenance, comments));
                }

                // Clear previous columns
                feedbackTable.getColumns().clear();

                // Create and set up columns
                TableColumn<FeedbackInfo, String> foodQualityCol = new TableColumn<>("Food Quality");
                foodQualityCol.setCellValueFactory(cellData -> cellData.getValue().foodQualityProperty());

                TableColumn<FeedbackInfo, String> foodMaintenanceCol = new TableColumn<>("Food Maintenance");
                foodMaintenanceCol.setCellValueFactory(cellData -> cellData.getValue().foodMaintenanceProperty());

                TableColumn<FeedbackInfo, String> roomQualityCol = new TableColumn<>("Room Quality");
                roomQualityCol.setCellValueFactory(cellData -> cellData.getValue().roomQualityProperty());

                TableColumn<FeedbackInfo, String> roomMaintenanceCol = new TableColumn<>("Room Maintenance");
                roomMaintenanceCol.setCellValueFactory(cellData -> cellData.getValue().roomMaintenanceProperty());

                TableColumn<FeedbackInfo, String> commentsCol = new TableColumn<>("Comments");
                commentsCol.setCellValueFactory(cellData -> cellData.getValue().commentsProperty());

                // Add columns to the table
                feedbackTable.getColumns().addAll(foodQualityCol, foodMaintenanceCol, roomQualityCol, roomMaintenanceCol, commentsCol);


                // Populate the TableView
                feedbackTable.setItems(feedbackList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            feedbackText.setText("Error fetching feedback information: " + e.getMessage());
        }
    }

    public static class FeedbackInfo {
        private final StringProperty foodQuality;
        private final StringProperty foodMaintenance;
        private final StringProperty roomQuality;
        private final StringProperty roomMaintenance;
        private final StringProperty comments;

        public FeedbackInfo(String foodQuality, String foodMaintenance, String roomQuality, String roomMaintenance, String comments) {
            this.foodQuality = new SimpleStringProperty(foodQuality);
            this.foodMaintenance = new SimpleStringProperty(foodMaintenance);
            this.roomQuality = new SimpleStringProperty(roomQuality);
            this.roomMaintenance = new SimpleStringProperty(roomMaintenance);
            this.comments = new SimpleStringProperty(comments);
        }

        public StringProperty foodQualityProperty() {
            return foodQuality;
        }

        public StringProperty foodMaintenanceProperty() {
            return foodMaintenance;
        }

        public StringProperty roomQualityProperty() {
            return roomQuality;
        }

        public StringProperty roomMaintenanceProperty() {
            return roomMaintenance;
        }

        public StringProperty commentsProperty() {
            return comments;
        }

        // Getter methods
        public String getFoodQuality() {
            return foodQuality.get();
        }

        public String getFoodMaintenance() {
            return foodMaintenance.get();
        }

        public String getRoomQuality() {
            return roomQuality.get();
        }

        public String getRoomMaintenance() {
            return roomMaintenance.get();
        }

        public String getComments() {
            return comments.get();
        }
    }

     @FXML
    private void CLOSEBUTTON(ActionEvent event) {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
    
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminMenu.fxml"));
            Parent root = loader.load();

            // GRASP: Controller
             // Pass the selected role to the controller
             AdminMenuController AdminMenu = loader.getController();
             AdminMenu.selectedRole = "Admin";
             AdminMenu.receiveUserId(userId);

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Feedbacks");
            currentStage.setResizable(false);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
    
