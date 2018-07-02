
package controller.services;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.ClientSessionHandler;
import model.EmailAccountBean;
import model.EmailConstants;
import model.EmailMessageBean;
import model.Message;
import model.folder.EmailFolderBean;

/**
 *
 * @author alber
 * 
 * Servizio per inviare messaggi
 * Le istanze di questa classe vengono fatte partire su un EmailFolderBean di tipo outgoing
 * 
 */
public class MessageSenderService extends Service<Void>{
    
    private int result;//risultato dell'invio
    private EmailAccountBean emailAccountBean;
    private EmailFolderBean emailFolderBean; 

    public MessageSenderService(EmailFolderBean emailFolderBean, EmailAccountBean emailAccountBean) {
        this.emailFolderBean = emailFolderBean;
        this.emailAccountBean = emailAccountBean;
    }

    @Override
    protected Task<Void> createTask() {
        
        return new Task<Void>(){
            
            /**
             * 
             * @return
             * @throws Exception 
             * 
             * Il metodo call si sveglia ogni 5s circa e controlla se nella cartella
             * sono presenti messaggi da inviare, nel qual caso procede all'invio
             */
            @Override
            protected Void call() throws Exception{
                Thread.currentThread().setName("MsgSndrSrvc " + emailAccountBean.getEmailAddress() + "/" + emailFolderBean.getValue());
                
                List<EmailMessageBean> messages;
                ClientSessionHandler handler;
                
                Integer wakeUpInterval = ThreadLocalRandom.current().nextInt(4000, 5000 + 1);
                for(;;) {
                    Thread.sleep(wakeUpInterval);
                    messages = emailFolderBean.getData();
                    for (EmailMessageBean msg : messages) {
                        handler = emailAccountBean.getHandler();
                        if(handler != null){
                            Message msgToSend = msg.getMessageReference();
                            if (handler.sendMessage(msgToSend.getSubject(), msgToSend.recipientsToString(), msgToSend.getContent())) {
                                result = EmailConstants.MESSAGE_SENT_OK;
                                Platform.runLater(() -> {
                                        emailFolderBean.getData().remove(msg);
                                        emailFolderBean.decrementUnreadMessagesCount();
                                    });
                            } else {
                                //Senza runLater() il metodo .remove(msg) causa la rimozione dell'intera EmailFolderBean
                                Platform.runLater(() -> {
                                        emailFolderBean.getData().remove(msg);
                                        emailFolderBean.decrementUnreadMessagesCount();
                                    });
                                result = EmailConstants.MESSAGE_SENT_ERROR;
                            }
                        }
                    }
                }
            }
        };
    }
    
}