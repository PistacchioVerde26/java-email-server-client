/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import model.EmailMessageBean;
import model.Message;
import model.folder.EmailFolderBean;

/**
 *
 * @author alber
 */
public class ComposeMessageController extends AbstractController implements Initializable {
    
    private EmailMessageBean replyMessage = null;
    private boolean answerAll = false;
    
    @FXML
    private HTMLEditor composeArea;
    
    @FXML
    private ChoiceBox<String> senderChoice;

    @FXML
    private TextField recipientField;

    @FXML
    private TextField subjectField;

    @FXML
    private Label errorLabel;

    public ComposeMessageController(ModelAccess modelAccess) {
        super(modelAccess);
    }
    
    public ComposeMessageController(ModelAccess modelAccess, EmailMessageBean replyMessage, boolean answerAll) {
        super(modelAccess);
        this.replyMessage = replyMessage;
        this.answerAll = answerAll;
    }

    @FXML
    void sendBtnAction(ActionEvent event) throws IOException {
        boolean inputsAreValid = true;
        
        String subject = subjectField.getText();
        String content = composeArea.getHtmlText();
        String recipients = recipientField.getText();
        
        if(subject == null || subject.isEmpty()){
            inputsAreValid = false;
            errorLabel.setText("ATTENZIONE. Devi inserire l'oggetto");
        }
        if(content == null){
            inputsAreValid = false;
            errorLabel.setText("ATTENZIONE. Contenuto is null");
        }
        if(recipients == null || recipients.isEmpty()){
            inputsAreValid = false;
            errorLabel.setText("ATTENZIONE. Devi inserire almeno un destinatario");
        }else{
            String[] recipientsCheck = recipients.split(";");
            for (String str : recipientsCheck) {
                if (!str.matches("[a-zA-Z0-9]+[._a-zA-Z0-9!#$%&'*+-/=?^_`{|}~]*[a-zA-Z]*@[a-zA-Z0-9]{2,8}.[a-zA-Z.]{2,6}")) {
                    inputsAreValid = false;
                    errorLabel.setText("ERRORE nei destinatari. Esempio -> ****@****.***;****@***.***");
                }
            }
        }
        
        if(inputsAreValid){
            String sender = getModelAccess().getEmailAccountByName(senderChoice.getValue()).getEmailAddress();
            EmailFolderBean outgoingBean = getModelAccess().getOutgoingBean(sender);
                                                //String from, String subject, String recipients, String content
            outgoingBean.addEmail(0, new Message(sender, subject, recipients, content));
            Node source = (Node) event.getSource();
            Stage thisStage = (Stage) source.getScene().getWindow();
            thisStage.close();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        senderChoice.setItems(getModelAccess().getEmailAccountNames());
        if(getModelAccess().getEmailAccountNames().size() > 0)
            senderChoice.setValue(getModelAccess().getEmailAccountNames().get(0));
        
        if(replyMessage != null){
            String ownerEmail = replyMessage.getMessageReference().getOwnerEmail();
            senderChoice.setValue(ownerEmail);
            
            if(answerAll){
                List<String> recipients = replyMessage.getMessageReference().getRecipients();
                String tmp = replyMessage.getSender()+";";
                for(String str : recipients){
                    if(!str.equals(ownerEmail))
                        tmp += str+";";
                }
                recipientField.setText(tmp);
            }else{
                recipientField.setText(replyMessage.getSender());
            }
            
            
            subjectField.setText("RE- "+replyMessage.getSubject());
            composeArea.setHtmlText("<br /><br /><hr />"+replyMessage.getMessageReference().getContent().replace("\r",""));
        }
        
    }
    
}
