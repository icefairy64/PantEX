/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tk.breezy64.pantex.core.Static;

/**
 *
 * @author icefairy64
 */
public class AuthManager {
    
    public static AuthManager instance;
    
    private final Map<Class<?>, Authorizer> authorizers;
    
    public AuthManager() {
        List<Authorizer> x = Static.pluginManager.getExtensions(Authorizer.class);
        authorizers = new HashMap<>();
        
        for (Authorizer<?> auth : x) {
            authorizers.put(auth.getClass(), auth);
        }
    }
    
    public Authorizer<?> get(Class<?> type) {
        return authorizers.get(type);
    }
    
    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        
        return instance;
    }
    
}
