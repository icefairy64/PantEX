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
public class SankakuComplexImage extends EXImage implements Loadable {

    private static final Pattern IMAGE_PATTERN = Pattern.compile("<li>Original.+?a href=\"(.+?)\"");
    
    private RemoteImage back;
    private final String pageUrl;

    public SankakuComplexImage(String relativeUrl, String thumbUrl, String subdomain) {
        super(null, null, null);
        pageUrl = "https://" + subdomain + ".sankakucomplex.com" + relativeUrl;
        thumb = new RemoteImage("https:" + thumbUrl);
        title = thumbUrl.substring(thumbUrl.lastIndexOf('/'), thumbUrl.length());
    }
    
    @Override
    public void load() throws IOException {
        if (back == null) {
            String content = Util.fetchHttpContent(pageUrl);
            Matcher m = IMAGE_PATTERN.matcher(content);
            m.find();
            back = new RemoteImage("https:" + m.group(1));
        } 
    }
    
    private EXImage getBack() throws IOException {
        load();
        return back;
    }
    
    @Override
    public int getImageSize() throws IOException {
        return getBack().getImageSize();
    }

    @Override
    public InputStream getImageStream() throws IOException {
        return getBack().getImageStream();
    }

    @Override
    public void writeImage(OutputStream out) throws IOException {
        getBack().writeImage(out);
    }
    
}
