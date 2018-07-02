
package controller.services;

import java.util.List;
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
 * Servizio che si occupa di scaricare i messaggi appartenti alla
 * EmailFolderBean di istanza al primo avvio dell'applicazione
 * 
 */
public class FetchMessagesOnFolderService extends Service<Void>{

    private EmailFolderBean<String> emailFolder;
    private EmailAccountBean emailAccountBean;
    private Folder folder;

    public FetchMessagesOnFolderService(EmailFolderBean<String> emailFolder, Folder folder, EmailAccountBean emailAccountBean) {
        this.emailFolder = emailFolder;
        this.emailAccountBean = emailAccountBean;
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
             * Invia una richiesta al server per ottenere tutti i messaggi
             * appartenti all'EmailFolderBean di appartenenza
             * 
             */
            @Override
            protected Void call() throws Exception{
                ClientSessionHandler handler = emailAccountBean.getHandler();
                if(handler != null){
                    List<Message> messages = handler.getMessagesFromFolder(folder.getFolderName());
                    messages.forEach((msg) -> {
                        emailFolder.addEmail(-1, msg);
                    });
                    emailFolder.sortByDate();
                    
                }
                return null;
            }
        };
    }
    
}
