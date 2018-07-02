
import controller.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author alber
 */
public class ClientApp extends Application{

    public static void main(String[] args){
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        ViewFactory viewFactory = ViewFactory.defaultFactory;

        primaryStage.setTitle("Client Window");
        primaryStage.setScene(viewFactory.getMainScene());
        primaryStage.show();
        
    }
    
}
