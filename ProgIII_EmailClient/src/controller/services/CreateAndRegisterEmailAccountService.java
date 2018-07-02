
package controller.services;


import controller.ModelAccess;
import controller.ViewFactory;
import java.util.concurrent.ThreadLocalRandom;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.EmailAccountBean;
import model.EmailConstants;
import model.folder.EmailFolderBean;

/**
 *
 * @author alber
 * 
 * Servizio che si occupa di inizializzare gli EmailAccountBean
 * 
 */
public class CreateAndRegisterEmailAccountService extends Service<Integer> {
    
    private String emailAddress;
    private EmailFolderBean<String> folderRoot;
    private ModelAccess modelAccess;
    
    public CreateAndRegisterEmailAccountService(String emailAddress, EmailFolderBean<String> folderRoot, ModelAccess modelAccess){
        this.emailAddress = emailAddress;
        this.folderRoot = folderRoot;
        this.modelAccess = modelAccess;
    }
    
    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>(){
            
            /**
             * 
             * @return
             * @throws Exception 
             * 
             * Crea un EmailAccountBean dall'emailAddress di istanza e fa partire
             * i vari servizi che andranno a scaricare dal server i dati dell'account
             * corrispondente
             * 
             * Se il server non è online dorme per circa 5s e riprova
             * 
             */
            @Override
            protected Integer call() throws Exception {
                Integer wakeUpInterval = ThreadLocalRandom.current().nextInt(4000, 5500 + 1);
                
                EmailAccountBean emailAccount = new EmailAccountBean(emailAddress);
                
                while(!emailAccount.isServerOnline())
                    Thread.sleep(wakeUpInterval);
                
                if (emailAccount.getHandler().login(emailAddress)) {
                    emailAccount.setLoginState(EmailConstants.LOGIN_STATE_SUCCEDED);
                    
                    modelAccess.addAccount(emailAccount);

                    EmailFolderBean<String> emailFolderBean = new EmailFolderBean(emailAddress);
                    emailFolderBean.setGraphic(ViewFactory.defaultFactory.resolveIcon(emailAddress));
                    folderRoot.addChildren(emailFolderBean);
                    
                    FetchFolderService fetchFoldersService = new FetchFolderService(emailFolderBean, emailAccount, modelAccess);
                    fetchFoldersService.start();
                    
                }
                return emailAccount.getLoginState();
            }
        };
    }
    
}
