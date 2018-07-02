
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import model.Server;
import view.MainView;

/**
 *
 * @author alber
 */
public class MainController {
        
    private MainView view;
    private Server model;
    
    private Thread serverThread;
    
    public MainController(MainView view, Server model){
        
        this.view = view;
        this.model = model;
        
        model.addObserver(view);
        
        view.addStartListener(new StartServerListener());
        
    }
    
    /**
     * Listener per la view.
     * 
     */
    class StartServerListener implements ActionListener{
        
        /**
         * 
         * @param e 
         * 
         * Si occupa di avviare e stoppare l'esecuzione del server su un nuovo thread
         * 
         */
        @Override
	public void actionPerformed(ActionEvent e) { 
            
            String buttonState = view.getStartBtnText();
            
            System.out.println(model.getRunning());
            if(!model.getRunning()){
                if(buttonState.equals("Start server")) view.setStartBtnText("Stop server");
                int port = view.getPort();
                model.setPort(port);
                model.setRunning(true);
                serverThread = new Thread(model, "ServerThread");
                serverThread.start();
            }else if(serverThread != null && model.getRunning()){
                if(buttonState.equals("Stop server")) view.setStartBtnText("Start server");
                model.closeConnection();
                try {
                    serverThread.join();
                } catch (InterruptedException ex) {
                    view.log("EXCEPTION -> " + ex);
                }
            }
            
        }
    }
    
}