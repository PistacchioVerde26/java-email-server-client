/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;
import model.EmailMessageBean;
import model.Message;

/**
 *
 * @author alber
 * 
 * Grazie a questa classe sarebbe possibile gestire eventuali allegati o immagini
 * contenuti nel corpo di un messaggio.
 * 
 */
public class MessageRendererService extends Service<Void>{
 
    private EmailMessageBean messageToRender;
    
    //WebEngine permette di gestire una pagina html ed eventuale codice JavaScript
    private WebEngine messageRendererEngine;
    
    //StringBuffer -> stringa modificabile, senza cambiare solo il riferimento.
    //Con String ogni volta che si modifica viene creato un nuovo oggetto e cambiato il riferimento.
    //E' thread safe.
    //Rappresenta il corpo di un messaggio, da passare poi al WebEngine
    private StringBuffer sb = new StringBuffer();

    public MessageRendererService(WebEngine messageRendererEngine) {
        this.messageRendererEngine = messageRendererEngine;
    }
    
    public void setMessageToRender(EmailMessageBean messageToRender){
        this.messageToRender = messageToRender;
        //Aggancio un listener su task di questo servizio
        //Quando finisce ed è successful ritorno il mesaggio renderizzato
        this.setOnSucceeded(e -> {showMessage();});
    }
    
    /**
     * Cicla il contenuto di un messaggio, quando trova del contenuto String lo aggiunge al buffer
     */
    private void renderMessage(){
        sb.setLength(0);//Pulisco il buffer
        Message message = messageToRender.getMessageReference();//Prendo il riferimento al messaggio vero e proprio
        sb.append(message.getContent());
        /**
         * 
         * Qui si potrebbero fare altre operazioni per gestire contenuti diversi da semplice testo
         * 
         */
    }

    /**
     * 
     * @return 
     * 
     * 
     */
    @Override
    protected Task<Void> createTask() {
       return new Task<Void>(){
           @Override
           protected Void call() throws Exception{
               renderMessage();
               return null;
           }
       };
    }
    
    /**
     * 
     * 
     */
    private void showMessage(){
        messageRendererEngine.loadContent(sb.toString());
    }
}