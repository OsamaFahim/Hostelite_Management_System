import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ExpenseTrackingController {

    @FXML
    private TextField budgetTextField;

    @FXML
    private TextField daysTextField;

    @FXML
    private Button generateButton;

    @FXML
    private int userId;

    @FXML
    private Text resultText;

    @FXML
    private Text EmojiText;

    @FXML
    public void receiveUserId(int id) {
        userId = id;
    }

    @FXML
    void handleGenerateButton(ActionEvent event) {
        // Validate budget
        try {
            double budget = Double.parseDouble(budgetTextField.getText());
            if (budget % 1 != 0) {
                showAlert("Invalid Budget", "Budget must be an integer or a double.");
                return;
            }
            else if(budget == 0){
                showAlert("Unfortunate Budget", "You cannot generate your budget as you have nothing!.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Budget", "Enter a valid number for the budget.");
            return;
        }

        // Validate days
        try {
            int days = Integer.parseInt(daysTextField.getText());
            if (days <= 0) {
                showAlert("Invalid Days", "Number of days must be a positive integer.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Days", "Enter a valid number for the days.");
            return;
        }

        //If the days and budget entered are valid
        double budget = Double.parseDouble(budgetTextField.getText());
        int days = Integer.parseInt(daysTextField.getText());

        double budgetPerDay = budget / days;

        resultText.setText(String.format("RS %.2f (per day)", budgetPerDay));
        resultText.setFill(javafx.scene.paint.Color.valueOf("#0598ff"));

        displayMessageBasedOnBudget(budgetPerDay);
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

    private void displayMessageBasedOnBudget(double budgetPerDay) {
        if (budgetPerDay > 1000) {
            // Smiling emoji and message
            EmojiText.setText(String.format("😎 Easily Manageable"));
        } else if (budgetPerDay > 500) {
            // Moderate emoji and message
            EmojiText.setText(String.format("    😊  Manageable"));
        } else {
            // Sad emoji and message
            EmojiText.setText(String.format("😢 Hardly Manageable"));
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
