/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author alber
 * 
 * ServerCOM è la classe che definisce il protocollo di comunicazione tra client e server
 * 
 */
public class ServerCOM {
    
    public static final String REQUEST_SEPARATOR = "|->";
    public static final String PARAM_SEPARATOR = "|-|-|";
    
    public static final String SERVER_ADDRESS = "127.0.0.1";
    public static final int SERVER_PORT = 5000;

    public static final int LOGIN = 2;
    public static final int LOGIN_SUCCESSFUL = 3;
    
    public static final int GET_FOLDER_LIST = 11;
    public static final int GET_FOLDER_MESSAGES = 12;
    public static final int GET_FOLDER_NEW_MESSAGES = 14;
    public static final int FOLDER_HAS_CHILDREN = 16;
    
    public static final int MESSAGE_SEND = 21;
    public static final int MESSAGE_SENT_OK = 22;
    public static final int DELETE_MESSAGE = 24;
    public static final int DELETE_MESSAGE_OK = 25;
    public static final int SET_MESSAGE_READ = 26;
    
    public static final int ERROR = -1;
    
    /**
     * 
     * @param request
     * @return 
     * 
     * Si occupa di tradurre la stringa passata come argomento in parametri
     * utilizzatibili dalla logica dell'applicazione
     * 
     */
    public static String[] parseRequest(String request){
        if(request != null){
            String[] result = request.split("(\\|->)");
            return result.length > 0 ? result : null;
        }
        return null;
    }
    
    /**
     * 
     * @param parameters
     * @return 
     * 
     * Si occupa di tradurre la stringa passata come argomento in una richiesta
     * valida utilizzabile dalla logica dell'applicazione
     * 
     */
    public static String[] parseParameters(String parameters){
        String[] result = parameters.split("(\\|-\\|-\\|)");
        return result.length > 0 ? result : null;
    }
    
    private String args;
    private int type;
    
    public ServerCOM(int type, String args){
        this.type = type;
        this.args = args;
    }
    
    public ServerCOM(int type, String[] args){
        this(type, "");
        for(String str : args){
            this.args += str+PARAM_SEPARATOR;
        }
    }
    
    @Override
    public String toString(){
        return this.type + REQUEST_SEPARATOR + args;
    }
    
}