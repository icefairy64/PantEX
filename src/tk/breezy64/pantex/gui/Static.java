/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.EXImage;
import com.sun.javafx.collections.ObservableListWrapper;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;

/**
 *
 * @author icefairy64
 */
public class Static {
    
    public static FileChooser.ExtensionFilter imageExtFilter = new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif");
    public static ObservableList<EXImage> images = new ObservableListWrapper<>(new LinkedList<>());
    public static ExecutorService executor = Executors.newCachedThreadPool();
    public static ObjectProperty<EXImage> currentImage = new SimpleObjectProperty<>();
    
    public static void rebuildImageList() {
        Platform.runLater(() ->{
            images.clear();

            for (Collection col : Collection.dictionary.values()) {
                images.addAll(col.images.values());
            }
        });
    }
    
    public static void handleException(Throwable e) {
        throw new RuntimeException(e);
    }
    
}
