/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import javafx.concurrent.Task;
import tk.breezy64.pantex.core.Importer;

/**
 *
 * @author icefairy64
 */
public class ImportTask extends Task<FXCollection> {

    private final Object descriptor;
    private final Importer importer;

    public ImportTask(Importer importer, Object descriptor) {
        super();
        this.descriptor = descriptor;
        this.importer = importer;
        updateTitle("Importing collection");
    }
    
    @Override
    protected FXCollection call() throws Exception {
        FXCollection col = FXCollection.create("");
        importer.onImportProgress((d, t) -> updateProgress(d, t))
                .load(col, descriptor);
        return col;
    }

    @Override
    protected void scheduled() {
        super.scheduled();
    }
    
}
