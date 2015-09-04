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
public class ImportException extends Exception {

    public ImportException(String message) {
        super(message);
    }

    public ImportException() {
    }

    public ImportException(Throwable cause) {
        super(cause);
    }

    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
