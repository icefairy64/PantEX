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
public class DanbooruSource extends ImageSource {
    
    private static final String root = "http://danbooru.donmai.us";
    private static final String simpleURL = "http://danbooru.donmai.us/posts?page=%d";
    
    private static final Pattern imgPattern = Pattern.compile("data-file-url=\"(.*)\"");
    private static final Pattern thumbPattern = Pattern.compile("data-preview-file-url=\"(.*)\"");
    
    @Override
    protected void load(int page) {
        String url = String.format(simpleURL, page + 1);
        String content = Util.fetch(url);
        Matcher m = imgPattern.matcher(content);
        Matcher t = thumbPattern.matcher(content);
        
        List<EXImage> imgs = new LinkedList<>();
        
        while (m.find()) {
            t.find();
            String furl = root + m.group(1);
            imgs.add(new RemoteImage(furl, null, null));
            imgs.get(imgs.size() - 1).thumbURL = root + t.group(1);
        }
        
        cache = imgs.toArray(new EXImage[0]);
    }
    
}
