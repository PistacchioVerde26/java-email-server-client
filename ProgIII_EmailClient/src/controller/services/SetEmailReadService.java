/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.services;

import java.util.concurrent.ThreadLocalRandom;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.EmailAccountBean;

/**
 *
 * @author alber
 * 
 * Si occupa di segnalare un messaggio come letto
 * 
 */
public class SetEmailReadService extends Service<Void>{

    private EmailAccountBean emailAccount;
    private int msgID;
    
    public SetEmailReadService(EmailAccountBean emailAccount, int msgID){
        this.emailAccount = emailAccount;
        this.msgID = msgID;
    }
    
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>(){
            
            /**
             * 
             * @return
             * @throws Exception 
             * 
             * Invia una richiesta al sever per settare il messaggio di riferimento come letto
             * Nel caso il server non sia online, dorme per circa 5s e riprova ad eseguire l'operazione
             */
            @Override
            protected Void call() throws Exception{
                Integer wakeUpInterval = ThreadLocalRandom.current().nextInt(4000, 5000 + 1);
                while(!emailAccount.isServerOnline())
                    Thread.sleep(wakeUpInterval);
                
                emailAccount.getHandler().setMessageRead(msgID);
                return null;
            }
        };
    }
    
}
