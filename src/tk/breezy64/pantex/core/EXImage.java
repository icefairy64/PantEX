/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.image.Image;

/**
 *
 * @author icefairy64
 */
public abstract class EXImage {
    public List<Tag> tags;
    public Collection collection;
    public String title;
    public String thumbURL;
    public int id;
    
    protected SoftReference<Image> cachedImage;
    
    protected EXImage(Collection collection, String title, List<Tag> tags) {
        this.tags = tags == null ? new LinkedList<>() : tags;
        this.collection = collection;
        this.title = title;
        this.cachedImage = new SoftReference<>(null);
    }

    @Override
    public String toString() {
        return title;
    }
            
    public abstract Image getImage();
    public abstract void writeImage(OutputStream out);
    public abstract int getImageSize();
    
}
