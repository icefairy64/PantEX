/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.sources;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.RemoteImage;
import tk.breezy64.pantex.core.Util;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import tk.breezy64.pantex.core.Cache;
import tk.breezy64.pantex.core.Tag;

/**
 *
 * @author icefairy64
 */
public class DanbooruSource extends ImageSource {
    
    private static final String root = "http://danbooru.donmai.us";
    private static final String simpleURL = "http://danbooru.donmai.us/posts?page=%d";
    private static final String searchURL = "http://danbooru.donmai.us/posts?tags=%s&page=%d";
    
    private static final Pattern imgPattern = Pattern.compile("data-file-url=\"(.*)\"");
    private static final Pattern thumbPattern = Pattern.compile("data-preview-file-url=\"(.*)\"");
    private static final Pattern tagsPattern = Pattern.compile("data-tags=\"(.*)\"");
    
    private final String url;
    
    public DanbooruSource() {
        super();
        url = simpleURL;
    }
    
    public DanbooruSource(String query) {
        super();
        url = searchURL.replace("%s", URLEncoder.encode(query));
    }
    
    @Override
    protected void load(int page) throws IOException {
        String lurl = url.replace("%d", Integer.toString(page + 1));
        String content = Util.fetchHttpContent(lurl);
        Matcher m = imgPattern.matcher(content);
        Matcher t = thumbPattern.matcher(content);
        Matcher tg = tagsPattern.matcher(content);
        
        List<EXImage> imgs = new LinkedList<>();
        
        while (m.find()) {
            t.find();
            tg.find();
            
            String furl = root + m.group(1);
            String[] tags = tg.group(1).split(" ");
            
            EXImage img = new RemoteImage(furl, null, Arrays.stream(tags).map((x) -> Tag.getOrCreate(x)).collect(Collectors.toList()));
            imgs.add(img);
            img.thumb = new RemoteImage(root + t.group(1));
            Cache.getInstance().find(img.thumb).ifPresent((x) -> img.thumb = x);
        }
        
        cache = imgs.toArray(new EXImage[0]);
    }

    @Override
    public String getWindowTitle() {
        return "Danbooru browser";
    }
    
}
