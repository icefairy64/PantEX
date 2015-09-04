/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.sources;

import java.util.function.Consumer;
import ro.fortsoft.pf4j.ExtensionPoint;

/**
 *
 * @author icefairy64
 */
public abstract class ImageSourceRequester<T extends ImageSource> implements ExtensionPoint {
    
    protected Consumer<T> finishHandler;

    public ImageSourceRequester() {
    }
    
    public ImageSourceRequester<T> onFinish(Consumer<T> handler) {
        finishHandler = handler;
        return this;
    }
    
    public void request() {
        if (finishHandler == null) {
            throw new NullPointerException("Finish handler cannot be null");
        }
        
        T result = performRequest();
        finishHandler.accept(result);
    }
    
    public T requestAndWait() {
        return performRequest();
    }

    public abstract String getCategory();
    public abstract String getTitle();
    
    protected abstract T performRequest();
    
}
