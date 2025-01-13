import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SelectActorController {

    @FXML
    private Button HosteliteButton;

    @FXML
    private Button AdminButton;

    @FXML
    private void initialize() {
        // Set up any initialization code here
        if (HosteliteButton != null) {
            HosteliteButton.setOnAction(event -> handleHosteliteButton());
        }
        if (AdminButton != null) {
            AdminButton.setOnAction(event -> handleAdminButton());
        }
    }

    private void openLoginOrRegisterPage(String selectedRole) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginorRegister.fxml"));
            Parent root = loader.load();

            // Pass the selected role to the controller
            LoginorRegisterController loginController = loader.getController();
            loginController.setSelectedRole(selectedRole);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setTitle("Login or Register!");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            ((Stage) HosteliteButton.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

    @FXML
    private void handleHosteliteButton() {
        openLoginOrRegisterPage("Hostelite");
    }

    @FXML
    private void handleAdminButton() {
        openLoginOrRegisterPage("Admin");
    }
}
