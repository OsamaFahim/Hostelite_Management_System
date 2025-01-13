import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminMenuController {

    @FXML
    private int userId;

    @FXML
    String selectedRole;

    @FXML
    public void receiveUserId(int id) {
        userId = id;
    }

    // GRASP: Controller
    @FXML
    public void ListHostelButton(ActionEvent event) {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ListHostel.fxml"));
            Parent root = loader.load();

            ListHostelController ListHostelController = loader.getController();
            ListHostelController.receiveUserId(userId);

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("List Hostels");
            currentStage.setResizable(false);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GRASP: Controller
    @FXML
    void handleFeedbacklButton(ActionEvent event) {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewFeedback.fxml"));
            Parent root = loader.load();

            ViewFeebackController ViewFeebackController = loader.getController();
            ViewFeebackController.receiveUserId(userId);

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Feedbacks!");
            currentStage.setResizable(false);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GRASP: Controller
    @FXML
    void handleRoomButton(ActionEvent event) {
         Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RoomAdmin.fxml"));
            Parent root = loader.load();

            RoomAdminController RoomAdmin = loader.getController();
            RoomAdmin.receiveUserId(userId);

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Manage Room Services!");
            currentStage.setResizable(false);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GRASP: Controller
    @FXML 
    void handlePaymentButton(ActionEvent event){
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BillConfirmation.fxml"));
            Parent root = loader.load();

            BillConfirmationController BillConfirmation = loader.getController();
            BillConfirmation.receiveUserId(userId);

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Confirm Payments");
            currentStage.setResizable(false);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void CLOSEBUTTON(ActionEvent event) {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
    
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
            Parent root = loader.load();

             // Pass the selected role to the controller
             LoginController LoginController = loader.getController();
             LoginController.setSelectedRole(selectedRole);

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Login!");
            currentStage.setResizable(false);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
