/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.auth;

import java.util.Optional;
import ro.fortsoft.pf4j.ExtensionPoint;

/**
 *
 * @author icefairy64
 */
public abstract class Authorizer<T extends Token> implements ExtensionPoint {
    
    protected T token;
    
    public Optional<T> getToken() {
        return token != null ? Optional.of(token) : Optional.empty();
    }
    
    public boolean isAuthorized() {
        return token != null;
    }
    
    public T getOrCreateToken() throws AuthException {
        return token != null ? token : auth();
    }
    
    public boolean testAndSetToken(T token) {
        if (testToken(token)) {
            this.token = token;
            return true;
        }
        return false;
    }
    
    public T auth() throws AuthException {
        if (token == null) {
            token = performAuth();
        }
        return token;
    }
    
    public abstract String getTitle();
    public abstract T performAuth() throws AuthException;
    public abstract boolean testToken(T token);
    
}
