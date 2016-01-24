/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.stage.DirectoryChooser;
import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.CollectionImportRecord;
import tk.breezy64.pantex.core.ExportException;
import tk.breezy64.pantex.core.Exporter;
import tk.breezy64.pantex.core.FileImage;
import tk.breezy64.pantex.core.ImportException;
import tk.breezy64.pantex.core.Importer;
import tk.breezy64.pantex.core.JSONPack;

/**
 *
 * @author icefairy64
 */
@Extension
public class JSONPackWrapper implements Importer, Exporter {
    
    private Consumer<Collection> importHandler;
    private Consumer<Collection> exportHandler;
    private BiConsumer<Integer, Integer> importProgressHandler;
    private BiConsumer<Integer, Integer> exportProgressHandler;
    
    @Override
    public String getTitle() {
        return "JSONPack";
    }

    @Override
    public Object createMinimalImportConfiguration() {
        return new Object();
    }

    @Override
    public Object createImportConfiguration() {
        DirectoryChooser ch = new DirectoryChooser();
        File dir = ch.showDialog(null);
        if (dir == null) {
            throw new RuntimeException("No directory selected");
        }
        return new JSONPackImportConfiguration(dir);
    }

    @Override
    public Object createMinimalExportConfiguration(Collection col) {
        return createMinimalImportConfiguration();
    }

    @Override
    public Object createExportConfiguration(Collection col) {
        return createImportConfiguration();
    }

    @Override
    public void load(Collection col, Object conf) throws IOException, ImportException {
        File dir = ((JSONPackImportConfiguration)conf).directory;
        loadInt(col, dir);
    }

    @Override
    public void load(Collection col, CollectionImportRecord ir, Object conf) throws IOException, ImportException {
        loadInt(col, new File(ir.collectionDescriptor));
    }
    
    private void loadInt(Collection col, File dir) throws IOException, ImportException {
        File f = new File(dir, "collection.json");
        File thDir = new File(dir, "thumbs");
        JSONPack.load(col, new FileReader(f), title -> {
            File x = new File(dir, title);
            FileImage img = new FileImage(x);
            x = new File(thDir, title);
            img.thumb = x.exists() ? new FileImage(x) : null;
            return img;
        }, importProgressHandler);
        col.title = dir.toPath().getFileName().toString();
        col.importRecord = new CollectionImportRecord(getClass().getName(), dir.getAbsolutePath(), col.title);
        if (importHandler != null) {
            importHandler.accept(col);
        }
    }

    private void export(Collection col, File dir) throws IOException, ExportException {
        if (dir == null) {
            throw new ExportException("No directory selected");
        }

        File thDir = new File(dir, "thumbs");
        thDir.mkdir();

        File f = new File(dir, "collection.json");
        JSONPack.write(col, new FileWriter(f), img -> {
            try {
                File x = new File(dir, img.title);
                if (!x.exists()) {
                    img.writeImage(new FileOutputStream(x));
                }
                x = new File(thDir, img.title);
                if (!x.exists()) {
                    img.getThumb().writeImage(new FileOutputStream(x));
                }
            } catch (Exception e) {
                FXStatic.handleException(e);
            }
        }, exportProgressHandler);
        if (exportHandler != null) {
            exportHandler.accept(col);
        }
    }
    
    @Override
    public void export(Collection col, Object conf) throws IOException, ExportException {
        export(col, ((JSONPackImportConfiguration) conf).directory);
    }

    @Override
    public void export(Collection col, CollectionImportRecord rec, Object conf) throws IOException, ExportException {
        export(col, new File(rec.collectionDescriptor));
    }

    @Override
    public Exporter afterExport(Consumer<Collection> handler) {
        exportHandler = handler;
        return this;
    }

    @Override
    public Importer afterImport(Consumer<Collection> handler) {
        importHandler = handler;
        return this;
    }

    @Override
    public Exporter onExportProgress(BiConsumer<Integer, Integer> handler) {
        exportProgressHandler = handler;
        return this;
    }

    @Override
    public Importer onImportProgress(BiConsumer<Integer, Integer> handler) {
        importProgressHandler = handler;
        return this;
    }
    
}
