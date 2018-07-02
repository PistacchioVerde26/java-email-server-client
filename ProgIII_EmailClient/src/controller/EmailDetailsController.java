/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.services.MessageRendererService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import model.EmailMessageBean;


/**
 *
 * @author alber
 * 
 * Controller che gestisce i dati e i componenti della view
 * per mostrare a video i dettagli di un determinato messaggio
 * 
 */
public class EmailDetailsController extends AbstractController implements Initializable{
    
    private EmailMessageBean selectedMessage;
    
    @FXML
    private Label senderLabel;

    @FXML
    private Label recipientsLabel;

    @FXML
    private WebView webView;

    @FXML
    private Label subjectLabel;
    
    @FXML
    void answerEmailAction(ActionEvent event) {
        Stage stage = new Stage();
        stage.setScene(ViewFactory.defaultFactory.getReplyMessageScene(selectedMessage, false));
        stage.show();
        Node source = (Node) event.getSource();
        Stage thisStage = (Stage) source.getScene().getWindow();
        thisStage.close();
    }
    
    @FXML
    void answerAllEmailAction(ActionEvent event) {
        Stage stage = new Stage();
        stage.setScene(ViewFactory.defaultFactory.getReplyMessageScene(selectedMessage, true));
        stage.show();
        Node source = (Node) event.getSource();
        Stage thisStage = (Stage) source.getScene().getWindow();
        thisStage.close();
    }

    public EmailDetailsController(ModelAccess modelAccess) {
        super(modelAccess);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        selectedMessage = getModelAccess().getSelectedMessage();
        MessageRendererService messageRendererService = new MessageRendererService(webView.getEngine());
        
        subjectLabel.setText(selectedMessage.getSubject());
        senderLabel.setText(selectedMessage.getSender());
        recipientsLabel.setText(selectedMessage.getMessageReference().recipientsToString());
        messageRendererService.setMessageToRender(selectedMessage);
        messageRendererService.restart();
        
    }
    
}
