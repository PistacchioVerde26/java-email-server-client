
package controller;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.EmailMessageBean;

/**
 *
 * @author alber
 * 
 * ViewFactory si occupa di instanziare i controller con la corrispondente view
 * 
 * Inoltre crea un oggetto di tipo ModelAccess, in modo da passare la stessa istanza
 * a tutti i controller
 * 
 */
public class ViewFactory {
    
    //Necessario per passare la stessa instanza del modelAccess tra i controllori
    public static ViewFactory defaultFactory = new ViewFactory();
    
    private final String DEFAULT_CSS = "/view/style.css";
    private final String MAIN_SCREEN_FXML = "/view/MainLayout.fxml";
    private final String EMAIL_DETAILS_FXML = "/view/EmailDetailsLayout.fxml";
    private final String COMPOSE_SCREEN_FXML = "/view/ComposeMessageLayout.fxml";
    
    //E' usato dai controller per passarsi informazioni a vicenda
    private final ModelAccess modelAccess = new ModelAccess();
    
    /**
     * 
     * @return 
     * 
     * Inizializza la view principale con il corrispondente controller
     * 
     */
    public Scene getMainScene(){
        AbstractController mainController = new MainController(modelAccess);
        return initializeScene(MAIN_SCREEN_FXML, mainController);
    }
    
    /**
     * 
     * @return 
     * 
     * Inizializza la view  con il corrispondente controller per mostrare i dettagli di un email
     */
    public Scene getEmailDetailsScene(){
        AbstractController emailDetailsController = new EmailDetailsController(modelAccess);
        return initializeScene(EMAIL_DETAILS_FXML, emailDetailsController);
    }
    
    /**
     * 
     * @return 
     * 
     * Inizializza la view con il corrispondente controller per comporre un nuovo messaggio
     */
    public Scene getComposeMessageScene(){
        AbstractController composeController = new ComposeMessageController(modelAccess);
        return initializeScene(COMPOSE_SCREEN_FXML, composeController);
    }
    
     /**
     * 
     * @param message
     * @param answerAll
     * @return 
     * 
     * Inizializza la view con il corrispondente controller per comporre un messaggio in risposta ad un altro
     */
    public Scene getReplyMessageScene(EmailMessageBean message, boolean answerAll){
        AbstractController composeController = new ComposeMessageController(modelAccess, message, answerAll);
        return initializeScene(COMPOSE_SCREEN_FXML, composeController);
    }
    
    /**
     * 
     * @param treeItemValue
     * @return 
     * 
     * Ritorna una ImageView contente un icona.
     * L'icona viene scelta in base alla stringa passata come parametro
     * 
     */
    public Node resolveIcon(String treeItemValue){
        
        String lowerCaseTreeItemValue = treeItemValue.toLowerCase();
        String path;
        if(lowerCaseTreeItemValue.contains("inbox")){ 
            path = "/view/images/inbox.png";
        }else if(lowerCaseTreeItemValue.contains("outbox")){
            path = "/view/images/outbox.png";
        }else if(lowerCaseTreeItemValue.contains("outgoing")){
            path = "/view/images/outgoing.png";
        }else if(lowerCaseTreeItemValue.contains("trash")){
            path = "/view/images/trash.png";
        }else if(lowerCaseTreeItemValue.contains("@")){
            path = "/view/images/emailbox.png";
        }else{
            path = "/view/images/spam.png";
        }
        
        return new ImageView(new Image(getClass().getResourceAsStream(path)));
    }
    
    /**
     * 
     * @param fxmlPath
     * @param controller
     * @return 
     * 
     * Responsabile di inizialiazzare una nuova scena insieme al suo controller
     * in base ai dati passati come parametro
     */
    private Scene initializeScene(String fxmlPath, AbstractController controller){
        FXMLLoader loader;
        Parent parent;
        Scene scene;
        
        try{
            loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(controller);
            parent = loader.load();
        }catch(IOException e){
            //e.printStackTrace();
            return null;
        }
        
        scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource(DEFAULT_CSS).toExternalForm());
        return scene;
    }
    
}
