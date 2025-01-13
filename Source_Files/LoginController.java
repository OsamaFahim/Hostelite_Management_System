import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/HMS";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Studyroom@123";


    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button LoginButton;

    @FXML
    private String selectedRole;

    @FXML
    private static int user_id;

    @FXML
    private void initialize() {

    }

    @FXML
    public void setSelectedRole(String role) {
        selectedRole = role;
    }    

    public static void getPersonIdByUsername(String username) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT person_id FROM person WHERE username = ?";
            
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        user_id = resultSet.getInt("person_id");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginButton() {
    try {
            // Get values from the fields
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Validate email, username, and other fields
            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Please fill in all fields");
                return;
            }

            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                // Check if username and password are valid
                String loginQuery = "SELECT * FROM person WHERE username = ? AND password = ?";
                try (PreparedStatement loginStatement = connection.prepareStatement(loginQuery)) {
                    // Set values for the prepared statement
                    loginStatement.setString(1, username);
                    loginStatement.setString(2, password);
            
                    // Execute the query
                    try (ResultSet resultSet = loginStatement.executeQuery()) {
                        if (resultSet.next()) {
                            // Valid username and password
                            String userType = resultSet.getString("type");
                            if (userType.equals("Hostellite") && selectedRole.equals("Hostelite")){
                                getPersonIdByUsername((username));
                                loadHosteliteMenu();
                            }
                            else if (userType.equals("Admin") && selectedRole.equals("Admin")){
                                getPersonIdByUsername((username));
                                loadAdminMenu();
                            }
                            else
                                showAlert("Invalid Role Selected", AlertType.WARNING);

                        } else {
                            // Invalid username or password
                            showAlert("Invalid username or password. Please try again.", AlertType.WARNING);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("An error occurred. Please try again.", AlertType.ERROR);
            }
        }  catch (Exception e) {
                e.printStackTrace();
                showAlert("An error occurred. Please try again.", AlertType.ERROR);
        }
    }

    private void showAlert(String message) {
        showAlert(message, AlertType.ERROR);
    }

    private void showAlert(String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #0598ff;");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: white;");
        alert.show();
    }

    private void loadAdminMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminMenu.fxml"));
            Parent root = loader.load();


            AdminMenuController adminMenuController = loader.getController();
            adminMenuController.receiveUserId(user_id);
            adminMenuController.selectedRole = selectedRole;

            // Set up the stage
            Stage stage = new Stage();
            stage.setTitle("Admin Menu");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setResizable(false);
            ((Stage) LoginButton.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHosteliteMenu() {
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HosteliteMenu.fxml"));
            Parent root = loader.load();


            HosteliteMenuController HosteliteMenuController = loader.getController();
            HosteliteMenuController.receiveUserId(user_id);
            HosteliteMenuController.selectedRole = selectedRole;

            // Set up the stage
            Stage stage = new Stage();
            stage.setTitle("Hostelite Menu");
            stage.setScene(new Scene(root));
            stage.show();
             stage.setResizable(false);
            ((Stage) LoginButton.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void CLOSEBUTTON(ActionEvent event) {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
    
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginorRegister.fxml"));
            Parent root = loader.load();

             // Pass the selected role to the controller
             LoginorRegisterController LoginorRegister = loader.getController();
             LoginorRegister.setSelectedRole(selectedRole);

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Login or Register!");
            currentStage.setResizable(false);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
