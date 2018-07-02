/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.folder;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alber
 * 
 * CLASSE UTILIZZATA DALL'HANLDER DELLA SESSIONE PER COMUNICARE IN MODO COERENTE CON IL SERVER
 * 
 */
public class Folder{
    
    private Folder parent;
    private List<Folder> children;
    private String absolutePath;
    
    private String folderName;
    
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
    
    //GETTERS AND SETTERS
    
    public Folder getParentFolder() {
        return parent;
    }
    
    public void setParentFolder(Folder parentFolder) {
        this.parent = parentFolder;
    }
    
    public String getFolderName() {
        return folderName;
    }
    
    public List<Folder> getChildren() {
        return children;
    }
    
    public void addChild(Folder child){
        //if(!children.contains(child)) children.add(child);
        children.add(child);
    }
    
    public String getPath(){
        return absolutePath;
    }
    
    //GETTERS AND SETTERS END
    
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