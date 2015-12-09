/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import com.sun.javafx.collections.ObservableMapWrapper;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.EXImage;

/**
 *
 * @author icefairy64
 */
public class FXCollection extends Collection {
    
    public ObservableMap<Integer, FXImage> images;

    public FXCollection(String title) {
        super(title);
        this.images = new ObservableMapWrapper<>(new LinkedHashMap<Integer, FXImage>());
        this.images.addListener((MapChangeListener<Integer, FXImage>)(x) ->
                FXStatic.rebuildImageList());
    }

    @Override
    public void addImage(EXImage img) {
        images.put(maxImgId++, new FXImage(img));
        img.index = maxImgId - 1;
        img.collection = this;
    }

    @Override
    public void removeImage(EXImage img) {
        FXImage target = null;
        
        for (FXImage i : images.values()) {
            if (i.getEXImage() == img) {
                target = i;
                break;
            }
        }
        
        images.remove(target.getEXImage().index);
    }
    
    @Override
    public EXImage[] getImages() {
        return images.values().stream().map((x) -> x.getEXImage()).collect(Collectors.toList()).toArray(new EXImage[0]);
    }
    
    @Override
    public EXImage[] getImagesReverse() {
        List<EXImage> imgs = images.values().stream().map((x) -> x.getEXImage()).collect(Collectors.toList());
        Collections.reverse(imgs);
        return imgs.toArray(new EXImage[0]);
    }
    
    public static ObservableMap<Integer, FXCollection> initDictionary() {
        ObservableMap<Integer, FXCollection> res = new ObservableMapWrapper<>(new LinkedHashMap<>());
        res.addListener((MapChangeListener<Integer, FXCollection>)(x) -> { 
                if (x.getValueRemoved() != null)    
                    FXStatic.rebuildImageList();
            
                if (x.getValueRemoved() == defaultCollection) 
                    defaultCollection = dictionary.values().iterator().next();
            });
        
        return res;
    }
    
    /**
     * Creates a collection and adds it to dictionary
     * @param title
     * @return 
     */
    public static FXCollection createAndAdd(String title) {
        FXCollection col = new FXCollection(title);
        dictionary.put(col.index, col);
        return col;
    }
    
    public static FXCollection create(String title) {
        FXCollection col = new FXCollection(title);
        return col;
    }

    public static ObservableMap<Integer, FXCollection> dictionary = initDictionary();
    public static FXCollection defaultCollection;
    
    private static int maxImgId = 0;
    
}
