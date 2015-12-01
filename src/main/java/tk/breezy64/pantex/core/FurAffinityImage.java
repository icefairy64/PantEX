/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tk.breezy64.fur.DataException;
import tk.breezy64.fur.Submission;

/**
 *
 * @author icefairy64
 */
public class FurAffinityImage extends EXImage {
    
    private final Submission sub;
    
    private EXImage back;
    
    public FurAffinityImage(Submission s) {
        super(null, null, null);
        this.sub = s;
        this.thumb = new RemoteImage(s.getThumbUrl());
    }

    private void load() throws IOException {
        if (back == null) {
            try {
                sub.update();
                back = new RemoteImage(sub.getFullUrl());
                title = back.title;
            }
            catch (DataException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public int getImageSize() throws IOException {
        load();
        return back.getImageSize();
    }

    @Override
    public InputStream getImageStream() throws IOException {
        load();
        return back.getImageStream();
    }

    @Override
    public void writeImage(OutputStream out) throws IOException {
        load();
        back.writeImage(out);
    }
    
}
