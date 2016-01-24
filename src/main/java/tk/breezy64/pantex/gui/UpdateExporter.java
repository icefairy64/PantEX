/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.CollectionImportRecord;
import tk.breezy64.pantex.core.ExportException;
import tk.breezy64.pantex.core.Exporter;
import tk.breezy64.pantex.core.Util;

/**
 *
 * @author icefairy64
 */
@Extension
public class UpdateExporter implements Exporter {
    
    private Consumer<Collection> exportHandler;
    private BiConsumer<Integer, Integer> exportProgressHandler;
    private Exporter inst;

    @Override
    public String getTitle() {
        return "Save";
    }

    @Override
    public Exporter afterExport(Consumer<Collection> handler) {
        exportHandler = handler;
        if (inst != null) {
            inst.afterExport(handler);
        }
        return this;
    }

    @Override
    public Exporter onExportProgress(BiConsumer<Integer, Integer> handler) {
        exportProgressHandler = handler;
        if (inst != null) {
            inst.onExportProgress(handler);
        }
        return this;
    }

    @Override
    public Object createMinimalExportConfiguration(Collection col) {
        inst = Util.getExtensionClass(Exporter.class, col.importRecord.importerClassName);
        inst.afterExport(exportHandler).onExportProgress(exportProgressHandler);
        return new UpdateConfiguration(inst.createMinimalExportConfiguration(col), inst);
    }

    @Override
    public Object createExportConfiguration(Collection col) {
        return createMinimalExportConfiguration(col);
    }

    @Override
    public void export(Collection col, Object conf) throws IOException, ExportException {
        export(col, col.importRecord, conf);
    }

    @Override
    public void export(Collection col, CollectionImportRecord rec, Object conf) throws IOException, ExportException {
        UpdateConfiguration c = ((UpdateConfiguration)conf);
        c.instance.export(col, rec, c.config);
    }
    
}
