import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ReserveHostelController implements Initializable {

    @FXML
    private int userId;

    @FXML
    private ComboBox<String> cityComboBox;

    @FXML
    private TableView<HostelInfo> hostelTable;

    @FXML
    private TableColumn<HostelInfo, String> nameColumn;

    @FXML
    private TableColumn<HostelInfo, Integer> roomsColumn;

    @FXML
    private TableColumn<HostelInfo, Double> roomPriceColumn;

    @FXML
    private TableColumn<HostelInfo, Boolean> breakfastColumn;

    @FXML
    private TableColumn<HostelInfo, Boolean> lunchColumn;

    @FXML
    private TableColumn<HostelInfo, Boolean> dinnerColumn;

    @FXML
    private TableColumn<HostelInfo, Double> mealPriceColumn;

    private ObservableList<HostelInfo> hostelData = FXCollections.observableArrayList();

    @FXML
    private Button ReserveHostelButton;

    @FXML
    public void receiveUserId(int id) {
        userId = id;
    }

    @FXML
    private void onTableRowClicked(MouseEvent event) {
        if (event.getClickCount() == 1) { // Check for a single click
            HostelInfo selectedHostel = hostelTable.getSelectionModel().getSelectedItem();
            if (selectedHostel != null) {
            }
        }
    }

    // Function to get hostel_id based on Hostel_Name
    private int getHostelId(String hostelName) {
        int hostelId = 0;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123")) {
            String query = "SELECT hostel_id FROM Hostel WHERE Hostel_Name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, hostelName);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        hostelId = resultSet.getInt("hostel_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hostelId;
    }
    
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
    private void ReserveHostelButtonClicked(ActionEvent event) {
        HostelInfo selectedHostel = hostelTable.getSelectionModel().getSelectedItem();
        if (selectedHostel != null && !isUserRegisteredToHostel()) {
            // Execute the query to get hostel_id
            int hostelId = getHostelId(selectedHostel.getName());
    
            // Insert into hostel_hostelites table
            int userId = getUserId(); 
            insertIntoHostelHostelites(hostelId, userId);
        }
        else
            showAlert("Registration Error", "You cannot be registered to more than one hostel at a time.");
    }
    
    // Function to insert into hostel_hostelites table
    private void insertIntoHostelHostelites(int hostelId, int userId) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123")) {
            String query = "INSERT INTO hostel_hostelites (hostel_id, Hostelite_ID) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, hostelId);
                preparedStatement.setInt(2, userId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Example method to get the user ID (replace with your actual implementation)
    private int getUserId() {
        return userId; // Replace with your actual logic to get the user ID
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize and populate the ComboBox with distinct cities
        populateCityComboBox();
    
        // Configure the table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        roomsColumn.setCellValueFactory(new PropertyValueFactory<>("rooms"));
        roomPriceColumn.setCellValueFactory(new PropertyValueFactory<>("roomPrice"));
        breakfastColumn.setCellValueFactory(new PropertyValueFactory<>("breakfast"));
        lunchColumn.setCellValueFactory(new PropertyValueFactory<>("lunch"));
        dinnerColumn.setCellValueFactory(new PropertyValueFactory<>("dinner"));
        mealPriceColumn.setCellValueFactory(new PropertyValueFactory<>("mealPrice"));
    
        // Set hostelData as the data source for the table
        hostelTable.setItems(hostelData);
    
        // Initially hide the button
        ReserveHostelButton.setVisible(false);
    
        // Add a listener to the selection model of the table
        hostelTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Show the button when there's a selection in the table
                ReserveHostelButton.setVisible(true);
            } else {
                // Hide the button when there's no selection
                ReserveHostelButton.setVisible(false);
            }
        });
    
        // Add an event listener for the ComboBox selection change
        cityComboBox.setOnAction(event -> {
            // When a city is selected, update the table with hostel information
            updateHostelTable(cityComboBox.getValue());
        });
    }    

    private void populateCityComboBox() {
        // Use your database connection code to fetch distinct cities
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123")) {
            String query = "SELECT DISTINCT City FROM Hostel";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                // Clear existing items
                cityComboBox.getItems().clear();

                // Add cities to the ComboBox
                while (resultSet.next()) {
                    String city = resultSet.getString("City");
                    cityComboBox.getItems().add(city);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add an event listener for the ComboBox selection change
        cityComboBox.setOnAction(event -> {
            // When a city is selected, update the table with hostel information
            updateHostelTable(cityComboBox.getValue());
        });
    }

    private void updateHostelTable(String selectedCity) {
        // Use your database connection code to fetch hostel information for the selected city
        hostelData.clear(); // Clear existing data

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123")) {
            String query = "SELECT hostel_name, rooms, room_price, breakfast, lunch, dinner, meal_price FROM Hostel WHERE City = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, selectedCity);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String name = resultSet.getString("hostel_name");
                        int rooms = resultSet.getInt("rooms");
                        double roomPrice = resultSet.getDouble("room_price");
                        boolean breakfast = resultSet.getBoolean("breakfast");
                        boolean lunch = resultSet.getBoolean("lunch");
                        boolean dinner = resultSet.getBoolean("dinner");
                        double mealPrice = resultSet.getDouble("meal_price");

                        hostelData.add(new HostelInfo(name, rooms, roomPrice, breakfast, lunch, dinner, mealPrice));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // HostelInfo class to represent each row in the table
    public static class HostelInfo {
        private String name;
        private int rooms;
        private double roomPrice;
        private boolean breakfast;
        private boolean lunch;
        private boolean dinner;
        private double mealPrice;

        public HostelInfo(String name, int rooms, double roomPrice, boolean breakfast, boolean lunch, boolean dinner, double mealPrice) {
            this.name = name;
            this.rooms = rooms;
            this.roomPrice = roomPrice;
            this.breakfast = breakfast;
            this.lunch = lunch;
            this.dinner = dinner;
            this.mealPrice = mealPrice;
        }

        // Add getters for each property
        public String getName() {
            return name;
        }

        public int getRooms() {
            return rooms;
        }

        public double getRoomPrice() {
            return roomPrice;
        }

        public boolean isBreakfast() {
            return breakfast;
        }

        public boolean isLunch() {
            return lunch;
        }

        public boolean isDinner() {
            return dinner;
        }

        public double getMealPrice() {
            return mealPrice;
        }
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