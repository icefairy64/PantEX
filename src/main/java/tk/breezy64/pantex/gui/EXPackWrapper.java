/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.CollectionImportRecord;
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
    private BiConsumer<Integer, Integer> importProgressHandler;
    private BiConsumer<Integer, Integer> exportProgressHandler;
    
    @Override
    public String getTitle() {
        return "EXPack";
    }

    @Override
    public Object createImportConfiguration() {
        FileChooser ch = new FileChooser();
        File f = ch.showOpenDialog(null);
        return new EXPackImportConfiguration(f);
    }

    @Override
    public Object createMinimalImportConfiguration() {
        return new Object();
    }

    @Override
    public Object createExportConfiguration(Collection col) {
        FileChooser ch = new FileChooser();
        File f = ch.showSaveDialog(null);
        return new EXPackImportConfiguration(f);
    }

    @Override
    public Object createMinimalExportConfiguration(Collection col) {
        return createMinimalImportConfiguration();
    }

    @Override
    public void load(Collection col, Object conf) throws IOException, ImportException {
        EXPackImportConfiguration c = (EXPackImportConfiguration)conf;
        if (c.file != null) {
            EXPack.getInstance().load(c.file, col, importProgressHandler);
            if (importHandler != null) {
                importHandler.accept(col);
            }
        }
    }

    @Override
    public void load(Collection col, CollectionImportRecord ir, Object conf) throws IOException, ImportException {
        File f = new File(ir.collectionDescriptor);
        EXPack.getInstance().load(f, col, importProgressHandler);
        if (importHandler != null) {
            importHandler.accept(col);
        }
    }

    @Override
    public void export(Collection col, Object conf) throws IOException, ExportException {
        File f = ((EXPackImportConfiguration)conf).file;
        if (f != null) {
            EXPack.getInstance().write(col, f, exportProgressHandler);
            if (exportHandler != null) {
                exportHandler.accept(col);
            }
        }
    }

    @Override
    public void export(Collection col, CollectionImportRecord rec, Object conf) throws IOException, ExportException {
        File f = new File(rec.collectionDescriptor);
        EXPack.getInstance().write(col, f, exportProgressHandler);
        if (exportHandler != null) {
            exportHandler.accept(col);
        }
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

    @Override
    public Importer onImportProgress(BiConsumer<Integer, Integer> handler) {
        importProgressHandler = handler;
        return this;
    }

    @Override
    public Exporter onExportProgress(BiConsumer<Integer, Integer> handler) {
        exportProgressHandler = handler;
        return this;
    }
    
    // FXStatic
    
    private final static EXPackWrapper instance = new EXPackWrapper();

    public static EXPackWrapper getInstance() {
        return instance;
    }
    
}
