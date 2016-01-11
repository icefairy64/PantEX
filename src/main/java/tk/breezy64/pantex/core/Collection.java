/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

/**
 *
 * @author icefairy64
 */
public abstract class Collection {
    
    public String title;
    public CollectionImportRecord importRecord;
    public final int index;

    public Collection(String title) {
        this.title = title;
        this.index = lastID++;
    }
    
    public void addImages(EXImage... imgs) {
        for (EXImage img : imgs) {
            addImage(img);
        }
    }
    
    public void moveImage(EXImage img, Collection col) {
        removeImage(img);
        img.setCollection(col);
        col.addImage(img);
    }

    @Override
    public String toString() {
        return title;
    }
    
    public abstract EXImage[] getImages();
    public abstract EXImage[] getImagesReverse();
    public abstract void addImage(EXImage img);
    public abstract void removeImage(EXImage img);
    
    public static int lastID;
    
}
