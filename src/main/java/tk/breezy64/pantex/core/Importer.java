/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import ro.fortsoft.pf4j.ExtensionPoint;

/**
 *
 * @author icefairy64
 */
public interface Importer extends ExtensionPoint {
    
    public String getTitle();
    public Importer onImportProgress(BiConsumer<Integer, Integer> handler);
    
    /**
     * Creates concrete import configuration based on user input
     * Note: this method should be called from UI thread
     * @return Import configuration
     */
    public Object createImportConfiguration();
    
    /**
     * Creates minimal necessary import configuration based on user input
     * Note: this method should be called from UI thread
     * @return Import configuration
     */
    public Object createMinimalImportConfiguration();
    
    public void load(Collection col, Object conf) throws IOException, ImportException;
    public void load(Collection col, CollectionImportRecord ir, Object conf) throws IOException, ImportException;
    public Importer afterImport(Consumer<Collection> handler);
    
}
