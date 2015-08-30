/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.List;
import javafx.scene.image.Image;

/**
 *
 * @author icefairy64
 */
public class StreamImage extends EXImage {

    protected ImageStream stream;
    protected int expectedSize;
    
    public StreamImage(ImageStream stream, Collection collection, String title, int expectedSize, List<Tag> tags) {
        super(collection, title, tags);
        
        this.stream = stream;
        this.expectedSize = expectedSize;
    }
    
    @Override
    public Image getImage() {
        Image result = cachedImage.get();
        if (result == null) {
            /*try {
                File f = File.createTempFile("PantTX", ".png");
                f.deleteOnExit();
                FileOutputStream s = new FileOutputStream(f);
                Util.copy(stream, s);
                s.close();
                
                stream.reset();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }*/
            result = new Image(stream);
            cachedImage = new SoftReference<>(result);
        }
        return result;
    }

    @Override
    public void writeImage(OutputStream out) {
        try {
            stream.reset();
            Util.copy(stream, out, expectedSize);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getImageSize() {
        return expectedSize;
    }
    
}
