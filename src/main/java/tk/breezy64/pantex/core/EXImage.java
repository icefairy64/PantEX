/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import ro.fortsoft.pf4j.ExtensionPoint;

/**
 *
 * @author icefairy64
 */
public abstract class EXImage implements ExtensionPoint {
    
    public static final int THUMB_SIZE = 150;
    public static final String THUMB_FORMAT = "PNG";
    
    public List<Tag> tags;
    public Collection collection;
    public String title;
    public EXImage thumb;
    public int index;
    public long id;
    public boolean imported;

    protected EXImage(Collection collection, String title, List<Tag> tags) {
        this.tags = tags == null ? new LinkedList<>() : tags;
        this.collection = collection;
        this.title = title;
        this.id = (new Date().toString() + title).hashCode();
        this.imported = false;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj instanceof EXImage) ? false :
                ((EXImage)obj).title.equals(this.title);
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }
    
    public EXImage getThumb() throws IOException {
        if (thumb != null)
            return thumb;
        
        try (InputStream str = getImageStream()) {
            BufferedImage img = ImageIO.read(str);
            if (img == null)
                return null;
            
            BufferedImage sc = Scalr.resize(img, THUMB_SIZE);
            SavedStream buf = new SavedStream();
            ImageIO.write(sc, THUMB_FORMAT, buf.getWriter());
            buf.endWriting();
            thumb = new StreamImage(buf, null, null, buf.available(), null);
        }
        
        return thumb;
    }
    
    public abstract InputStream getImageStream() throws IOException;
    public abstract void writeImage(OutputStream out) throws IOException;
    public abstract int getImageSize() throws IOException;
    
}
