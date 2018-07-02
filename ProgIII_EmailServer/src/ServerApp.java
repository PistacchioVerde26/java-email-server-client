
import controller.MainController;
import model.Server;
import view.MainView;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alber
 */
public class ServerApp {

    public static void main(String[] args){
        
        MainView view = new MainView();
        Server model = new Server();
        new MainController(view, model);
        
        view.setVisible(true);
        
    }
    
}
