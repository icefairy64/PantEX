/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.sources;

import java.io.IOException;
import tk.breezy64.pantex.core.EXImage;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import ro.fortsoft.pf4j.ExtensionPoint;

/**
 *
 * @author icefairy64
 */
public abstract class ImageSource implements Iterator<EXImage[]>, ExtensionPoint {
    
    protected EXImage[] cache;
    protected boolean endReached = false;
    
    private int page = 0;
    private Consumer<Exception> exceptionHandler = null;
    
    protected abstract void load(int page) throws IOException;
    
    private void load() {
        try {
            load(page++);
        }
        catch (Exception e) {
            if (exceptionHandler != null) {
                exceptionHandler.accept(e);
            }
            else {
                System.err.println(e.getClass().toString());
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        if (cache == null && !endReached) {
            load();
        }
        
        return cache == null;
    }

    @Override
    public EXImage[] next() {
        if (cache == null && !endReached) {
            load();
        }
        
        EXImage[] result = cache;
        cache = null;
        return result;
    }
    
    public ImageSource onException(Consumer<Exception> handler) {
        this.exceptionHandler = handler;
        return this;
    }
    
}
