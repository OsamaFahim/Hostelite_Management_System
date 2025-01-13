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
import java.sql.SQLException;

public class RegisterController {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/HMS";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Studyroom@123";

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private String selectedRole;

    @FXML
    public void setSelectedRole(String role) {
        selectedRole = role;
    }    

    @FXML
    private void handleRegisterButton() {
    try {
            // Get values from the fields
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Validate email, username, and other fields
            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Please fill in all fields");
                return;
            }


            // Validate email format
            if (!isValidEmail(email)) {
                showAlert("Please enter a valid email address.");
                return;
            }

            if(isEmailTaken(email)) {
                showAlert("This email is already taken.");
                return;
            }
            //Validate Password Length
            if(password.length() < 8){
                showAlert("Password must have atleast 8 characters.");
                return;
            }

            // Validate password match
            if (!password.equals(confirmPassword)) {
                showAlert("Passwords do not match.");
                return;
            }

            // Check if the username is already taken
            if (isUsernameTaken(username)) {
                showAlert("The username is already taken. Please try a different username");
                return;
            }

            // Insert data into the person table with type = "Hostellite"
             // GRASP: Creator (creating a PreparedStatement)
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                String insertQuery = "INSERT INTO person (email, username, password, type) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, email);
                    preparedStatement.setString(2, username);
                    preparedStatement.setString(3, password);

                    // GRASP: Polymorphism (handling different roles)
                    if (selectedRole.equals("Hostelite")) 
                        preparedStatement.setString(4, "Hostellite");
                    else if (selectedRole.equals("Admin")) 
                        preparedStatement.setString(4, "Admin");

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert("Registration successful", AlertType.INFORMATION);

                        // Clear fields
                        clearFields();

                        // Refresh the page
                        refreshPage();
                    } else {
                        showAlert("Registration failed");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("An error occurred. Please try again.");
            }

        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage());
        }
    }

    private void refreshPage() {
        // Reload the current FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterPage.fxml"));
        Parent root;
        // Pass the selected role to the controller
        try {
            root = loader.load();
            Scene scene = new Scene(root);

            // Set the controller for the current stage
            Stage stage = (Stage) registerButton.getScene().getWindow();
            RegisterController registerController = loader.getController();
            registerController.setSelectedRole(selectedRole);
            registerController.clearFields(); 
            stage.setTitle("Register!");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }   

    private void showAlert(String message) {
        showAlert(message, AlertType.ERROR);
    }

    private boolean isValidEmail(String email) {
        // For example, using the specified regular expression
        String emailRegex = "^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,4}$";
        return email.matches(emailRegex);
    }

    private void showAlert(String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #0598ff;");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: white;");
        alert.show();
    }

    private void clearFields() {
        emailField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private boolean isUsernameTaken(String username) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM person WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // Returns true if the username already exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Assume the username is taken in case of an exception
        }
    }

    private boolean isEmailTaken(String email) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM person WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // Returns true if the email already exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Assume the email is taken in case of an exception
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
