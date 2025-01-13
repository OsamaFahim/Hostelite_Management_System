import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;

//Information Expert
public class BillAutomationController {

    @FXML
    private Text billDetailsText;

    @FXML
    private Text HeadingsText;

    @FXML
    private Text RoomsText;
    
    @FXML
    private Text RoomsBill;

    @FXML
    private Text statusText;

    @FXML
    private Text FoodHeadingsText;

    @FXML
    private Button PaymentButton;

    private int userId;

    public void receiveUserId(int id) {
        userId = id;
        displayBillDetails(userId);
        initialize();
    }

    public void initialize() {
        int status = getStatus();

        if (status == 0) {
            PaymentButton.setVisible(false);
            statusText.setFill(Color.web("#0598ff"));
            statusText.setText("Your Transaction is underway");
        }
        else if(status == 1){
            PaymentButton.setVisible(false);
            statusText.setFill(Color.web("#0598ff"));
            statusText.setText("Your Transcation Has been confirmed and you have\n been checked in");
        }
    }

    private void displayBillDetails(int userId) {
        String url = "jdbc:mysql://localhost:3306/HMS";
        String user = "root";
        String password = "Studyroom@123";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT breakfast, lunch, dinner, meal_price, room_price FROM hostel " +
                    "JOIN hostel_hostelites ON hostel.hostel_id = hostel_hostelites.hostel_id " +
                    "WHERE hostel_hostelites.hostelite_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        boolean breakfast = resultSet.getBoolean("breakfast");
                        boolean lunch = resultSet.getBoolean("lunch");
                        boolean dinner = resultSet.getBoolean("dinner");
                        double mealPrice = resultSet.getDouble("meal_price");
                        double roomPrice = resultSet.getDouble("room_price");
                        double PerdayMealPrice = 0;
                        if(breakfast) PerdayMealPrice += mealPrice;
                        if(lunch) PerdayMealPrice += mealPrice;
                        if(dinner) PerdayMealPrice += mealPrice;
                        double Monthly_Meal_Price = PerdayMealPrice * YearMonth.now().lengthOfMonth();

                        String displayText = String.format("Breakfast: %s\nLunch: %s\nDinner: %s\n\n" +
                        "Meal Price: RS %.2f\n" +
                        "Food Price Per Day : %s\n                   = %.2f\nMonthly Food Price : RS %.2f",
                        (breakfast ? "✔" : "✘"),
                        (lunch ? "✔" : "✘"),
                        (dinner ? "✔" : "✘"),
                        mealPrice,
                        "breakfast" + (lunch ? " + lunch" : "") + (dinner ? " + dinner" : ""),
                        PerdayMealPrice,
                        Monthly_Meal_Price);
                
                        String mealsPriniing = "Food Bill : ";

                        HeadingsText.setText(mealsPriniing);
                        HeadingsText.setFill(javafx.scene.paint.Color.valueOf("#0598ff"));

                        billDetailsText.setText(displayText);
                        billDetailsText.setFill(javafx.scene.paint.Color.valueOf("#0598ff"));

                        RoomsText.setText("Room Bill : ");
                        RoomsText.setFill(javafx.scene.paint.Color.valueOf("#0598ff"));
                        double monthlyRoomPrice = roomPrice * 4;

                        String rooms = String.format("Weekly Room Service Price: RS %.2f\nMonthly Room Service Price: RS %.2f", roomPrice, monthlyRoomPrice);

                        RoomsBill.setText(rooms);
                        RoomsText.setFill(javafx.scene.paint.Color.valueOf("#0598ff"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    @FXML
    private int getStatus() {
        int status = -1; // Default value if something goes wrong

        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123");

            // SQL query to get the status for the hostelite ID provided
            String sql = "SELECT payment.status FROM payment WHERE Payment.hostelite_id = ?";
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


     //If room service is already underway 
     private boolean isEntryExists(int userId, int hostelId) {
        boolean entryExists = false;

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123");

            String sql = "SELECT * FROM Payment WHERE hostelite_id = ? AND hostel_id = ?";
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

    @FXML
    private void handlePaymentClick(ActionEvent event) {
         int hostel_id = getHostelId();

         if (!isEntryExists(userId, hostel_id)) {
            insertPaymentEntry(userId, hostel_id, 0);

            PaymentButton.setVisible(false);
            statusText.setFill(Color.web("#0598ff"));
            statusText.setText("Your Transaction is underway");
        }
    }

        //Insert the room service to the table if the room service is requested by the hostelite
        private void insertPaymentEntry(int userId, int hostelId, int status) {
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HMS", "root", "Studyroom@123");
    
                String sql = "INSERT INTO Payment (hostelite_id, hostel_id, status) VALUES (?, ?, ?)";
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
