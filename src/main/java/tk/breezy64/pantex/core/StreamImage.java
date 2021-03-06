/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *
 * @author icefairy64
 */
public class StreamImage extends EXImage {

    protected InputStream stream;
    protected int expectedSize;
    
    public StreamImage(InputStream stream, Collection collection, String title, int expectedSize, List<Tag> tags) {
        super(collection, title, tags);
        
        this.stream = stream;
        this.expectedSize = expectedSize;
    }

    @Override
    public InputStream getImageStream() throws IOException {
        stream.reset();
        return stream;
    }

    @Override
    public void writeImage(OutputStream out) throws IOException {
        stream.reset();
        Util.copy(stream, out, expectedSize);
    }

    @Override
    public int getImageSize() {
        return expectedSize;
    }
    
}
