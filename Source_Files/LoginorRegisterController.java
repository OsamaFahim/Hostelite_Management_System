import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginorRegisterController {

    @FXML
    private Button LoginButton;

    @FXML
    private Button RegisterButton;

    private String selectedRole;

    public void setSelectedRole(String role) {
        selectedRole = role;
    }

    @FXML
    private void initialize() {
        // Set up any initialization code here
        if (RegisterButton != null) {
            RegisterButton.setOnAction(event -> handleRegisterButton());
        }

        if (LoginButton != null) {
            LoginButton.setOnAction(event -> handleLoginButton());
        }
    }

    @FXML
    private void openRegisterPage(String selectedRole) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterPage.fxml"));
            Parent root = loader.load();

            // Pass the selected role to the controller
            RegisterController registerController = loader.getController();
            registerController.setSelectedRole(selectedRole);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setTitle("Register!");
            stage.setScene(scene);
            stage.show();
            ((Stage) RegisterButton.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

    @FXML
    private void openLoginPage(String selectedRole) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
            Parent root = loader.load();

            // Pass the selected role to the controller
            LoginController LoginController = loader.getController();
            LoginController.setSelectedRole(selectedRole);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setTitle("Login!");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            ((Stage) LoginButton.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

    @FXML
    private void handleRegisterButton() {
        openRegisterPage(selectedRole);
    }

    @FXML
    private void handleLoginButton() {
        openLoginPage(selectedRole);
    }
    
    @FXML
    private void CLOSEBUTTON(ActionEvent event) {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
    
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectActor.fxml"));
            Parent root = loader.load();

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Choose Your Journey!");
            currentStage.setResizable(false);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}


