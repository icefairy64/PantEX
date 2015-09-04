/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.EXPack;
import tk.breezy64.pantex.core.ExportException;
import tk.breezy64.pantex.core.Exporter;
import tk.breezy64.pantex.core.ImportException;
import tk.breezy64.pantex.core.Importer;

/**
 *
 * @author icefairy64
 */
@Extension
public class EXPackWrapper implements Importer, Exporter {
    
    private Consumer<Collection> importHandler;
    private Consumer<Collection> exportHandler;
    
    @Override
    public String getTitle() {
        return "EXPack";
    }

    @Override
    public void load(Collection col) throws IOException, ImportException {
        FileChooser ch = new FileChooser();
        Platform.runLater(() -> {
            File f = ch.showOpenDialog(null);
            Static.executor.submit(() -> {
                if (f != null) {
                    try {
                        EXPack.loadCollection(f, col);
                        if (importHandler != null) {
                            importHandler.accept(col);
                        }
                    }
                    catch (Exception e) {
                        Static.handleException(e);
                    }
                }
            });
        });
    }

    @Override
    public void export(Collection col) throws IOException, ExportException {
        FileChooser ch = new FileChooser();
        Platform.runLater(() -> {
            File f = ch.showSaveDialog(null);
            Static.executor.submit(() -> {
                if (f != null) {
                    try {
                        EXPack.writeCollection(col, f);
                        if (exportHandler != null) {
                            exportHandler.accept(col);
                        }
                    }
                    catch (Exception e) {
                        Static.handleException(e);
                    }
                }
            });
        });
    }
    
    @Override
    public Importer afterImport(Consumer<Collection> handler) {
        importHandler = handler;
        return this;
    }

    @Override
    public Exporter afterExport(Consumer<Collection> handler) {
        exportHandler = handler;
        return this;
    }
    
    // Static
    
    private final static EXPackWrapper instance = new EXPackWrapper();

    public static EXPackWrapper getInstance() {
        return instance;
    }
    
}
