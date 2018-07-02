package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alber
 */
public class Folder{
    
    /**
     * 
     * @param root 
     * 
     * createStructure crea la struttura base della casella di posta.
     * 
     */
    public static void createStructure(String root){
        new File(root).mkdir();
        new File(root+"/inbox").mkdir();
        new File(root+"/outbox").mkdir();
    }

    private Folder parent;
    private List<Folder> children;
    private String absolutePath;
    
    private String folderName;
    
    private boolean structureLoaded=false;
    
    /**
     * 
     * @param folderName 
     *
     * Costruttore di una cartella radice
     * 
     */
    public Folder(String folderName) {
        this.folderName = folderName;
        this.absolutePath = folderName+"/";
        this.parent = null;
        this.children = new ArrayList<>();
    }
    
    /**
     * 
     * @param folderName
     * @param parent 
     * 
     * Costruttore di una cartella figlia
     * 
     */
    public Folder(String folderName, Folder parent){
        this.folderName = folderName;
        this.parent = parent;
        if(parent != null)
            this.absolutePath = parent.getPath()+folderName+"/";
        this.children = new ArrayList<>();
    }
    
    //GETTER AND SETTERS
    
    public Folder getParentFolder() {
        return parent;
    }

    public void setParentFolder(Folder parentFolder) {
        this.parent = parentFolder;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    
    public String getPath(){
        return absolutePath;
    }
    
    public List<Folder> getChildren() {
        return children;
    }
    //GETTER AND SETTERS ENDS
    
    /**
     * 
     * @param child 
     * 
     * Aggiunge una cartella figlia a this
     * 
     */
    public void addChild(Folder child){
        //if(!children.contains(child)) children.add(child);
        children.add(child);
    }
    
    /**
     * 
     * @return 
     * 
     * verifica fisicamente su file se la cartella corrente ha delle sottocartelle
     * 
     */
    public boolean hasChildren(){
        File dir = new File(absolutePath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if(!child.isFile()){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Si occupa di caricare ricorsivamente la gerarchia delle cartelle
     */
    public void loadStructure(){
        File dir = new File(absolutePath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if(!child.isFile()){
                    Folder newChild = new Folder(child.getName(), this);
                    children.add(newChild);
                    newChild.loadStructure();
                }
            }
            structureLoaded = true;
        }
    }
    
    /**
     * 
     * @return 
     * 
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     * 
     * ritorna una lista completa dei messaggi presenti in questa cartella, prelevandoli direttamente da file.
     * Inoltre segna i messaggi non ancora scaricati come scaricati, e salva la modifica su file.
     * 
     */
    public List<Message> openMessages() throws FileNotFoundException, IOException, ClassNotFoundException{
        List<Message> msgs = new ArrayList();
        File dir = new File(absolutePath);
        File[] directoryListing = dir.listFiles();
        if(directoryListing != null){
            for(File child : directoryListing){
                if(child.isFile()){
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(child));
                    Message tmpMessage = (Message) ois.readObject();
                    Integer size = (int) (long) child.length();
                    tmpMessage.setSize(size);
                    msgs.add(tmpMessage);
                    ois.close();
                    if (!tmpMessage.isDownloaded()) {
                        tmpMessage.setDownloaded(true);
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(child));
                        oos.writeObject(tmpMessage);
                        oos.close();
                    }
                }
            }
        }
        return msgs;
    }
    
    /**
     * 
     * @return 
     * 
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     * 
     * controlla su file i messaggi presenti in questa cartella e 
     * ritorna una lista con quelli segnati come non ancora scaricati
     * 
     */
    public List<Message> getNewMessages() throws FileNotFoundException, IOException, ClassNotFoundException{
        List<Message> msgs = new ArrayList();
        File dir = new File(absolutePath);
        File[] directoryListing = dir.listFiles();
        if(directoryListing != null){
            for(File child : directoryListing){
                if(child.isFile()){
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(child));
                    Message tmpMessage = (Message) ois.readObject();
                    Integer size = (int) (long) child.length();
                    tmpMessage.setSize(size);
                    ois.close();
                    if (!tmpMessage.isDownloaded()) {
                        msgs.add(tmpMessage);
                        tmpMessage.setDownloaded(true);
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(child));
                        oos.writeObject(tmpMessage);
                        oos.close();
                    }
                }
            }
        }
        return msgs;
    }
    
    /**
     * 
     * @param message 
     * 
     * @throws java.io.IOException
     * 
     * prende un oggetto di tipo Message come parametro e si occupa di scriverlo su file all'interno di this con un nome univoco
     * 
     */
    public void writeMessage(Message message) throws IOException{
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(absolutePath + File.createTempFile("-" + message.getSubject(), "_").getName()));
        message.setOwnerEmail(absolutePath.substring(0, absolutePath.indexOf("/")));
        oos.writeObject(message);
        oos.close();
    }
    
    /**
     * 
     * @param msgID 
     * 
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     * 
     * cerca fisicamente su file all'interno di this un messaggio con msgID corrispondente
     * al parametro e lo segna come letto, salvando la modifica su file.
     * 
     */
    public void setMessageRead(int msgID) throws FileNotFoundException, IOException, ClassNotFoundException{
        if(!structureLoaded) loadStructure();
        File dir = new File(absolutePath);
        File[] directoryListing = dir.listFiles();
        if(directoryListing != null){
            for(File child : directoryListing){
                if(child.isFile()){
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(child));
                    Message tmpMessage = (Message) ois.readObject();
                    if (tmpMessage.getMsgID() == msgID) {
                        tmpMessage.setRead(true);
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(child));
                        oos.writeObject(tmpMessage);
                        oos.flush();
                        oos.close();
                        ois.close();
                        return;
                    }
                }
            }
        }
        for(Folder child : children){
            child.setMessageRead(msgID);
        }
    }
    
    /**
     * 
     * @param msgID
     * @return 
     * 
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     * 
     * cerca fisica su file all'interno di this un messaggio con msgID corrispondente a 
     * quello passato come parametro e lo elimina.
     * 
     */
    public boolean deleteMessage(int msgID) throws FileNotFoundException, IOException, ClassNotFoundException{
        File dir = new File(absolutePath);
        File[] directoryListing = dir.listFiles();
        if(directoryListing != null){
            for(File child : directoryListing){
                if(child.isFile()){
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(child));
                    Message tmpMsg = (Message) ois.readObject();
                    if (tmpMsg.getMsgID() == msgID) {
                        ois.close();
                        child.delete();
                        return true;
                    }
                    ois.close(); 
                }
            }
        }
        return false;
    }
    
    /**
     * 
     * @param fldName
     * @return 
     * 
     * metodo esterno per ottenere una cartella con nome uguale a quello specificato
     * presente nella struttura ad albero corrente
     * 
     * N.B. nel caso di più ricorrenze di fldName, ritorna sempre l'ultima cartella che soddisfa la condizione
     * 
     */
    public Folder getFolderByName(String fldName){
        Folder result = getFolderByNameInner(fldName);
        return result != null ? result : this.folderName.equals(fldName) ? this : null;
    }
    
    /**
     * 
     * @param fldName
     * @return 
     * 
     * metodo interno priato per cercare e ritornare una cartella con nome
     * uguale a quello speficificato nella struttura ad albero corrente.
     * 
     */
    private Folder getFolderByNameInner(String fldName){
        if(!structureLoaded) loadStructure();
        Folder result = null;
        if(children.size() > 0){
            for(Folder child : children){
                if(child.getFolderName().equals(fldName)){
                    result = child;
                }
            }
            if(result != null) return result;
            else return searchFolderInChildren(fldName);
        }
        return null;
    }
    
    /**
     * 
     * @param fldName
     * @return 
     * 
     * metodo ricorsivo per cercare e tornare una cartella con nome uguale a quello specificato come parametro
     * in tutti i figli 
     * 
     */
    private Folder searchFolderInChildren(String fldName){
        for(Folder child: children){
            Folder tmp = null;
            tmp = child.getFolderByName(fldName);
            if(tmp != null) return tmp;
        }
        return null;
    }
    
    /**
     * 
     * @return 
     * 
     * ritorna una stringa formattata con i nomi dei figli di this
     * 
     */
    public String printChildren(){
        String result = "(";
        for(Folder child : children){
            result += child.getFolderName()+" | ";
        }
        result += ")";
        return result;
    }
    
    
    /**
     * 
     * @return 
     * 
     * ritorna una stringa con la struttura dell'albero partendo da this
     * 
     */
    @Override
    public String toString(){
        String str = "("+folderName;
        if(children.size() > 0){
            str += "(";
            for(Folder child : children){
                if(child != null)
                    str += child;
                }
            str += ")";
        }
        str += ")";
        return str;
    }
    
}