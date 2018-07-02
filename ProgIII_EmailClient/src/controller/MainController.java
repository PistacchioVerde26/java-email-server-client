
package controller;

import controller.services.DeleteMessageService;
import controller.services.SetEmailReadService;
import controller.services.MessageRendererService;
import model.EmailMessageBean;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import model.EmailAccountBean;
import model.folder.EmailFolderBean;
import model.table.BoldableRowFactory;
import model.table.FormattableInteger;

/**
 *
 * @author alber
 * 
 * Controller principale dell'intera applicazine
 * 
 */
public class MainController extends AbstractController implements Initializable{
    
    private EmailAccountsController emailAccounts;
    
    @FXML
    private TreeView<String> emailFolderdsTreeView; //L'oggetto root deve essere un TreeItem
    private MenuItem showDetails = new MenuItem("Mostra dettagli");
    
    @FXML
    private TableView<EmailMessageBean> emailTable;
    
    @FXML
    private TableColumn<EmailMessageBean, String> subjectCol;
    
    @FXML
    private TableColumn<EmailMessageBean, String> senderCol;
    
    @FXML
    private TableColumn<EmailMessageBean, String> dateCol;
    
    @FXML
    private TableColumn<EmailMessageBean, FormattableInteger> sizeCol;

    @FXML
    private WebView messageRenderer;
    private MessageRendererService messageRendererService;

    @FXML
    private Button btnRemoveEmail;
    
    public MainController(ModelAccess modelAccess) {
        super(modelAccess);
    }
    
    @FXML
    void composeBtnAction(ActionEvent event) {
        Stage stage = new Stage();
        stage.setScene(ViewFactory.defaultFactory.getComposeMessageScene());
        stage.show();
    }
    
    @FXML
    void addNewAccountAction(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Nuovo indirizzo email");
        dialog.setHeaderText("Aggiungi account email");
        dialog.setContentText("Indirizzo email: ");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newEmailAddress -> emailAccounts.addNewAccount(newEmailAddress));
        
    }
    
    @FXML
    void removeAccount(ActionEvent event){
        ChoiceDialog<String> dialog = new ChoiceDialog<>("...", getModelAccess().getEmailAccountNames());
        dialog.setTitle("RImuovi account");
        dialog.setHeaderText("Rimuovi account");
        dialog.setContentText("Seleziona account: ");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(accountToRemove -> {
            emailAccounts.removeAccount(accountToRemove);
            getModelAccess().getEmailAccountNames().remove(accountToRemove);
            
        });
    }
    
    @FXML
    void removeEmail(ActionEvent event) { 
        System.out.println("Removing");
        btnRemoveEmail.disableProperty().set(true);
        EmailMessageBean messageToDelete = getModelAccess().getSelectedMessage();
        EmailFolderBean<String> folderBean = getModelAccess().getSelectedFolder();
        EmailAccountBean messageOwner = getModelAccess().getEmailAccountByName(messageToDelete.getMessageReference().getOwnerEmail());
        
        DeleteMessageService deleteMessage = new DeleteMessageService(messageToDelete, folderBean, messageOwner);
        deleteMessage.start();
        
        getModelAccess().setSelectedMessage(null);      
    }
    
    void setReadAction() {
        EmailMessageBean messageBean = getModelAccess().getSelectedMessage();
        if(messageBean != null){
            boolean value = messageBean.isRead();
            if(!value){
                SetEmailReadService flagReadService = new SetEmailReadService(getModelAccess().getEmailAccountByName(messageBean.getMessageReference().getOwnerEmail()),
                        messageBean.getMessageReference().getMsgID());
                flagReadService.start();
                flagReadService.setOnSucceeded(e -> {
                    messageBean.setRead(true);//Questo causerà l'update definito in BoldableRowFactory
                    EmailFolderBean<String> selectedFolder = getModelAccess().getSelectedFolder();
                    if(selectedFolder != null){
                        if(value){
                            selectedFolder.incrementUnreadMessagesCount(1);
                        }else{
                            selectedFolder.decrementUnreadMessagesCount();
                        }
                    }
                });
            }
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        ViewFactory viewFactory = ViewFactory.defaultFactory;
        
        messageRendererService = new MessageRendererService(messageRenderer.getEngine());
        btnRemoveEmail.disableProperty().set(true);
        
        /**
         * @setRowFactory sovrascrive la classe padre TableRow con la figlia BoldableRowFactory
         * @BoldableRowFactory estende TableRow e fa l'override di updateItem che cambia lo stile css della cella selezionata
         * Il costruttore di BoldableRowFactory ogni volta che cambia l'elemento contenuto nella riga gli aggiunge un listener
         * sulla proprietà isRead, che verrà triggerato ogni volta che cambia
         */
        emailTable.setRowFactory(e -> new BoldableRowFactory<>());
        
        //Associo alla cellValueFactory di ogni colonna il valore delle
        //SimpleProperty del EmailMessageBean che contiene la rispettiva row
        //Essendo le SimpleProperty osservabili questo causerà l'aggiornamento
        //Della vista in caso di cambiamento dei dati
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        senderCol.setCellValueFactory(new PropertyValueFactory<>("sender"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
	sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        
        //BUG: non sovrascrive autamicamente il comparatore della colonna, bisogna farlo manualmente
        //passandogli un FormattableInteger (che è un comparator) con valore 0 come metro di misura
        sizeCol.setComparator(new FormattableInteger(0));
        
        /**
         * TREE VIEW
         */
        EmailFolderBean<String> root = new EmailFolderBean<>("");//ROOT PRINCIPALE
        emailFolderdsTreeView.setRoot(root);
        //Per poter visulazziare più account di posta nascondo la root
        emailFolderdsTreeView.setShowRoot(false);
        
        emailAccounts = new EmailAccountsController(root, getModelAccess());
        emailAccounts.start();
        
        emailTable.setContextMenu(new ContextMenu(showDetails));
        
        /**
         * 
         */
        emailFolderdsTreeView.setOnMouseClicked(e ->{
            EmailFolderBean<String> item = (EmailFolderBean<String>)emailFolderdsTreeView.getSelectionModel().getSelectedItem();
            if(item != null && !item.isTopElement()){
                emailTable.setItems(item.getData());
                getModelAccess().setSelectedFolder(item);//Passo al model access la cartella selezionata
                //Pulisco il messaggio selezionato
                btnRemoveEmail.disableProperty().set(true);
                getModelAccess().setSelectedMessage(null);
            }
        });
        
        /**
         * 
         */
        emailTable.setOnMouseClicked(e -> {
            EmailMessageBean message = emailTable.getSelectionModel().getSelectedItem();
            if(message != null){
                getModelAccess().setSelectedMessage(message);
                messageRendererService.setMessageToRender(message);
                messageRendererService.restart();
                if(!getModelAccess().getSelectedFolder().getValue().toString().replaceAll("[(][0-9]+[)]", "").equals("outgoing")){
                    btnRemoveEmail.disableProperty().set(false);
                }
            }
        });
        
        /**
         * 
         */
        showDetails.setOnAction(e ->{
                setReadAction();
                Stage stage = new Stage();
                stage.setScene(viewFactory.getEmailDetailsScene());
                stage.show();
        });
        
    }
    
}