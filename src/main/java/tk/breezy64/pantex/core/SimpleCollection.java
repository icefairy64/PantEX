/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author icefairy64
 */
public class SimpleCollection extends Collection {
    public int id;
    public String title;
    public Map<Integer, EXImage> images;
    public int maxImgId;
    
    protected SimpleCollection(String title) {
        super(title);
        this.images = new LinkedHashMap<>();
    }
    
    @Override
    public void addImage(EXImage image) {
        image.index = maxImgId++;
        
        if (images.containsKey(image.index)) {
            throw new RuntimeException(new CollectionException(
                    String.format("Image with ID %d is already exists in collection %s", image.index, title)));
        }
        
        images.put(image.index, image);
        image.collection = this;
    }
    
    @Override
    public void removeImage(EXImage img) {
        images.remove(img.index);
    }

    @Override
    public EXImage[] getImages() {
        return images.values().toArray(new EXImage[0]);
    }
    
    @Override
    public EXImage[] getImagesReverse() {
        List<EXImage> imgs = new ArrayList<>(images.values());
        Collections.reverse(imgs);
        return imgs.toArray(new EXImage[0]);
    }

    @Override
    public String toString() {
        return title;
    }
    
    public static Map<Integer, SimpleCollection> dictionary = new LinkedHashMap<>();
    
    public static SimpleCollection create(String title) {
        SimpleCollection col = new SimpleCollection(title);
        dictionary.put(col.id, col);
        return col;
    }
}
