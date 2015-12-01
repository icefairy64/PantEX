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
public class YandereSource extends ImageSource {
    
    private static final String root = "http://yande.re";
    private static final String simpleURL = root + "/post.json?page=%d";
    private static final String searchURL = root + "/post.json?tags=%s&page=%d";
    
    private static final Pattern imgPattern = Pattern.compile("file_url\":\"(.*?)\"");
    private static final Pattern thumbPattern = Pattern.compile("preview_url\":\"(.*?)\"");
    private static final Pattern tagsPattern = Pattern.compile("tags\":\"(.*?)\"");
    
    private final String url;
    
    public YandereSource() {
        super();
        url = simpleURL;
    }
    
    public YandereSource(String query) {
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
            
            String furl = m.group(1);
            String[] tags = tg.group(1).split(" ");
            
            EXImage img = new RemoteImage(furl, null, Arrays.stream(tags).map((x) -> Tag.getOrCreate(x)).collect(Collectors.toList()));
            imgs.add(img);
            img.thumb = new RemoteImage(t.group(1));
            Cache.getInstance().find(img.thumb).ifPresent((x) -> img.thumb = x);
        }
        
        cache = imgs.toArray(new EXImage[0]);
    }

    @Override
    public String getWindowTitle() {
        return "Yande.re browser";
    }
    
}
