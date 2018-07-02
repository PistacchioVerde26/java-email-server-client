/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.folder;

import java.util.Comparator;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import model.EmailMessageBean;
import model.Message;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author alber
 * @param <T>
 * 
 * Rappresentazione di un oggetto Folder adatto ad essere utilizzato in una TreeView
 * 
 */
public class EmailFolderBean<T> extends TreeItem<String>{
 
    private boolean topElement = false;//Primo elemento di una TreeView
    private int unreadMessageCount;
    private String name;
    private String completeName;//path completo, non utilizzato al momento
    
    //Sincronizzazione
    private Lock childrenLock = new ReentrantLock();
    private Lock dataLock = new ReentrantLock();
    
    //Osservabile, quando cambia stato avverte automaticamente i suoi osservatori
    //che in questo contesto sarà la TableView
    private ObservableList<EmailMessageBean> data = FXCollections.observableArrayList();
    
    /**
     * Costruttore per elementi Top
     * @param value 
     */
    public EmailFolderBean(String value){
        super(value);
        this.name = value;
        this.completeName = value;
        data = null;
        topElement = true;
        this.setExpanded(true);
        
    }
    
    /**
     * Costruttore per sottocartelle
     * 
     * @param value
     * @param completeName
     */
    public EmailFolderBean(String value, String completeName){
        super(value);
        this.name = value;
        this.completeName = completeName;
    }
    
    /**
     * 
     * @param item 
     * 
     * Aggiunge una cartella figlia a this.
     * 
     * Lock necessario per evitare modifiche della struttura dati 
     * in contemporanea.
     * 
     */
    public void addChildren(TreeItem item){
        childrenLock.lock();
        try{
            getChildren().add(item);
        }finally{
            childrenLock.unlock();
        }
    }
    
    /**
     * 
     * Si occupa di mantere aggiornato il conteggio dei messaggi non letti
     * contenuti in questa cartella
     * 
     */
    private void updateValue(){
        if(unreadMessageCount > 0){
            this.setValue((String)(name + "(" + unreadMessageCount + ")") );
        }else{
            this.setValue(name);
        }
    }
    
    public void incrementUnreadMessagesCount(int newMessages){
        unreadMessageCount += newMessages;
        updateValue();
    }
    
    public void decrementUnreadMessagesCount(){
        unreadMessageCount--;
        updateValue();
    }
    
    /**
     * 
     * @param position
     * @param message
     * 
     * Aggiunge un nuovo EmailMessageBean alla lista data instanziadolo
     * prendendo i dati da un oggetto Message
     * 
     * Lock necessario per evitare modifiche della struttura dati 
     * in contemporanea.
     * 
     */  
    public void addEmail(int position, Message message){
        dataLock.lock();
        try{
            boolean isRead = message.isRead();
            EmailMessageBean emailMessageBean = new EmailMessageBean(
                    message.getSubject(),
                    message.getSender(),
                    message.getSentDate(),
                    message.getSize(),
                    isRead,
                    message
            );
            if(position < 0) data.add(emailMessageBean);
            else data.add(position, emailMessageBean);
            if(!isRead) incrementUnreadMessagesCount(1);
        }finally{
            dataLock.unlock();
        }
    }
    
    /**
     * 
     * @param message 
     * 
     * Elimina un EmailMessageBean dai dati di this
     * 
     */  
    public void removeEmail(EmailMessageBean message){
    dataLock.lock();
        try{
            this.data.remove(message);
        }finally{
            dataLock.unlock();
        }
    }

    /**
     * Ordina i messaggi in base alla Date
     */
    public void sortByDate(){
        dataLock.lock();
        try{
            data.sort(new Comparator<EmailMessageBean>(){
                @Override
                public int compare(EmailMessageBean o1, EmailMessageBean o2) {
                    Date int1 = o1.getDate();
                    Date int2 = o2.getDate();
                    return int2.compareTo(int1);
                }
            });
        }finally{
           dataLock.unlock(); 
        }
    }
    
    public boolean isTopElement(){
        return topElement;
    }
    
    public ObservableList<EmailMessageBean> getData(){
        return data;
    }
    
}

