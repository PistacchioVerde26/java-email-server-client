/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.services;

import controller.ModelAccess;
import controller.ViewFactory;
import java.io.IOException;
import java.util.List;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.EmailAccountBean;
import model.folder.EmailFolderBean;
import model.folder.Folder;


/**
 *
 * @author alber
 * 
 * Servizio che si occupa di scaricare la struttura ad albero
 * delle cartelle dell'EmailAccountBean di istanza
 * 
 */
public class FetchFolderService extends Service<Void>{

    private EmailFolderBean<String> foldersRoot;
    private EmailAccountBean emailAccountBean;
    private ModelAccess modelAccess;
    private static int NUMBER_OF_FETCHFOLDERSSERVICES_ACTIVE = 0;
    
    public FetchFolderService(EmailFolderBean<String> foldersRoot, EmailAccountBean emailAccountBean, ModelAccess modelAccess){
        this.foldersRoot = foldersRoot;
        this.emailAccountBean = emailAccountBean;
        this.modelAccess = modelAccess;
        
        this.setOnSucceeded(e -> {
            NUMBER_OF_FETCHFOLDERSSERVICES_ACTIVE--;
        });
    }
    
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>(){
            @Override
            protected Void call() throws Exception{
                NUMBER_OF_FETCHFOLDERSSERVICES_ACTIVE++;
                Thread.currentThread().setName("FetchFolderService #" + NUMBER_OF_FETCHFOLDERSSERVICES_ACTIVE);
                /**
                 * 
                 * Crea la radice della struttura passandole come value
                 * l'indirizzo email dell'accountBean.
                 * 
                 * Dopodichè avvia il metodo ricorsivo per recuperare ogni cartella e sottocartella
                 * 
                 * Infine aggiunge la cartella virtual outgoing dove memorizzare i messaggi
                 * in attesa di essere inviati
                 * 
                 */
                if(emailAccountBean != null){
                    Folder rootFolderObject = new Folder(emailAccountBean.getEmailAddress());
                    fetchFolderChildrenRecursive(foldersRoot, rootFolderObject);
                    
                    Folder outgoing = new Folder("outgoing", rootFolderObject);
                    EmailFolderBean<String> outgoingBean = new EmailFolderBean<>(outgoing.getFolderName(), outgoing.getPath());
                    outgoingBean.setGraphic(ViewFactory.defaultFactory.resolveIcon("outgoing"));
                    foldersRoot.addChildren(outgoingBean);
                    modelAccess.addOutgoingBean(outgoingBean, emailAccountBean.getEmailAddress());
                    MessageSenderService messageSenderService = new MessageSenderService(outgoingBean, emailAccountBean);
                    messageSenderService.restart();
                }
                return null;
            }
            
            protected void fetchFolderChildrenRecursive(EmailFolderBean<String> parent, Folder folder) throws IOException{
                List<Folder> tmpList = emailAccountBean.getHandler().getFolderChildren(folder);
                for(Folder folderTmp : tmpList){
                    folder.addChild(folderTmp);
                    EmailFolderBean<String> item = new EmailFolderBean<>(folderTmp.getFolderName(), folderTmp.getPath());
                    item.setGraphic(ViewFactory.defaultFactory.resolveIcon(folderTmp.getFolderName()));
                    parent.addChildren(item);
                    item.setExpanded(true);
                    
                    FetchMessagesOnFolderService fetchMessagesOnFolderService = new FetchMessagesOnFolderService(item, folderTmp, emailAccountBean);
                    fetchMessagesOnFolderService.start();
                    
                    FolderUpdaterService folderUpdaterService = new FolderUpdaterService(emailAccountBean,item,folderTmp);
                    folderUpdaterService.start();
                    
                    fetchFolderChildrenRecursive(item, folderTmp);
                }
            }
        };
    }

    public static boolean noServicesActive(){
        return NUMBER_OF_FETCHFOLDERSSERVICES_ACTIVE == 0;
    }
    
}
