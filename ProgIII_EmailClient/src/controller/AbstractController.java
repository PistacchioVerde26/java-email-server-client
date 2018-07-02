
package controller;

/**
 *
 * @author alber
 * 
 * Ogni controller dell'applicazione estende questa classe astratta
 * In questo modo ci si assicura che ogni controller ha i metodi
 * necessari per interagire con il ModelAccess
 * 
 */
public abstract class AbstractController {
           
    private ModelAccess modelAccess;
    
    public AbstractController(ModelAccess modelAccess) {
        this.modelAccess = modelAccess;
    }
	
    public ModelAccess getModelAccess(){
        return modelAccess;
    }
    
}
