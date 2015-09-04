/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.util.LinkedHashMap;
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
        image.id = maxImgId++;
        
        if (images.containsKey(image.id)) {
            throw new RuntimeException(new CollectionException(
                    String.format("Image with ID %d is already exists in collection %s", image.id, title)));
        }
        
        images.put(image.id, image);
    }
    
    @Override
    public void removeImage(EXImage img) {
        images.remove(img.id);
    }

    @Override
    public EXImage[] getImages() {
        return images.values().toArray(new EXImage[0]);
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
