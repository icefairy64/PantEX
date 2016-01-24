/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import tk.breezy64.pantex.core.Exporter;

/**
 *
 * @author icefairy64
 */
public class UpdateConfiguration {
    
    public Object config;
    public Exporter instance;

    public UpdateConfiguration(Exporter instance) {
        this.instance = instance;
    }
    
    public UpdateConfiguration(Object config, Exporter instance) {
        this.config = config;
        this.instance = instance;
    }
    
}
