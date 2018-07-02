package model;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author alber
 */
public class ServerSessionHandler extends Observable{
    
    private Folder root;
    
    /**
     * 
     * @param result
     * @param args
     * 
     * login controlla se l'indirizzo email passato è esistente.
     * Se esiste notifica una nuova connessione agli osservatori
     * Se non esiste crea la struttura base di un account e notifica gli osservatori.
     * 
     */
    public void login(Writer result, String args){
        String[] params = ServerCOM.parseParameters(args);
        if(params.length == 1){
            if(rootExists(params[0])){
                stateChange("USER CONNECTED -> " + params[0]);
            }else{
                Folder.createStructure(params[0]);
                stateChange("NEW USER CONNECTED -> " + params[0] + "\n\tCREATED FOLDER STRUCTURE FOR NEW USER " + params[0]);
            }
            try {
                result.append("true").append("\n");
                result.flush();
            } catch (IOException ex) {
                stateChange("EXCEPTION LOGGING-IN -> " + ex);
            }
        }
    }
    
    /**
     * 
     * @param result
     * @param args
     * 
     * getFolderList ritorna sullo stream in uscita tutte i figli della cartella passata come parametro
     * 
     */
    public void getFolderChildren(Writer result, String args){
        String[] tmp = ServerCOM.parseParameters(args);
        String email;
        String folderName;
        
        email = tmp[0];
        folderName = tmp.length == 2 ? tmp[1] : tmp[0];
        try{
            if(rootExists(email)){
                if(this.root == null){
                    root = new Folder(email);
                }
                Folder tmpFolder = root.getFolderByName(folderName);
                List<Folder> children = tmpFolder != null ? tmpFolder.getChildren() : null;
                if(children != null){
                    for(Folder child : children){
                        result.append(child.getFolderName()).append("\n");
                    }
                    result.flush();
                }else
                    result.append(new ServerCOM(ServerCOM.ERROR, "No children on folder").toString()).append("\n");

            }else{
                result.append(new ServerCOM(ServerCOM.ERROR, "Email address does not exist").toString()).append("\n");
                result.flush();
            }
        }catch(IOException ex){
            stateChange("EXCEPTION GETTING FOLDER CHILDREN -> " + ex);
        }   
    }
    
    /**
     * 
     * @param result
     * @param args
     * 
     * folderHasChildren controlla se la cartella passata come parametro ha dei figli e
     * restituisce il risultato sul canale di uscita dello stream
     * 
     * Può essere usato dai client per controllare se una cartella ha dei figli
     * e di conseguenza inviare una richiesta getFolderList
     * 
     */
    public void folderHasChildren(Writer result, String args){
        String[] tmp = ServerCOM.parseParameters(args);
        String email;
        String folderName;
        
        email = tmp[0];
        folderName = tmp.length == 2 ? tmp[1] : tmp[0];
        try{
            if(rootExists(email)){
                if(this.root == null){
                    root = new Folder(email);
                }
                Folder tmpFolder = root.getFolderByName(folderName);
                if(tmpFolder.getChildren() != null){
                    result.append("true").append("\n");
                }else{
                    result.append("false").append("\n");
                }
                result.flush();
            }else{
                result.append(new ServerCOM(ServerCOM.ERROR, "Email address does not exist").toString()).append("\n");
                result.flush();
            }
        }catch(IOException ex){
            stateChange("EXCEPTION CHECKING FOLDER'S CHILDREN -> " + ex);
        }
    }
    
    /**
     * 
     * @param result
     * @param args
     * 
     * getFolderMessage carica i messaggi contenuti nella cartella passata come parametro
     * e li trasmette sullo stream in uscita
     * 
     */
    public void getFolderMessages(Writer result, String args){
        String[] tmp = ServerCOM.parseParameters(args);
        String email;
        String folderName;

        email = tmp[0];
        folderName = tmp.length == 2 ? tmp[1] : tmp[0];
        try{
            if(rootExists(email)){
                if(this.root == null){
                    root = new Folder(email);
                }
                Folder folderToGetMessagesFrom = root.getFolderByName(folderName);
                if(folderToGetMessagesFrom != null){
                    List<Message> messages = folderToGetMessagesFrom.openMessages();
                    if(messages != null && messages.size() > 0){
                        for(Message msg : messages){
                            msg.setDownloaded(true);
                            //0 ID, 1 from, 2 subject, 3 recipients, 4 date, 5 content, 6 size, 7 ownerEmail, 8 isRead
                            result.append(msg.getMsgID()+ServerCOM.PARAM_SEPARATOR+
                                    msg.getFrom()+ServerCOM.PARAM_SEPARATOR+
                                    msg.getSubject()+ServerCOM.PARAM_SEPARATOR+
                                    msg.recipientsToString()+ServerCOM.PARAM_SEPARATOR+
                                    msg.getSentDate().getTime()+ServerCOM.PARAM_SEPARATOR+
                                    msg.getContent()+ServerCOM.PARAM_SEPARATOR+
                                    msg.getSize()+ServerCOM.PARAM_SEPARATOR+
                                    msg.getOwnerEmail()+ServerCOM.PARAM_SEPARATOR+
                                    msg.isRead()).append("\n");
                        }
                        result.flush();
                    }else{
                        result.append(new ServerCOM(ServerCOM.ERROR, "The specified folder does not contain messages").toString()).append("\n");
                        result.flush();
                    }
                }else{
                    result.append(new ServerCOM(ServerCOM.ERROR, "Folder does not exists").toString()).append("\n");
                    result.flush();
                }
            }else{
                result.append(new ServerCOM(ServerCOM.ERROR, "Email address does not exists").toString()).append("\n");
                result.flush();
            }
        }catch(IOException | ClassNotFoundException ex){
            stateChange("EXCEPTION GETTING MESSAGES ON FOLDER -> " + ex);
        } 
    }
    
    
    /**
     * 
     * @param result
     * @param args
     * 
     * getNewMessages richiama la funziona getNewMessages sulla cartella passata come parametro
     * Se la lista ritornata contiene almeno un messaggio vengono trasmessi sullo stream in uscita
     * 
     */
    public void getNewMessages(Writer result, String args){
        String[] params = ServerCOM.parseParameters(args);
        if(params.length == 2){
            if(rootExists(params[0])){
                if(this.root == null){
                    root = new Folder(params[0]);
                }
                try{
                    List<Message> newMessages = root.getFolderByName(params[1]).getNewMessages();
                    if(newMessages.size() > 0){
                        for(Message msg : newMessages){
                            //0 ID, 1 from, 2 subject, 3 recipients, 4 date, 5 content, 6 size, 7 ownerEmail, 8 isRead
                            result.append(msg.getMsgID()+ServerCOM.PARAM_SEPARATOR+
                                    msg.getFrom()+ServerCOM.PARAM_SEPARATOR+
                                    msg.getSubject()+ServerCOM.PARAM_SEPARATOR+
                                    msg.recipientsToString()+ServerCOM.PARAM_SEPARATOR+
                                    msg.getSentDate().getTime()+ServerCOM.PARAM_SEPARATOR+
                                    msg.getContent()+ServerCOM.PARAM_SEPARATOR+
                                    msg.getSize()+ServerCOM.PARAM_SEPARATOR+
                                    msg.getOwnerEmail()+ServerCOM.PARAM_SEPARATOR+
                                    msg.isRead()).append("\n");
                            result.flush();
                        }
                    }else{
                        result.append(new ServerCOM(ServerCOM.ERROR, "No new messages in folder").toString()).append("\n");
                        result.flush();
                    }
                }catch(IOException | ClassNotFoundException ex){
                    stateChange("EXCEPTION GETTING NEW MESSAGES -> " + ex);
                }
            }
        }
    }
    
    
    /**
     * 
     * @param result
     * @param args
     * 
     * send si occupa di distribuire una nuova email nelle caselle di posta dei destinatari.
     * Nel caso l'indirizzo email di un destinatario non esistesse invia una email di notifica al mittente del messaggio.
     * 
     */
    public void send(Writer result, String args){
        String[] params = ServerCOM.parseParameters(args);
        Message msgToSend = null;
        try{
            if(params.length == 4){
                //from, subject, recipients, content
                msgToSend = new Message(params[0],params[1],params[2],params[3]);
            }
            if(msgToSend != null){
                if(rootExists(msgToSend.getFrom())){
                    List<String> recipients = msgToSend.getRecipients();
                    int emailSent = 0;
                    for(String sendTo : recipients){
                        if(rootExists(sendTo)){
                            Folder sendToRoot = new Folder(sendTo);
                            Folder folderToWriteMessageOn = sendToRoot.getFolderByName("inbox");
                            msgToSend.setDownloaded(false);
                            folderToWriteMessageOn.writeMessage(msgToSend);
                            emailSent++;
                            stateChange("EMAIL SENT\nFROM: " + msgToSend.getFrom() + "\nTO:  " + sendTo);
                        }else{
                            //(String from, String subject, String recipients, String content)
                            Message errorMessage = new Message("errornotice@server.srv", "ERROR Recipient not found" , msgToSend.getFrom(), "Error: " + sendTo + " does not exist");
                            Folder senderRoot = new Folder(msgToSend.getFrom());
                            Folder folderToWriteSentMessageOn = senderRoot.getFolderByName("inbox");
                            folderToWriteSentMessageOn.writeMessage(errorMessage);
                            stateChange("EMAIL SEND ERROR -> \n\tFROM: " + msgToSend.getFrom() + "\nTO:  " + sendTo + ".\n\t RECIPIENTS DOES NOT EXISTS");
                            result.append(new ServerCOM(ServerCOM.ERROR, "Recipient does not exists").toString()).append("\n");
                            result.flush();
                        }
                    }
                    if(emailSent > 0){
                        //Implementare controllo che ad almeno uno dei recipient sia stato recapitato il messaggio
                        Folder senderRoot = new Folder(msgToSend.getFrom());
                        Folder folderToWriteSentMessageOn = senderRoot.getFolderByName("outbox");
                        folderToWriteSentMessageOn.writeMessage(msgToSend);
                        result.append(new ServerCOM(ServerCOM.MESSAGE_SENT_OK, "Message sent to existing recipients").toString()).append("\n");
                        result.flush();
                    }
                }else{
                    stateChange("** EMAIL SEND ERROR ->  **\nTRIED TO SEND EMAIL FROM NOT EXISTING SENDER -> " + msgToSend.getFrom());
                    result.append(new ServerCOM(ServerCOM.ERROR, "Email from does not exists").toString()).append("\n");
                    result.flush(); 
                }
            }else{
                result.append(new ServerCOM(ServerCOM.ERROR, "Error parsing message, check syntax").toString()).append("\n");
                result.flush();
            }
        }catch(IOException ex){
            stateChange("EXCEPTION SENDING MESSAGE -> " + ex);
        }
    }
    
    /**
     * 
     * @param result
     * @param args
     * 
     * deleteMessageFromFolder prende dal parametro args un ID messaggio e un nome cartella e lo elimina
     * 
     */
    public void deleteMessageFromFolder(Writer result, String args){
        String[] params = ServerCOM.parseParameters(args);
        String email;
        String folderName;
        String messageID;

        if(params.length == 3){
            email = params[0];
            folderName = params[1];
            messageID = params[2];
            System.out.println(folderName);
            try{
                if (rootExists(email)) {
                    if (messageID != null) {
                        Folder folderToDeleteMessageFrom = new Folder(email).getFolderByName(folderName);
                        if (folderToDeleteMessageFrom.deleteMessage(Integer.valueOf(messageID))) {
                            result.append(new ServerCOM(ServerCOM.DELETE_MESSAGE_OK, "Message deleted").toString()).append("\n");
                            result.flush();
                            stateChange("DELETED message from " + folderName + ". OWNER: " + email);
                        } else {
                            result.append(new ServerCOM(ServerCOM.ERROR, "Error deleting message").toString()).append("\n");
                            result.flush();
                        }
                    }
                } else {
                    result.append(new ServerCOM(ServerCOM.ERROR, "Email address does not exist").toString()).append("\n");
                    result.flush();
                }
            }catch(IOException | ClassNotFoundException ex){
                stateChange("EXCEPTION DELETING MESSAGE -> " + ex);
            }
        }
    }
    
    /**
     * 
     * @param result
     * @param args 
     * 
     * setMessageRead prende da args un ID messaggio e lo setta come letto
     * 
     */
    public void setMessageRead(Writer result, String args){
        String[] params = ServerCOM.parseParameters(args);
        
        String email = params[0];
        String msgID = params[1];
        if(rootExists(email)){
            if(this.root == null){
                root = new Folder(params[0]);
            }
            root.loadStructure();
            try {
                root.setMessageRead(Integer.valueOf(msgID));
            } catch (IOException | ClassNotFoundException ex) {
                stateChange("EXCEPTION SETTING MESSAGE READ -> " + ex);
            }
        }
           
    }
    
    /**
     * 
     * @param emailAddress
     * @return 
     * 
     * rootExists controlla se l'indirizzo email passato come parametro è valido ed esiste su file
     * 
     */
    public boolean rootExists(String emailAddress){
        return Files.exists(Paths.get(emailAddress));
    }
    
    /**
     * 
     * @param args 
     * 
     * utilizzato per notificare gli osservatori in caso di cambiamento di stato
     * 
     * 
     */
    private void stateChange(Object args){
        setChanged();
        notifyObservers(args);
    }
    
}
