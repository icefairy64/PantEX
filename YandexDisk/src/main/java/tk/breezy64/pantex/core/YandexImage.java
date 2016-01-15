/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.json.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import tk.breezy64.pantex.core.auth.YandexToken;
import tk.breezy64.pantex.gui.FXStatic;
import tk.breezy64.pantex.plugin.YandexDiskPlugin;

/**
 *
 * @author icefairy64
 */
public class YandexImage extends EXImage {

    private final String path;
    private final YandexToken token;
    private final RestClient client;
    private Optional<RemoteImage> inner = Optional.empty();
    
    public YandexImage(String path, YandexToken token, Collection collection, String title, List<Tag> tags, RestClient client) {
        super(collection, title, tags);
        this.path = path;
        this.token = token;
        this.client = client;
    }
    
    private void load() {
        try {
            inner = Optional.of(new RemoteImage(YandexDiskPlugin.YandexDiskLoader.getFileUrl(path, token), collection, tags));
        }
        catch (Exception e) {
            FXStatic.handleException(e);
        }
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
    public void writeImage(OutputStream out) throws IOException {
        if (!inner.isPresent()) {
            load();
        }
        inner.get().writeImage(out);
    }

    @Override
    public EXImage getThumb() throws IOException {
        if (thumb != null)
            return thumb;
        
        try {
            ResourcesArgs args = new ResourcesArgs.Builder()
                    .setPreviewSize("S")
                    .setPath(path)
                    .build();
            
            Resource res = client.getResources(args);
            RemoteImage tmp = new RemoteImage(res.getPreview());
            tmp.addHeader("Authorization", "OAuth " + token.getToken());
            thumb = tmp;
            return thumb;
        }
        catch (Exception e) {
            return super.getThumb();
        }
    }
    
}
