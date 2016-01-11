/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.util.HashMap;
import java.util.Map;
import ro.fortsoft.pf4j.ExtensionPoint;

/**
 *
 * @author icefairy64
 */
public abstract class ConfigSection implements ExtensionPoint {

    protected int priority;
    
    public ConfigSection() {
    }
    
    public abstract String getTitle();
    
    public void load(Map<String, Object> root) {
        priority = 0;
        if (root.containsKey("priority")) {
            priority = (Integer)root.get("priority");
        }
    }
    
    public Map<String, Object> save() {
        Map<String, Object> map = new HashMap<>();
        map.put("priority", (Integer)priority);
        return map;
    }
    
}
