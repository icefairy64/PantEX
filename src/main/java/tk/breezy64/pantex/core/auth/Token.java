/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.auth;

/**
 *
 * @author icefairy64
 */
public abstract class Token {
    
    public abstract String save();
    public abstract void load(String s);
    
}
