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
public class Collection {
    public int id;
    public String title;
    public Map<Integer, EXImage> images;
    public int maxImgId;
    
    protected Collection(int id, String title) {
        this.id = id;
        this.title = title;
        this.images = new LinkedHashMap<>();
    }
    
    public void addImage(EXImage image) throws RuntimeException {
        image.id = maxImgId++;
        
        if (images.containsKey(image.id)) {
            throw new RuntimeException(new CollectionException(
                    String.format("Image with ID %d is already exists in collection %s", image.id, title)));
        }
        
        images.put(image.id, image);
    }
    
    public void addImages(EXImage... images) throws RuntimeException {
        for (EXImage img : images) {
            addImage(img);
        }
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
    
    public static Map<Integer, Collection> dictionary = new LinkedHashMap<>();
    private static int maxId = 0;
}
