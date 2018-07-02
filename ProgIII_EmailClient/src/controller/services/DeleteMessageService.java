
package controller.services;

import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.EmailAccountBean;
import model.EmailMessageBean;
import model.folder.EmailFolderBean;

/**
 *
 * @author alber
 * 
 * Servizio che invia una richiesta al server per cancellare un messaggio
 * 
 */
public class DeleteMessageService extends Service<Void>{
    
    EmailMessageBean messageBean;
    EmailFolderBean<String> folderBean;
    EmailAccountBean accountBean;
    
    public DeleteMessageService(EmailMessageBean messageBean, EmailFolderBean<String> folderBean, EmailAccountBean accountBean){
        this.messageBean = messageBean;
        this.folderBean = folderBean;
        this.accountBean = accountBean;
    }
    
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>(){
            
            /**
             * 
             * @return
             * @throws Exception 
             * 
             * Invia una richiesta al server per cancellare un messaggio
             * 
             * Se il server non è online dorme circa 6s e ci riprova
             * 
             */
            @Override
            protected Void call() throws Exception{
                Integer wakeUpInterval = ThreadLocalRandom.current().nextInt(5000, 6000 + 1);
                
                while(!accountBean.isServerOnline())
                    Thread.sleep(wakeUpInterval);
                
                accountBean.getHandler().deleteMessage(folderBean.getValue().replaceAll("[(][0-9]+[)]", ""), messageBean.getMessageReference().getMsgID());
                Platform.runLater(() -> {
                    if(!messageBean.isRead()) folderBean.decrementUnreadMessagesCount();
                    folderBean.getData().remove(messageBean);
                });
                return null;
            }
        };
            
    }
    
}
