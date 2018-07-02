
package controller;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.EmailAccountBean;
import model.EmailMessageBean;
import model.folder.EmailFolderBean;

/**
 *
 * @author alber
 * 
 * Un istanza di questa classe è utilizzata per la 
 * comunicazione tra diversi controllori
 * 
 */
public class ModelAccess {
    
    //Mantiene a runtime l'ultimo messaggio su cui l'utente ha cliccato
    private EmailMessageBean selectedMessage;
    
    //Mantiene a runtime l'ultima cartelle su cui un utente ha cliccato
    private EmailFolderBean<String> selectedFolder;
    
    //Mantiene una mappa degli oggetti EmailAccountBean creati dai controller
    //associati all'indirizzo email di appartenenza
    private Map<String, EmailAccountBean> emailAccounts = new HashMap<>();
    
    //Mantiene una mappa delle EmailFolderBean di tipo outoing
    //associate al loro possessore (indirizzo email)
    private Map<String, EmailFolderBean> emailOutgoingBeans = new HashMap<>();
    
    //Lista osservabile di tutti gli indirizzi email da cui sono stati creati degli EmailAccountBean
    //E' necessario che sia di tipo observable per poterla passare ad un ChoiceBox
    private ObservableList<String> emailAccountsNames = FXCollections.observableArrayList();
    
    public ObservableList<String> getEmailAccountNames(){
        return emailAccountsNames;
    }
    
    public EmailAccountBean getEmailAccountByName(String accountName){
        return emailAccounts.get(accountName);
    }
    
    public void addAccount(EmailAccountBean emailAccountBean){
        emailAccounts.put(emailAccountBean.getEmailAddress(), emailAccountBean);
        emailAccountsNames.add(emailAccountBean.getEmailAddress());
    }
    
    public EmailFolderBean getOutgoingBean(String emailAccount){
        return emailOutgoingBeans.get(emailAccount);
    }
    
    public void addOutgoingBean(EmailFolderBean outgoingBean, String emailAccount){
        emailOutgoingBeans.put(emailAccount, outgoingBean);
    }
    
    public EmailMessageBean getSelectedMessage(){
        return selectedMessage; 
    }
    
    public void setSelectedMessage(EmailMessageBean selectedMessage){
        this.selectedMessage = selectedMessage;
    }
    
    public EmailFolderBean getSelectedFolder(){
        return selectedFolder; 
    }
    
    public void setSelectedFolder(EmailFolderBean selectedFolder){
        this.selectedFolder = selectedFolder;
    }
    
}