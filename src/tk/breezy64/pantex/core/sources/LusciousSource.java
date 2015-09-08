/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.sources;

import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.RemoteImage;
import tk.breezy64.pantex.core.Util;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author icefairy64
 */
public class LusciousSource extends ImageSource {
    
    private static final String root = "http://luscious.net";
    private static final String simpleURL = "http://luscious.net/c/%s/pictures/page/%d/?style=thumbnails";
    
    private final String url;
    
    private static final Pattern thumbPattern = Pattern.compile("a href=\"//luscious\\.net/c/.+?img src=\"(.+?)\"", Pattern.DOTALL);
    
    public LusciousSource(String category) {
        url = simpleURL.replaceFirst("%s", category);
    }
    
    @Override
    protected void load(int page) {
        String lurl = String.format(url, page + 1);
        String content = Util.fetchHttpContent(lurl);
        Matcher t = thumbPattern.matcher(content);
        
        List<EXImage> imgs = new LinkedList<>();
        
        while (t.find()) {
            String furl = "http://" + t.group(1).substring(2);
            String ext = furl.substring(furl.lastIndexOf(".") + 1, furl.length());
            String iurl = furl.replaceFirst("\\.\\d.+?x\\d\\..*", "." + ext);
            imgs.add(new RemoteImage(iurl, null, null));
            imgs.get(imgs.size() - 1).thumbURL = furl;
        }
        
        cache = imgs.toArray(new EXImage[0]);
    }
    
}
