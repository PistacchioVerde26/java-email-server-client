/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Date;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.table.AbstractTableItem;
import model.table.FormattableInteger;

/**
 *
 * @author alber
 * 
 * Rappresenta un messaggio utilizzabile nella view
 * 
 */
public class EmailMessageBean extends AbstractTableItem{
    
    /**
     * 
     * Le SimplePoperty sono delle proprietà osservabili che supportano
     * un changeListener ed un InvalidationListener, introdotte da JavaFX
     * 
     * Offrono delle funzionalità in più rispetto a delle semplici proprietà di classe
     * 
     */
    private SimpleStringProperty sender;
    private SimpleStringProperty subject;
    private SimpleObjectProperty<Date> date;
    private SimpleObjectProperty<FormattableInteger> size;
    private Message messageReference;
    
    public EmailMessageBean(String subject, String sender, Date date, int size, boolean isRead, Message messageReference){
        super(isRead);
        this.subject = new SimpleStringProperty(subject);
        this.sender = new SimpleStringProperty(sender);
        this.date = new SimpleObjectProperty<>(date);
        this.size = new SimpleObjectProperty<>(new FormattableInteger(size));
        this.messageReference = messageReference; 
    }
    
    public String getSender() {
        return sender.get();
    }

    public String getSubject() {
        return subject.get();
    }

    public Date getDate() {
        return date.get();
    }
    
    public FormattableInteger getSize() {
        return size.get();
    }
    
    /**
     * @return 
     * Ritorna il riferimento all'oggetto Message di this 
     */
    public Message getMessageReference() {
        return messageReference;
    }
    
    @Override
    public String toString() {
        return "EmailMessageBean{" + "sender=" + sender + ", subject=" + subject + ", date=" + date + ", size=" + size + "}";
    }


}