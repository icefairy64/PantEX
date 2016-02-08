/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import javafx.concurrent.Task;
import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.Exporter;

/**
 *
 * @author icefairy64
 */
public class ExportTask extends Task<Void> {

    private final Object descriptor;
    private final Exporter exporter;
    private final Collection col;

    public ExportTask(Exporter exporter, Object descriptor, Collection col) {
        super();
        this.descriptor = descriptor;
        this.exporter = exporter;
        this.col = col;
        updateTitle("Exporting collection " + col.title);
    }
    
    @Override
    protected Void call() throws Exception {
        exporter.onExportProgress((d, t) -> updateProgress(d, t))
                .export(col, descriptor);
        return null;
    }

    @Override
    protected void scheduled() {
        super.scheduled();
    }
    
}
