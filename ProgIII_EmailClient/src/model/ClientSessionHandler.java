
package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import model.folder.Folder;

/**
 *
 * @author alber
 * 
 * Gestisce la comunicazione tra client e server
 * Ogni ClientSessionHandler gestisce una singola richiesta ed è associato ad un indirizzo email
 * 
 * La comunicazione avviene secondo il protoccolo definito dalla classe ServerCOM
 * 
 */
public class ClientSessionHandler {

    private String owner;

    private Writer out;
    private BufferedReader in;

    public ClientSessionHandler(Writer out, BufferedReader in, String owner) {
        this.out = out;
        this.in = in;
        this.owner = owner;
    }

    /**
     * 
     * @param getChildrenOfThis
     * @return 
     * 
     * Inoltra una richiesta al server per ottenere le cartelle figlie di quella passata come parametro
     * 
     */
    public List<Folder> getFolderChildren(Folder getChildrenOfThis) {
        List<Folder> children = new ArrayList<>();
        try {
            out.append(new ServerCOM(ServerCOM.GET_FOLDER_LIST, new String[]{owner, getChildrenOfThis.getFolderName()}).toString()).append("\n");
            out.flush();
            String response;
            while ((response = in.readLine()) != null) {
                String[] parsedResponse = ServerCOM.parseRequest(response);
                if (parsedResponse.length == 1) {
                    Folder tmpFolder = new Folder(parsedResponse[0], getChildrenOfThis);
                    children.add(tmpFolder);
                }
            }
        } catch (IOException ex) {
            System.out.println("Exception getting folder children ->" + ex.getMessage());
        }finally{
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                System.out.println("Exception closing streams -> " + ex.getMessage());
            }  
        }
        return children;
    }
    
    /**
     * 
     * @param folderName
     * @return 
     * 
     * Inoltra una richiesta al server per ottenere i messaggi della cartella corrispondente al nome passato come parametro
     * 
     */
    public List<Message> getMessagesFromFolder(String folderName) {
        List<Message> messages = new ArrayList<>();
        try{
            out.append(new ServerCOM(ServerCOM.GET_FOLDER_MESSAGES, new String[]{owner, folderName}).toString()).append("\n");
            out.flush();
            String response;
            while((response = in.readLine()) != null){
                String[] parsedResponse = ServerCOM.parseRequest(response);
                if(parsedResponse.length == 1){
                    String[] params = ServerCOM.parseParameters(response);
                    if(params.length == 9){
                        //0 ID, 1 from, 2 subject, 3 recipients, 4 date, 5 content, 6 size, 7 ownerEmail, 8 isRead
                        messages.add(new Message(Integer.valueOf(params[0]), 
                                params[1], 
                                params[2], 
                                params[3], 
                                params[4], 
                                params[5], 
                                Integer.valueOf(params[6]), 
                                params[7], 
                                params[8]));
                    }
                }
            }
            if(messages.size() > 0) return messages;
        }catch(IOException ex){
            System.out.println("Exception getting messages on folder ->" + ex.getMessage());
        }finally{
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                System.out.println("Exception closing streams -> " + ex.getMessage());
            }  
        }
        return null;
    }
    
    /**
     * 
     * @param folderName
     * @return 
     * 
     * Inoltra una richiesta al server per ottenere, se presenti, messaggi non ancora segnati come scaricati
     * contenuti nella cartella con nome corrispondente a quello passato come parametro
     * 
     */
    public List<Message> downloadNewMessages(String folderName){
        List<Message> messages = new ArrayList<>();
        try{
            out.append(new ServerCOM(ServerCOM.GET_FOLDER_NEW_MESSAGES, new String[]{owner, folderName}).toString()).append("\n");
            out.flush();
            
            String response;
            while((response = in.readLine()) != null){
                String[] parsedResponse = ServerCOM.parseRequest(response);
                if(parsedResponse.length == 1){
                    String[] params = ServerCOM.parseParameters(response);
                    if(params.length == 9){
                        //System.out.println("Subject " + params[2]);
                        //0 ID, 1 from, 2 subject, 3 recipients, 4 date, 5 content, 6 size, 7 ownerEmail, 8 isRead
                        messages.add(new Message(Integer.valueOf(params[0]), 
                                params[1], 
                                params[2], 
                                params[3], 
                                params[4], 
                                params[5], 
                                Integer.valueOf(params[6]), 
                                params[7], 
                                params[8]));
                    }
                }
            }
            if(messages.size() > 0) return messages;
        }catch(IOException ex){
            System.out.println("Exception downloading new messages ->" + ex.getMessage());
        }finally{
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                System.out.println("Exception closing streams -> " + ex.getMessage());
            }  
        }
        return null;
    }

    /**
     * 
     * @param subject
     * @param recipients
     * @param content
     * @return 
     * 
     * Si occupa di creare ed inviare al server un nuovo messaggio con i parametri passati
     * 
     */
    public boolean sendMessage(String subject, String recipients, String content) {
        try {
            //from, subject, recipients, content
            out.append(new ServerCOM(ServerCOM.MESSAGE_SEND, new String[]{owner,subject,recipients,content}).toString()).append("\n");
            out.flush();
            
            String result = in.readLine();
            String[] parsedResponse = ServerCOM.parseRequest(result);
            System.out.println(parsedResponse[0]);
            return (parsedResponse[0].equals(String.valueOf(ServerCOM.MESSAGE_SENT_OK)));
            
        } catch (IOException ex) {
           System.out.println("Exception sending message -> " + ex.getMessage());
        }finally{
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                System.out.println("Exception closing streams -> " + ex.getMessage());
            }  
        }
        return false;
    }
    
    /**
     * 
     * @param msgID 
     * 
     * Si occupa di inviare una richiesta al server per segnalare come letto 
     * un messaggio con msgID corrispondente al parametro
     * 
     */
    public void setMessageRead(int msgID){
        try{
            out.append(new ServerCOM(ServerCOM.SET_MESSAGE_READ, new String[]{owner, String.valueOf(msgID)}).toString()).append("\n");
            out.flush();   
        }catch(IOException ex){
            System.out.println("Exception setting message read -> " + ex.getMessage());
        }finally{
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                System.out.println("Exception closing streams -> " + ex.getMessage());
            }  
        }
    }
    
    /**
     * 
     * @param folderName
     * @param msgID 
     * @return 
     * 
     * Si occupa di inviare una richiesta al server eliminare 
     * un messaggio con msgID corrispondente al parametro contenuto nella cartella specificata da folderName
     * 
     */
    public boolean deleteMessage(String folderName, int msgID) {
        try{
            out.append(new ServerCOM(ServerCOM.DELETE_MESSAGE, new String[]{owner, folderName, String.valueOf(msgID)}).toString()).append("\n");
            out.flush();
        }catch(IOException ex){
            System.out.println("Exception deleting message -> " + ex.getMessage());
        }finally{
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                System.out.println("Exception closing streams -> " + ex.getMessage());
            }  
        }
        return false;
    }
    
    /**
     * 
     * @param emailAddress
     * @return 
     * 
     * Invia una richiesta al server per verificare se l'indirizzo email passato come parametro esiste.
     *
     * N.B.
     * Se non esiste il server penserà a crearlo, quindi in ogni caso il login sarà SUCCESSUFL
     * 
     */
    public boolean login(String emailAddress){
        try {
            out.append(new ServerCOM(ServerCOM.LOGIN, emailAddress).toString()).append("\n");
            out.flush();
            
            String response = in.readLine();
            String[] parsedResponse = ServerCOM.parseRequest(response);
            if(parsedResponse.length == 1){
                return Boolean.valueOf(parsedResponse[0]);
            }
        } catch (IOException ex) {
            System.out.println("Exception loggin in -> " + ex.getMessage());
        }finally{
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                System.out.println("Exception closing streams -> " + ex.getMessage());
            }
        }
        return false;
    }

}
