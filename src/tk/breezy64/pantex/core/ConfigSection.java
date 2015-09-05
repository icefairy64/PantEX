/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.util.Map;
import ro.fortsoft.pf4j.ExtensionPoint;

/**
 *
 * @author icefairy64
 */
public abstract class ConfigSection implements ExtensionPoint {

    public ConfigSection() {
    }
    
    public abstract String getTitle();
    public abstract void load(Map<String, Object> root);
    public abstract Map<String, Object> save();
    
}
