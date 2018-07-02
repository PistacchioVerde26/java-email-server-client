
package model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author alber
 */
public final class Message implements Serializable{
    
    private String ownerEmail;
    
    private String subject;
    private String sender;
    private List<String> recipients;
    private Date sentDate;
    private int size;
    private String content;
    private int msgID;
    private boolean isRead;
    private boolean isDownloaded = false;
    
    /**
     * 
     * @param ID
     * @param sender
     * @param subject
     * @param recipients
     * @param date
     * @param content
     * @param size
     * @param ownerEmail
     * @param isRead 
     * 
     * Costruttore per creare un nuovo messaggio dopo averlo scaricato dal server
     * 
     */
    public Message(int ID, String sender, String subject, String recipients, String date, String content, int size, String ownerEmail, String isRead){
        this.msgID = ID;
        this.sender = sender;
        this.subject = subject;
        parseRecipients(recipients);
        this.sentDate = new Date(Long.valueOf(date));
        this.content = content;
        this.size = size;
        this.ownerEmail = ownerEmail;
        this.isRead = Boolean.valueOf(isRead);
    }
    
    /**
     * 
     * @param from
     * @param subject
     * @param recipients
     * @param content
     * @throws IOException 
     * 
     * Costruttore per creare un nuovo messaggio da inviare
     * 
     */
    public Message(String from, String subject, String recipients, String content) throws IOException {
        this.subject = subject;
        this.sender = from;
        this.parseRecipients(recipients);
        this.content = content;
    }
    
    /**
     * 
     * @param recipientsString 
     * 
     * Estrapola dalla stringa passata come parametro i destinatari del messaggio
     * 
     */
    public void parseRecipients(String recipientsString){
        recipients = new ArrayList<>();
        String[] sc = recipientsString.split(";");
        this.recipients.addAll(Arrays.asList(sc));
    }
    
    /**
     * 
     * @return 
     * 
     * stampa formattata dei destinatari
     * 
     */
    public String recipientsToString(){
        String result = "";
        for(String str : recipients)
            result += str+";";
        return result;
    }
    
    //SETTERS AND GETTERS
    public String getOwnerEmail(){
        return ownerEmail;
    }
    
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSender() {
        return sender;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public int getSize() {
        return size;
    }

    public String getContent() {
        return content;
    }

    public int getMsgID() {
        return msgID;
    }
    
    public boolean isRead(){
        return this.isRead;
    }
    
    public List<String> getRecipients(){
        return recipients;
    }
    
    //SETTERS AND GETTERS
    
    @Override
    public String toString() {
        return "Message{" + "ownerEmail=" + ownerEmail + ", subject=" + subject + ", from=" + sender + ", recipients=" + recipients + ", sentDate=" + sentDate + ", size=" + size + ", content=" + content + ", msgID=" + msgID + ", isRead=" + isRead + ", isDownloaded=" + isDownloaded + '}';
    }
    
}