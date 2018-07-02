
package model.table;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableRow;

/**
 *
 * @author alber
 * @param <T>
 * 
 * Custom RowFactory da sostiture in una TableView per cambiare lo stile
 * di una riga della tabella a run-time, in base a dei changeListener
 * 
 */
public class BoldableRowFactory<T extends AbstractTableItem> extends TableRow<T>{
    
    private final SimpleBooleanProperty bold = new SimpleBooleanProperty();
    private T currentItem = null;
    
    /**
     * Necessario per richiamare updateItem ogni volta che viene istanziata la classe, in modo da rendere il cambio dinamico
     * Viene aggiunto un listener sulla property bold e sulle proprieta dell'item corrente
     */
    public BoldableRowFactory(){
        super();
        
        /**
         * ItemProperty rappresenta i dati contenuti in questo elemento.
         * Il listener parte ogni volta che un elemento in una row cambia.
         * Dopo aver fatto un null check aggiunge un changeListener all'elemento corrente,
         * passandogli il valore della proprietà read dell'AbstractTableItem T.
         * (che in questo contesto sarà un EmailMessageBean)
         * 
         */
        itemProperty().addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observable, T olValue, T NewValue) {
                bold.unbind();//dissocia il valore osservabile a cui questa proprietà è legata
                if(NewValue != null){
                    bold.bind(NewValue.getReadProperty());
                    currentItem = NewValue;
                }
            }
        });
        
        /**
         * Ogni volta che il listener sull'itemProperty cambia il valore della proprietà bold
         * questo listener si occupa di richiamare il metodo che andrà a cambiare lo stile
         * della row in base al valore della proprietà isRead dell'AbtractTableItem T
         * 
         */
        bold.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean olValue, Boolean NewValue) {
                if(currentItem != null && currentItem == getItem()){
                    updateItem(getItem(), isEmpty());
                }
            }
        });   
    }
    
    /**
     * Override del metodo updateItem del componente JavaFX TableRow
     * 
     * @param item
     * @param empty
     * 
     */
    @Override
    final protected void updateItem(T item, boolean empty){
        super.updateItem(item, empty);//non modifico nulla del metodo padre
        if(item != null && !item.isRead()){
            setStyle("-fx-font-weight: bold");
        }else{
            setStyle("");
        }
    }
    
}
