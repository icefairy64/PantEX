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
import java.util.Optional;
import tk.breezy64.pantex.core.auth.YandexToken;
import tk.breezy64.pantex.plugin.YandexDiskPlugin;

/**
 *
 * @author icefairy64
 */
public class YandexImage extends EXImage {

    private final String path;
    private final YandexToken token;
    private Optional<RemoteImage> inner = Optional.empty();
    
    public YandexImage(String path, YandexToken token, Collection collection, String title, List<Tag> tags) {
        super(collection, title, tags);
        this.path = path;
        this.token = token;
    }
    
    private void load() {
        inner = Optional.of(new RemoteImage(YandexDiskPlugin.YandexDiskLoader.getFileUrl(path, token), collection, tags));
    }
    
    @Override
    public InputStream getImageStream() throws IOException {
        if (!inner.isPresent()) {
            load();
        }
        return inner.get().getImageStream();
    }

    @Override
    public int getImageSize() {
        if (!inner.isPresent()) {
            load();
        }
        return inner.get().getImageSize();
    }

    @Override
    public void writeImage(OutputStream out) {
        if (!inner.isPresent()) {
            load();
        }
        inner.get().writeImage(out);
    }
    
}
