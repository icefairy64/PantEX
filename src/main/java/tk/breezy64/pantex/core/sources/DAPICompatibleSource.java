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
public abstract class DAPICompatibleSource extends ImageSource {
    
    protected String simpleURL = getRoot() + "/index.php?page=dapi&s=post&q=index&pid=%d";
    protected String searchURL = getRoot() + "/index.php?page=dapi&s=post&q=index&tags=%s&pid=%d";
    
    protected static final Pattern imgPattern = Pattern.compile("file_url=\"(.*?)\"");
    protected static final Pattern thumbPattern = Pattern.compile("preview_url=\"(.*?)\"");
    protected static final Pattern tagsPattern = Pattern.compile("tags=\"(.*?)\"");
    
    private final String url;
    private final String proto;
    
    public DAPICompatibleSource(boolean appendProto) {
        super();
        url = simpleURL;
        proto = appendProto ? getRoot().substring(0, getRoot().indexOf(':') + 1) : "";
    }
    
    public DAPICompatibleSource(boolean appendProto, String query) {
        super();
        url = searchURL.replace("%s", URLEncoder.encode(query));
        proto = appendProto ? getRoot().substring(0, getRoot().indexOf(':') + 1) : "";
    }
    
    protected abstract String getRoot();
    
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
            
            EXImage img = new RemoteImage(proto + furl, null, Arrays.stream(tags).map((x) -> Tag.getOrCreate(x)).collect(Collectors.toList()));
            imgs.add(img);
            img.thumb = Cache.getInstance().tryFind(new RemoteImage(proto + t.group(1)));
        }
        
        cache = imgs.toArray(new EXImage[0]);
    }
    
}
