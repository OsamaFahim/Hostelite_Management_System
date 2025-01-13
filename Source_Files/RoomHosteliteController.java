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
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RoomHosteliteController {

    @FXML
    private int userId;

    @FXML
    public void receiveUserId(int id) {
        userId = id;
        initialize();
    }

    @FXML
    private Button RequestButton;

    @FXML
    private Text PromptText1;

    @FXML
    private Text PromptText2;


    @FXML
    private Text statusText;

    public void initialize() {
        int status = getStatus();

        if (status == 0) {
            RequestButton.setVisible(false);
            statusText.setFill(Color.web("#0598ff"));
            statusText.setText("Your request has been forwarded to the admin");

            PromptText1.setVisible(false);
            PromptText2.setVisible(false);
        }
        else if(status == 1){
            RequestButton.setVisible(true);
            RequestButton.setText("Confirm");

            statusText.setFill(Color.web("#0598ff"));
            statusText.setText("A team has been sent on its way for Room Service");

            PromptText1.setVisible(true);
            PromptText2.setText("    Press Button Below");
            PromptText2.setVisible(true);
            PromptText1.setText("       To confirm Room Service");
        }
    }

    @FXML
    void handleRequestButton(ActionEvent event) {
        int hostel_id = getHostelId();

         if (!isEntryExists(userId, hostel_id)) {
            insertRoomServiceEntry(userId, hostel_id, 0);

            RequestButton.setVisible(false);
            statusText.setFill(Color.web("#0598ff"));
            statusText.setText("Your request has been forwarded to the admin");
            
            PromptText1.setVisible(false);
            PromptText2.setVisible(false);
        }
        else{
            int status = getStatus();
            if (status == 1) {
                deleteRoomServiceEntry(userId, hostel_id);
    
                RequestButton.setVisible(true);
                RequestButton.setText("Request");
                
                statusText.setFill(Color.web("#0598ff"));
                statusText.setText("");
    
                PromptText2.setVisible(true);
                PromptText1.setVisible(true);

                PromptText2.setText("Press Button Below To");
                PromptText2.setFill(Color.web("#0598ff"));

                PromptText1.setText("Request Weekly Room Service");
                PromptText1.setFill(Color.web("#0598ff"));

            }
        }
    }

    private void deleteRoomServiceEntry(int userId, int hostelId) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123");
    
            String sql = "DELETE FROM Room_Service WHERE hostelite_id = ? AND hostel_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, hostelId);
    
                preparedStatement.executeUpdate();
            }
    
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private int getStatus() {
        int status = -1; // Default value if something goes wrong

        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123");

            // SQL query to get the status for the hostelite ID provided
            String sql = "SELECT room_service.status FROM room_service WHERE room_service.hostelite_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        status = resultSet.getInt("status");
                    }
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    @FXML
    int getHostelId() {
        int hostelId = -1; // Default value if something goes wrong

        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123");

            // SQL query to get hostel id for the username provided to it
            String sql = "SELECT hostel_hostelites.Hostel_id FROM hostel_hostelites WHERE hostel_hostelites.Hostelite_ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        hostelId = resultSet.getInt("Hostel_id");
                    }
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hostelId;
    }

    //If room service is already underway 
    private boolean isEntryExists(int userId, int hostelId) {
        boolean entryExists = false;

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123");

            String sql = "SELECT * FROM Room_Service WHERE hostelite_id = ? AND hostel_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, hostelId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    entryExists = resultSet.next();
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entryExists;
    }

    //Insert the room service to the table if the room service is requested by the hostelite
    private void insertRoomServiceEntry(int userId, int hostelId, int status) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123");

            String sql = "INSERT INTO Room_Service (hostelite_id, hostel_id, status) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, hostelId);
                preparedStatement.setInt(3, status);

                preparedStatement.executeUpdate();
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
