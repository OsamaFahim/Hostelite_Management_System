import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.beans.property.SimpleIntegerProperty;

//Information Expert
public class RoomAdminController {
    private int userId;

    @FXML
    private Text RequestsText;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/HMS";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Studyroom@123";

    @FXML
    private ComboBox<String> cityComboBox;

    @FXML
    private ComboBox<String> hostelComboBox;

    @FXML
    private TableView<RoomServiceInfo> requestsTable;
    
    @FXML
    private TableColumn<RoomServiceInfo, String> roomServiceIdColumn;
    
    @FXML
    private TableColumn<RoomServiceInfo, String> hostelNameColumn;
    
    @FXML
    private TableColumn<RoomServiceInfo, String> statusColumn;

     @FXML
    private TableColumn<RoomServiceInfo, String> HosteliteID_Column;

    @FXML
    private Button SendButton;
    
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
            Populate_Requests(selectedHostel);
        }
        else
            requestsTable.getItems().clear();
    }

    private void Populate_Requests(String hostelName) {
        String query = "SELECT room_service_id, Hostel_Name, person_id, status FROM room_service " +
                       "JOIN hostel ON hostel.Hostel_id = room_service.hostel_id " +
                       "JOIN person ON person.person_id = room_service.hostelite_id " +
                       "WHERE Hostel_Name = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, hostelName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ObservableList<RoomServiceInfo> requestsList = FXCollections.observableArrayList();
    
                while (resultSet.next()) {
                    // Extract room service information from the result set
                    int roomServiceId = resultSet.getInt("room_service_id");
                    String retrievedHostelName = resultSet.getString("Hostel_Name");
                    int hosteliteId = resultSet.getInt("person_id"); 
                    int status = resultSet.getInt("status");
    
                    requestsList.add(new RoomServiceInfo(roomServiceId, retrievedHostelName, hosteliteId, status));
                }
    
                // Clear previous columns
                requestsTable.getColumns().clear();
    
                // Create and set up columns
                TableColumn<RoomServiceInfo, Integer> roomServiceIdColumn = new TableColumn<>("Room Service ID");
                roomServiceIdColumn.setCellValueFactory(cellData -> cellData.getValue().roomServiceIdProperty().asObject());
    
                TableColumn<RoomServiceInfo, String> hostelNameColumn = new TableColumn<>("Hostel Name");
                hostelNameColumn.setCellValueFactory(cellData -> cellData.getValue().hostelNameProperty());
    
                TableColumn<RoomServiceInfo, Integer> HosteliteID_Column = new TableColumn<>("Hostelite ID");
                HosteliteID_Column.setCellValueFactory(cellData -> cellData.getValue().Hostel_ID_Property().asObject()); 

                TableColumn<RoomServiceInfo, String> statusColumn = new TableColumn<>("Room Service Status");
                statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getStatusString(cellData.getValue().getStatus())));
                
                // Add columns to the table
                requestsTable.getColumns().addAll(roomServiceIdColumn, hostelNameColumn, HosteliteID_Column, statusColumn);
    
                // Populate the TableView
                requestsTable.setItems(requestsList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            RequestsText.setText("Error fetching room service information: " + e.getMessage());
        }
    }
    
    private String getStatusString(int status) {
        if(status == 0){
            return "Request Recieved";
        }
        else if(status == 1){
            return "Team Sent";
        }

        return "";
    }

    @FXML
    private void SendTeamButton(ActionEvent event) {
        RoomServiceInfo selectedService = requestsTable.getSelectionModel().getSelectedItem();
        if (selectedService != null) {
             int status = selectedService.getStatus();
             if(status == 0){
                // Execute the query to get hostel_id
                int hostelId = getHostelId(selectedService.getHostelName());
                
                SendTeam(hostelId);

                // Calling the function again to refresh the page after update
                Populate_Requests(selectedService.getHostelName());
            }
            else
                showAlert("Team Sent", "Team is already underway for this service");
        }
        else {
            showAlert("Please Select", "Please Select a Service to send a Team");
        }
    }

    @FXML
    private void onTableRowClicked(MouseEvent event) {
        if (event.getClickCount() == 1) { // Check for a single click
            RoomServiceInfo selectedService = requestsTable.getSelectionModel().getSelectedItem();
            if (selectedService != null) {
                int status = selectedService.getStatus();
                if(status == 1)
                    SendButton.setVisible(false);
                else
                    SendButton.setVisible(true);
            }
            else
                SendButton.setVisible(true);
        }
        else
            SendButton.setVisible(true);
    }

    private void SendTeam(int hostelId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            RoomServiceInfo selectedService = requestsTable.getSelectionModel().getSelectedItem();
            int hosteliteId = selectedService.get_HosteliteID();
    
            String query = "UPDATE room_service SET status = 1 WHERE hostel_id = ? AND hostelite_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, hostelId);
                preparedStatement.setInt(2, hosteliteId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to send team: " + e.getMessage());
        }
    }
    
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
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #0598ff;");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: white;");
        alert.showAndWait();
    }

    public class RoomServiceInfo {
        private SimpleIntegerProperty roomServiceId;
        private SimpleStringProperty hostelName;
        private SimpleIntegerProperty status;
        private SimpleIntegerProperty Hostelite_ID;
    
        public RoomServiceInfo(int roomServiceId, String hostelName, int Hostelite_ID, int status) {
            this.roomServiceId = new SimpleIntegerProperty(roomServiceId);
            this.hostelName = new SimpleStringProperty(hostelName);
            this.Hostelite_ID = new SimpleIntegerProperty(Hostelite_ID);
            this.status = new SimpleIntegerProperty(status);
        }
        
        public int getRoomServiceId() {
            return roomServiceId.get();
        }
    
        public SimpleIntegerProperty roomServiceIdProperty() {
            return roomServiceId;
        }
    
        public String getHostelName() {
            return hostelName.get();
        }
        
        public SimpleStringProperty hostelNameProperty() {
            return hostelName;
        }
    
        public int getStatus() {
            return status.get();
        }

        public int get_HosteliteID() {
            return Hostelite_ID.get();
        }
    
        public SimpleIntegerProperty statusProperty() {
            return status;
        }

         public SimpleIntegerProperty HosteliteIDProperty() {
            return Hostelite_ID;
        }

        public SimpleIntegerProperty Hostel_ID_Property() {
            return Hostelite_ID;
        }
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
