/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

/**
 *
 * @author icefairy64
 */
public class CollectionException extends RuntimeException {
    
    public CollectionException(String s) {
        super(s);
    }
    
    public CollectionException() {
        super();
    }

    public CollectionException(Throwable cause) {
        super(cause);
    }

    public CollectionException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
