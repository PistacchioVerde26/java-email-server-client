package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author alber
 */
public class Server extends Observable implements Runnable{
    
    private ServerSocket serverSocket;
    private List<Observer> observers;
    private boolean running;
    private int port;
    private ExecutorService exec;
    
    public Server(){
        this.running = false;
        this.observers = new ArrayList<>();
    }
    
    /**
     * 
     * @param observer
     * 
     * Mantiene una lista di osservatori da passare agli altri componenti del modello
     * 
     */
    @Override
    public void addObserver(Observer observer){
        super.addObserver(observer); 
        observers.add(observer);
    }
    
    /**
     * 
     * Crea un thread pool e un avvia un SocketServer in ascolto di nuovo connessioni in ingresso.
     * Per ogni connessione in arrivo ne delega la gestione ad un nuovo thread.
     * 
     */
    @Override
    public void run(){
        try {
            if(port != 0){
                exec = Executors.newFixedThreadPool(10);
                serverSocket = new ServerSocket(port);
                setChanged();
                notifyObservers("SERVER STARTED ON PORT " + port + ". Accepting request...");
                while (running) {
                    ServerSession session;
                    session = new ServerSession(serverSocket.accept());
                    session.setObservers(observers);
                    exec.execute(session);
                }
            }
        } catch (SocketException ex) {
            setChanged();
            notifyObservers("SERVER STOPPED. Shutting down...");
        } catch (IOException ex){
            setChanged();
            notifyObservers("EXCEPTION ON SERVER SOCKET -> " + ex);
        }finally{
            exec.shutdown();
        }
    }

    //SETTERS AND GETTERS
    
    public void setPort(int port){
        this.port = port;
    }
    
    public int getPort(){
        return port;
    }
    
    public boolean getRunning(){
        return running;
    }
    
    public void setRunning(boolean running){
        this.running = running;
    }
    
    public ServerSocket getServerSocket(){
        return this.serverSocket;
    }
    
    //SETTERS AND GETTERS END
    
    public void closeConnection(){
        running = false;
        try {
            serverSocket.close();
        } catch (IOException ex) {
            setChanged();
            notifyObservers("EXCEPTION CLOSING SERVER SOCKET");
        }
    }
    
}