
package controller.services;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.ClientSessionHandler;
import model.EmailAccountBean;
import model.Message;
import model.folder.EmailFolderBean;
import model.folder.Folder;

/**
 *
 * @author alber
 * 
 * Servizio per aggiornare le EmailFolderBean in caso ci siano nuovi messaggi da scaricare
 * 
 * Ogni istanza di questa classe appartiene ad una specifica EmailFolderBean
 * 
 */
public class FolderUpdaterService extends Service<Void>{

    private EmailAccountBean emailAccount;
    private EmailFolderBean folderBean;
    private Folder folder;

    public FolderUpdaterService(EmailAccountBean emailAccount, EmailFolderBean folderBean, Folder folder) {
        this.emailAccount = emailAccount;
        this.folderBean = folderBean;
        this.folder = folder;
    }
    
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>(){
            /**
             * 
             * @return
             * @throws Exception 
             * 
             * Si sveglia circa ogni 6s e invia una chiamata al server per verificare
             * la presenza di nuovi messaggi da scaricare, se presenti li scarica e li aggiunge 
             * all'EmailFolderBean di riferimento
             * 
             */
            @Override
            protected Void call() throws Exception{
                Thread.currentThread().setName("FldUpdtService " + emailAccount.getEmailAddress() + "/" + folder.getFolderName());
                        
                Integer wakeUpInterval = ThreadLocalRandom.current().nextInt(5000, 6000 + 1);
                for(;;){
                    Thread.sleep(wakeUpInterval);
                    
                    if (FetchFolderService.noServicesActive()) {
                        ClientSessionHandler handler = emailAccount.getHandler();
                        if(handler != null){
                            List<Message> newMessages = handler.downloadNewMessages(folder.getFolderName());
                            if(newMessages != null){
                                newMessages.forEach((msg) -> {
                                    folderBean.addEmail(0, msg);
                                }); 
                            } 
                        }
                    }
                }
            }
        };
    }
    
}