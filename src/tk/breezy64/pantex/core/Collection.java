/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import com.sun.javafx.collections.ObservableMapWrapper;
import java.util.LinkedHashMap;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import tk.breezy64.pantex.gui.Static;

/**
 *
 * @author icefairy64
 */
public class Collection {
    public int id;
    public String title;
    public ObservableMap<Integer, EXImage> images;
    public int maxImgId;
    
    protected Collection(int id, String title) {
        this.id = id;
        this.title = title;
        this.images = new ObservableMapWrapper<>(new LinkedHashMap<>());
        this.images.addListener((MapChangeListener<Integer, EXImage>)(x) ->
                Static.rebuildImageList());
    }
    
    public void addImage(EXImage image) {
        image.id = maxImgId++;
        
        if (images.containsKey(image.id)) {
            throw new RuntimeException(new CollectionException(
                    String.format("Image with ID %d is already exists in collection %s", image.id, title)));
        }
        
        images.put(image.id, image);
    }
    
    public void addImages(EXImage... images) {
        for (EXImage img : images) {
            addImage(img);
        }
    }
    
    public void removeImage(EXImage img) {
        images.remove(img);
    }
    
    public void moveImage(EXImage img, Collection col) {
        images.remove(img);
        img.setCollection(col);
        col.addImage(img);
    }

    @Override
    public String toString() {
        return title;
    }
    
    public static Collection create(String title) {
        Collection res = new Collection(maxId++, title);
        dictionary.put(maxId - 1, res);
        return res;
    }
    
    public static ObservableMap<Integer, Collection> initDictionary() {
        ObservableMap<Integer, Collection> res = new ObservableMapWrapper<>(new LinkedHashMap<>());
        res.addListener((MapChangeListener<Integer, Collection>)(x) -> { 
                Static.rebuildImageList();
            
                if (x.getValueRemoved() == defaultCollection) 
                    defaultCollection = dictionary.values().iterator().next();
            });
        
        return res;
    }
    
    public static ObservableMap<Integer, Collection> dictionary = initDictionary();
    public static Collection defaultCollection;
    private static int maxId = 0;
}
