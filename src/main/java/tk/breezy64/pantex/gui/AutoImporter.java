/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import com.sun.javafx.collections.ObservableListWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.CollectionImportRecord;
import tk.breezy64.pantex.core.ConfigSection;
import tk.breezy64.pantex.core.Importer;
import tk.breezy64.pantex.core.Static;

/**
 *
 * @author breezy
 */
@Extension
public class AutoImporter extends ConfigSection {

    private static final Logger logger = LoggerFactory.getLogger(AutoImporter.class);
    public final static ObservableList<CollectionImportRecord> records = new ObservableListWrapper<>(new ArrayList<CollectionImportRecord>());
    
    private static AutoImporter instance;
    
    public AutoImporter() {
        if (instance != null) {
            this.priority = instance.priority;
        }
    }

    @Override
    public String getTitle() {
        return "auto-importer";
    }

    @Override
    public void load(Map<String, Object> root) {
        super.load(root);
        
        if (instance == null) {
            instance = this;
        }
        
        List<CollectionImportRecord> list = ((List<String>)root.get("list"))
                .stream()
                .map(x -> {
                    String[] d = x.split("\n");
                    return new CollectionImportRecord(d[1], d[2], d[0]);
                })
                .collect(Collectors.toList());
        
        records.addAll(list);
        
        List<Task<Collection>> tasks = new ArrayList<>();
        
        for (CollectionImportRecord r : list) {
            Task<Collection> tk = new Task<Collection>() {
                
                private FXCollection col;
                private Object conf;
                private Importer importer;

                @Override
                protected Collection call() throws Exception {
                    while (!isCancelled() && conf == null) {
                        Thread.sleep(200);
                    }

                    if (isCancelled()) {
                        return null;
                    }

                    col = new FXCollection("");
                    importer.load(col, r, conf);
                    return col;
                }

                @Override
                protected void scheduled() {
                    super.scheduled();

                    updateTitle("Importing collection " + r.title);
                    Optional<Importer> imp = Static.pluginManager.getExtensions(Importer.class)
                            .stream()
                            .filter(x -> x.getClass().getName().equals(r.importerClassName))
                            .findFirst();

                    if (!imp.isPresent()) {
                        cancel();
                        return;
                    }

                    importer = imp.get();
                    conf = importer.createMinimalImportConfiguration();
                }

                @Override
                protected void done() {
                    super.done();
                    FXCollection.dictionary.put(col.index, col);
                }
            };
            
            tasks.add(tk);
        }
        
        for (int i = 0; i < tasks.size() - 1; i++) {
            Task<Collection> tk = tasks.get(i);
            Task<Collection> next = tasks.get(i + 1);
            tk.setOnSucceeded(x -> { FXTask.run(next); x.consume(); });
            tk.setOnCancelled(x -> { FXTask.run(next); x.consume(); });
            tk.setOnFailed(x -> { FXTask.run(next); x.consume(); });
        }
        
        if (!tasks.isEmpty()) {
            FXTask.run(tasks.get(0));
        }
        else {
            FXCollection.createAndAdd("Temp");
        }
    }

    @Override
    public Map<String, Object> save() {
        Map<String, Object> map = super.save();
        map.put("list", records.stream().map(x -> x.toString()).collect(Collectors.toList()));
        return map;
    }
    
}
