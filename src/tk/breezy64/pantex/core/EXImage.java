/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import ro.fortsoft.pf4j.ExtensionPoint;

/**
 *
 * @author icefairy64
 */
public abstract class EXImage implements ExtensionPoint {
    public List<Tag> tags;
    public Collection collection;
    public String title;
    public String thumbURL;
    public int id;

    protected EXImage(Collection collection, String title, List<Tag> tags) {
        this.tags = tags == null ? new LinkedList<>() : tags;
        this.collection = collection;
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }
    
    public abstract InputStream getImageStream() throws IOException;
    public abstract void writeImage(OutputStream out);
    public abstract int getImageSize();
    
}
