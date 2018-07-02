/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.table;

import java.util.Comparator;

/**
 *
 * @author alber
 * 
 * Classe necessaria per formattare correttamente la dimensione in byte di un messaggio
 * 
 */
public class FormattableInteger implements Comparator<FormattableInteger>{
 
    private int size;

    public FormattableInteger(int size) {
        this.size = size;
    }
    
    /**
     * 
     * @return 
     * 
     * Formatta corettamente in base ai byte il valore di size
     * 
     */
    @Override
    public String toString(){
        String ris;
        if(size <= 0) ris = "0";
        else if(size < 1024) ris = size + " B";
        else if(size < 1048576) ris = size/1024 + " KB";
        else ris = size/1048576 + " MB";
        return ris;
    }
    
    @Override
    public int compare(FormattableInteger o1, FormattableInteger o2) {
        Integer int1 = o1.size;
        Integer int2 = o2.size;
        
        return int1.compareTo(int2);
    }
    
}
