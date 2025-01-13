    import java.io.IOException;
    import javafx.application.Application;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;  
    import javafx.stage.Stage;
    
    public class App extends Application {
        @Override
        public void start(Stage primaryStage) {
            
            //GRASP : Controller
            Parent root;
            try{
                root = FXMLLoader.load(getClass().getResource("SelectActor.fxml"));
                Scene scene = new Scene(root);
            
                primaryStage.setTitle("Choose Your Journey!");
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.show();
            } catch(IOException e) {e.printStackTrace();}
    }
        
        public static void main(String[] args) {
            launch(args);
        }
    }
