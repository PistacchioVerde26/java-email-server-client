/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.IOException;

/**
 *
 * @author alber
 * 
 * Rappresenta un utente di una casella di posta.
 * Corrisponde ad una EmailFolderBean topElement = true
 * 
 * Permette inoltre di interagire con l'handler delle sessioni
 * per recuperari i dati dal server
 * 
 */
public class EmailAccountBean {
    
    private final String emailAddress;
    
    private int loginState = EmailConstants.LOGIN_STATE_NOT_READY;

    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setLoginState(int loginState){
        this.loginState = loginState;
    }
    
    public int getLoginState() {
        return loginState;
    }
    
    public EmailAccountBean(String emailAddress) throws IOException{
        this.emailAddress = emailAddress;   
    }
    
    /**
     * 
     * @return
     * @throws IOException 
     * 
     * Crea un gestore di sessione passandogli parametri di connessione,
     * definiti nel protocollo ServerCOM
     * 
     */
    public ClientSessionHandler getHandler() throws IOException{
        return new ClientSession(ServerCOM.SERVER_ADDRESS, ServerCOM.SERVER_PORT, emailAddress).getSessionHandler();
    }
    
    /**
     * 
     * @return 
     * 
     * Controlla se il server è online
     * 
     */
    public boolean isServerOnline(){
        ClientSession cs = new ClientSession(ServerCOM.SERVER_ADDRESS, ServerCOM.SERVER_PORT, "");
        return cs.isServerOnline();
    }
    
}
