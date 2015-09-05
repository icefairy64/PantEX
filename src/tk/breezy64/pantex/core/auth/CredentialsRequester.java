/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.auth;

import java.util.function.BiConsumer;

/**
 *
 * @author icefairy64
 */
public abstract class CredentialsRequester {
    
    private BiConsumer<String, String> finishHandler;
    
    public CredentialsRequester onFinish(BiConsumer<String, String> handler) {
        finishHandler = handler;
        return this;
    }
    
    public void request() throws AuthException {
        if (finishHandler == null) {
            throw new NullPointerException("Finish handler cannot be null");
        }
        
        String[] res = performRequest();
        if (res != null) {
            finishHandler.accept(res[0], res[1]);
        }
    }
    
    public abstract String[] performRequest() throws AuthException;
    
    public static CredentialsRequester defaultRequester;
    
}
