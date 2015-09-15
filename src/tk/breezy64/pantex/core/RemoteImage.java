/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *
 * @author icefairy64
 */
public class RemoteImage extends EXImage {

    protected String url;
    protected FileImage local;
    
    public RemoteImage(String url) {
        this(url, null, null);
    }
    
    public RemoteImage(String url, Collection collection, List<Tag> tags) {
        super(collection, url.substring(url.lastIndexOf("/") + 1, url.length()), tags);
        
        this.url = url;
    }
    
    @Override
    public InputStream getImageStream() throws IOException {
        if (local == null) {
            load();
        }
        return local.getImageStream();
    }
    
    private void load() {
        try {
            String ext = url.substring(url.lastIndexOf("."), url.length());
            File file = File.createTempFile("PantEX", ext);
            file.deleteOnExit();

            InputStream in = Util.fetchHttpStream(url);
            FileOutputStream out = new FileOutputStream(file);
            Util.copy(in, out);
            out.close();
            in.close();

            local = new FileImage(file, collection, title, tags);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void writeImage(OutputStream out) throws IOException {
        if (local == null) {
            load();
        }
        local.writeImage(out);
    }

    @Override
    public int getImageSize() {
        if (local == null) {
            load();
        }
        return local.getImageSize();
    }
    
}
