
package controller;

import controller.services.CreateAndRegisterEmailAccountService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.folder.EmailFolderBean;

/**
 *
 * @author alber
 * 
 * Gestisce gli account email che sono stati aggiunti da un user
 * a questa installazione dell'applicazione
 * 
 */
public final class EmailAccountsController{
    
    private static File file = new File("emailAccounts.dat");
    
    private List<String> emailAccounts;
    private EmailFolderBean<String> foldersRoot;
    private ModelAccess modelAccess;
    
    public EmailAccountsController(EmailFolderBean<String> foldersRoot, ModelAccess modelAccess){
        this.emailAccounts = getEmailAccounts();
        this.foldersRoot = foldersRoot;
        this.modelAccess = modelAccess;
    }
    
    /**
     * 
     * @return
     * @throws IOException
     * 
     * Controlla che il file dove sono memorizzati gli account esiste
     * In caso negativo lo crea
     * 
     */
    public boolean checkFile() throws IOException{
        return file.exists() ? true : file.createNewFile();
    }
    
    /**
     * 
     * Avvia il servizio per il caricarmento degli account email
     * 
     */
    public void start(){
        for(String str : emailAccounts){
            CreateAndRegisterEmailAccountService service = new CreateAndRegisterEmailAccountService(str, foldersRoot, modelAccess);
            service.start();
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
                System.out.println("Exception starting email accounts ->" + ex);
            }
        }
    }
    
    /**
     * 
     * @return 
     * 
     * Preleva gli account email 
     * 
     */
    public List<String> getEmailAccounts(){
        List<String> tmp = new ArrayList<>();
        try {
            checkFile();
            BufferedReader br;
            br = new BufferedReader(new FileReader(file));
                    String account;
            while((account = br.readLine()) != null){
                tmp.add(account);
            }
            br.close();
        } catch (IOException ex) {
           System.out.println("Exception getting email accounts from file ->" + ex);
        }

        return tmp;
    }
    
    /**
     * 
     * @param account 
     * 
     * Permette di far partire il servizio per un nuovo utente aggiunto a runtime
     * 
     */
    public void addAndStartAccount(String account){
        CreateAndRegisterEmailAccountService service = new CreateAndRegisterEmailAccountService(account, foldersRoot, modelAccess);
        service.start();
    }
    
    /**
     * 
     * @param newAcount 
     * 
     * Controlla che la stringa inserita sia conforme per un indirizzo email 
     * e poi avvia il salvataggio su file
     * 
     */
    public void addNewAccount(String newAcount){
        if(newAcount == null || !newAcount.matches("[a-zA-Z0-9]+[._a-zA-Z0-9!#$%&'*+-/=?^_`{|}~]*[a-zA-Z]*@[a-zA-Z0-9]{2,8}.[a-zA-Z.]{2,6}")) return;
        if(!existsAlready(newAcount)) writeNewAccountOnFile(newAcount);
    }
    
    /**
     * 
     * @param account 
     * 
     * Rimuove un account email da quelli impostati
     * 
     */
    public void removeAccount(String account){
        emailAccounts.remove(account);
        foldersRoot.getChildren().removeIf((str) -> (str.getValue().equals(account)));
        updateAccountsFile(account);
    }
    
    /**
     * 
     * @param account 
     * 
     * Aggiorna gli account su file prendendoli dalla struttra dati in memoria
     * 
     */
    public void updateAccountsFile(String account){
        try {
            checkFile();
            List<String> tmp = new ArrayList<>();
            BufferedWriter bw;
            bw = new BufferedWriter(new FileWriter(file));
            for(String str : emailAccounts){
                if(!str.equals(account)){
                    bw.append(str).append("\n");
                }
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
           System.out.println("Exception updating email accounts file ->" + ex);
        }
    }
    
    /**
     * 
     * @param account
     * @return 
     * 
     * Controlla che un account email con lo stesso indirizzo non sia già stato aggiunto
     * 
     */
    public boolean existsAlready(String account){
        return emailAccounts.stream().anyMatch((str) -> (str.equals(account)));
    }
    
    /**
     * 
     * @param newAcount 
     * 
     * Scrive su file un nuovo account aggiunto a runtime
     * 
     */
    public void writeNewAccountOnFile(String newAcount){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("emailAccounts.dat", true))) {
            bw.append(newAcount).append("\n");
            bw.flush();
            addAndStartAccount(newAcount);
        } catch (IOException ex) {
           System.out.println("Exception writing email on accounts file -> " + ex);
        }
    }
    
}
