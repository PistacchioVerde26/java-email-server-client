
package view;

import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author alber
 */
public class MainView extends JFrame implements Observer{

    private JLabel lblPort = new JLabel("port: ");
    private JTextField port = new JTextField();
    private JButton startButton = new JButton("Start server");
    private JTextArea log = new JTextArea(32,68);
    
    public MainView(){
        
        JPanel serverPanel = new JPanel();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        this.setSize(800, 600);
        
        port.setText("5000");
        log.setEditable(false);
        
        JScrollPane scroll = new JScrollPane (log, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        serverPanel.add(lblPort);
        serverPanel.add(port);
        serverPanel.add(startButton);
        serverPanel.add(scroll);

        this.add(serverPanel);
        
    }
    
    public int getPort(){
        return Integer.valueOf(port.getText());
    }
    
    public void addStartListener(ActionListener listenForStartButton){
        startButton.addActionListener(listenForStartButton);
    } 
    
    public String getStartBtnText(){
        return startButton.getText();
    }
    
    public void setStartBtnText(String newButtonText){
        startButton.setText(newButtonText);
    }
    
    public void log(String logMsg){
        log.append(logMsg + "\n");
    }
    
    @Override
    public void update(Observable o, Object arg) {
        log.append((String) arg + "\n");
    }
    
}
