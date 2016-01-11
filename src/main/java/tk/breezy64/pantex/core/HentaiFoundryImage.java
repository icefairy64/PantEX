/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author icefairy64
 */
public class HentaiFoundryImage extends EXImage implements Loadable {
    
    private static final Pattern pathPattern = Pattern.compile("pictures.user\\/(.+?)\\/(.+?)\\/(.+)");
    private static final Pattern imgPattern = Pattern.compile("class=\"imageTitle\".+?img.+?src=\"(.+?)\"", Pattern.DOTALL);

    private final String path;
    private EXImage back;
    
    public HentaiFoundryImage(String path, String thumbUrl) {
        super(null, null, null);
        this.path = path;
        
        try {
            this.thumb = Cache.getInstance().tryFind(new RemoteImage(thumbUrl));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        Matcher m = pathPattern.matcher(path);
        m.find();
        this.title = m.group(3);
        
    }

    @Override
    public void load() throws IOException {
        if (back == null) {
            String content = Util.fetchHttpContent(path);
            Matcher m = imgPattern.matcher(content);
            m.find();
            back = new RemoteImage("http:" + m.group(1));
            title = back.title;
        }
    }

    private EXImage getBack() throws IOException {
        load();
        return back;
    }
    
    @Override
    public InputStream getImageStream() throws IOException {
        return getBack().getImageStream();
    }

    @Override
    public int getImageSize() throws IOException {
        return getBack().getImageSize();
    }

    @Override
    public void writeImage(OutputStream out) throws IOException {
        getBack().writeImage(out);
    }
    
}
