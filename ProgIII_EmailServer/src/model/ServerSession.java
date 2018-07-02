
package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author alber
 * 
 * ServerSession è responsabile della comunicazione con un singolo client.
 * Offre dei microservizi
 * 
 */
public class ServerSession implements Runnable{
    
    //private static int session_counter;
    
    //Lista degli osservatori da passare all'handler della sessione
    private List<Observer> observers;
    
    private Socket socket;
    private ServerSessionHandler handler;
    //private int sessionUID;
    
    //Per la comunicazione
    private BufferedReader input;
    private Writer out;
    
    public ServerSession(Socket socket){
        this.socket = socket;
        //session_counter++;
        //sessionUID = session_counter;
    }
    
    public void setObservers(List<Observer> observers){
        this.observers = observers;
    }
    
    private void addObservers(Observable obs){
        observers.forEach((o) -> {
            obs.addObserver(o);
        });
    }
    
    /**
     * Attende un messaggio in ingresso da un client per massimo 5 secondi.
     * Se e quando lo riceve, lo analizza e in base al tipo di richiesta passa il controllo all'handler della sessione.
     */
    @Override
    public void run(){
        try {
            
            socket.setSoTimeout(5000);
            
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            
            String in = input.readLine();
            if(in == null) return;
            String[] request = ServerCOM.parseRequest(in);
            
            //System.out.println("#" + sessionUID + " / request: " + in);
            
            if (request.length == 2) {
                handler = new ServerSessionHandler();
                addObservers(handler);
                switch (Integer.valueOf(request[0])) {
                    case ServerCOM.LOGIN:
                        handler.login(out, request[1]);
                        break;
                    case ServerCOM.GET_FOLDER_LIST:
                        handler.getFolderChildren(out, request[1]);
                        break;
                    case ServerCOM.GET_FOLDER_MESSAGES:
                        handler.getFolderMessages(out, request[1]);
                        break;
                    case ServerCOM.GET_FOLDER_NEW_MESSAGES:
                        handler.getNewMessages(out, request[1]);
                        break;
                    case ServerCOM.FOLDER_HAS_CHILDREN:
                        handler.folderHasChildren(out, request[1]);
                        break;
                    case ServerCOM.MESSAGE_SEND:
                        handler.send(out, request[1]);
                        break;
                    case ServerCOM.DELETE_MESSAGE:
                        handler.deleteMessageFromFolder(out, request[1]);
                        break;
                    case ServerCOM.SET_MESSAGE_READ:
                        handler.setMessageRead(out, request[1]);
                        break;
                }
            }

        } catch (IOException ex) {
            System.out.println("Parsing requests: " + ex);
        } finally {
            //session_counter--;
            try {
                input.close();
                out.close();
                socket.close();
            } catch (IOException ex) {
                System.out.println("Closing streams: " + ex);
            }
        }
    }
    
}