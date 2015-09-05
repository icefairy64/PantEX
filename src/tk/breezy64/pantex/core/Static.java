/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import ro.fortsoft.pf4j.DefaultPluginManager;
import ro.fortsoft.pf4j.PluginManager;

/**
 *
 * @author icefairy64
 */
public class Static {
    
    public static PluginManager pluginManager = createPluginManager();

    public static PluginManager createPluginManager() {
        PluginManager pm = new DefaultPluginManager();
        return pm;
    }
    
}
