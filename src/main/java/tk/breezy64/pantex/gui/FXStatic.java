/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import com.sun.javafx.collections.ObservableListWrapper;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

/**
 *
 * @author icefairy64
 */
public class FXStatic {
    
    public static FileChooser.ExtensionFilter imageExtFilter = new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif");
    public static ObservableList<FXImage> images = new ObservableListWrapper<>(new LinkedList<>());
    public static ExecutorService executor = Executors.newCachedThreadPool();
    public static ObjectProperty<FXImage> currentImage = new SimpleObjectProperty<>();
    
    public static void rebuildImageList() {
        Platform.runLater(() ->{
            images.clear();

            for (FXCollection col : FXCollection.dictionary.values()) {
                images.addAll(col.images.values());
            }
        });
    }
    
    public static void applyCss(Pane x, String styleClass) {
        x.getScene().getStylesheets().add(FXStatic.class.getResource("/src/main/resources/css/default.css").toString());
        x.getStyleClass().add(styleClass);
    }
    
    public static void handleException(Throwable e) {
        e.printStackTrace(System.err);
        throw new RuntimeException(e);
    }
    
}
