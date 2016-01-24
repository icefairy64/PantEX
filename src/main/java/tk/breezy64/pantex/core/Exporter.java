/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import ro.fortsoft.pf4j.ExtensionPoint;

/**
 *
 * @author icefairy64
 */
public interface Exporter extends ExtensionPoint {
    
    public String getTitle();
    public Exporter onExportProgress(BiConsumer<Integer, Integer> handler);
    
    /**
     * Creates concrete export configuration based on user input
     * Note: this method should be called from UI thread
     * @return Import configuration
     */
    public Object createExportConfiguration(Collection col);
    
    /**
     * Creates minimal necessary export configuration based on user input
     * Note: this method should be called from UI thread
     * @return Import configuration
     */
    public Object createMinimalExportConfiguration(Collection col);
    
    public void export(Collection col, Object conf) throws IOException, ExportException;
    public void export(Collection col, CollectionImportRecord rec, Object conf) throws IOException, ExportException;
    public Exporter afterExport(Consumer<Collection> handler);
    
}
