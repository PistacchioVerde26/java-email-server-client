
package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

/**
 *
 * @author alber
 */
public final class ClientSession{
    
    private String serverIP;
    private String emailAddress;
    private int port;
    
    private BufferedReader input;
    private Writer output;
    
    private Socket connection;

    public ClientSession(String serverIP, int port, String emailAddress){
        this.serverIP = serverIP;
        this.port = port;
        this.emailAddress = emailAddress;
    }
    
    /**
     * 
     * @throws IOException 
     * 
     * Avvia una nuova connession verso il server e crea gli stream per la comunicazione
     * 
     */
    public void connect() throws IOException{
        connection = new Socket(serverIP, port);
        if(connection.isConnected()){
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF8"));
        }
    }
    /**
     * 
     * @return 
     * 
     * Controlla se il server è online
     * 
     */
    public boolean isServerOnline(){
        Socket s = null;
        try {
            s = new Socket(serverIP, port);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("Exception checking server online " + e);
                }
            }
        }
    }
    
    /**
     * 
     * @return
     * @throws IOException 
     * 
     * Crea e ritorna un nuovo gestore di connessione passandogli i canali di comunicazione con il server
     * oltre che l'indirizzo email dell'EmailAccountBean che ha originato la richiesta
     * 
     */
    public ClientSessionHandler getSessionHandler() throws IOException{
        if(isServerOnline()){
            connect();
            return new ClientSessionHandler(output, input, emailAddress);
        }
        return null;
    }
    
}
