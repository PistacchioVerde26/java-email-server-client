
package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author alber
 */
public final class Message implements Serializable{
    
    public static int lastID;
    public static boolean lastIDLoaded=false;

    /**
     * 
     * @return
     * @throws IOException 
     * 
     * Si occupa di assegnare ad ogni nuovo messaggio un ID univoco
     * 
     */
    public static int getNewID() throws IOException{
        if(!lastIDLoaded)loadLastID();
        int newID = ++lastID;
        saveLastID();
        return newID;
    }
    
    /**
     * 
     * @throws FileNotFoundException
     * @throws IOException 
     * 
     * Carica da file l'ultimo ID assegnato ad un nuovo messaggio
     * 
     */
    public static synchronized void loadLastID() throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(new File("lastMessageID.dat")));
        lastID = Integer.valueOf(br.readLine());
        br.close();
        lastIDLoaded = true;
    }

    /**
     * 
     * @throws FileNotFoundException
     * @throws IOException 
     * 
     * Salva su file l'ultimo ID assegnato ad un nuovo messaggio
     * 
     */
    public static synchronized void saveLastID() throws FileNotFoundException, IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("lastMessageID.dat")));
        bw.append(String.valueOf(lastID));
        bw.flush();
        bw.close();
    }

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
     * @param sender
     * @param subject
     * @param recipients
     * @param content
     * @throws IOException 
     * 
     * Costruttore in caso di nuovi messaggi. 
     * Quelli già esistenti vengono caricati direttamente da file.
     * 
     */
    public Message(String sender, String subject, String recipients, String content) throws IOException {
        this.subject = subject;
        this.sender = sender;
        this.parseRecipients(recipients);
        this.sentDate = getTodayDate();
        this.content = content;
        this.msgID = getNewID();
    }
    
    /**
     * 
     * @param sendToStr 
     * 
     * Estrapola dalla stringa passata come parametro i destinatari del messaggio
     * 
     */
    public void parseRecipients(String sendToStr){
        recipients = new ArrayList<>();
        String[] rc = sendToStr.split(";");
        if(rc != null){
            for(String str : rc){
                if(checkEmail(str))
                    recipients.add(str);
            }
        }
    }
    
    /**
     * 
     * @return 
     * 
     * Ritorna data e ora correnti
     * 
     */
    public Date getTodayDate(){
        Instant instant = Instant.now();
        long timeStampMillis = instant.toEpochMilli();
        Date date = new Date(timeStampMillis);
        return date;
    }
    
    /**
     * 
     * @param email
     * @return 
     * 
     * Verifica se la stringa passata come parametro è un indirizzo email
     * 
     */
    public boolean checkEmail(String email){
        return email.matches("[a-zA-Z0-9]+[._a-zA-Z0-9!#$%&'*+-/=?^_`{|}~]*[a-zA-Z]*@[a-zA-Z0-9]{2,8}.[a-zA-Z.]{2,6}");
    }
    
    //SETTERS AND GETTERS
    
    public String getOwnerEmail(){
        return ownerEmail;
    }
    
    public void setOwnerEmail(String ownerEmail){
        this.ownerEmail = ownerEmail;
    }
    
    public void setDownloaded(boolean downloaded){
        this.isDownloaded = downloaded;
    }
    
    public boolean isDownloaded(){
        return isDownloaded;
    }
    
    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return sender;
    }

    public void setFrom(String from) {
        this.sender = from;
    }

    public List<String> getRecipients() {
        return recipients;
    }
    
    public Date getSentDate() {
        return sentDate;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getContent() {
        return content;
    }

    public int getMsgID() {
        return msgID;
    }

    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }
    
    public boolean isRead(){
        return this.isRead;
    }
    
    public void setRead(boolean read){
        this.isRead = read;
    }
    
    //SETTERS AND GETTERS ENDS
    
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
    
    @Override
    public String toString() {
        return "Message{" + "ownerEmail=" + ownerEmail + ", subject=" + subject + ", from=" + sender + ", recipients=" + recipients + ", sentDate=" + sentDate + ", size=" + size + ", content=" + content + ", msgID=" + msgID + ", isRead=" + isRead + ", isDownloaded=" + isDownloaded + '}';
    }

}