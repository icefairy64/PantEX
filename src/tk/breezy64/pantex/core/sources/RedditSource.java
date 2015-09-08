/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.sources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.RemoteImage;
import tk.breezy64.pantex.core.Util;

/**
 *
 * @author icefairy64
 */
public class RedditSource extends ImageSource {

    private final static String simpleUrl = "https://www.reddit.com/r/%s/new/"; 
    private final static String moreUrl = "https://www.reddit.com/r/%s/new/?count=25&after=%s";
    
    private final static Pattern postPattern = Pattern.compile("div class=.+?data\\-fullname=\"(.+?)\"");
    private final static Pattern postFilePattern = Pattern.compile("a class=\"thumbnail.+?href=\"(.+?)\".+?\\/a", Pattern.DOTALL);
    private final static Pattern postThumbPattern = Pattern.compile("img.+?src=\"(.+?)\"");
    
    private final String subreddit;
    private Optional<String> lastPostId = Optional.empty();
    private String url;
    
    public RedditSource(String subreddit) {
        this.subreddit = subreddit;
        url = simpleUrl.replace("%s", subreddit);
    }
    
    private boolean checkExtension(String s) {
        return s.endsWith(".jpg") 
                || s.endsWith(".jpeg")
                || s.endsWith(".png")
                || s.endsWith(".gif");
    }
    
    @Override
    protected void load(int page) {
        String content = Util.fetchHttpContent(url);
        Matcher p = postFilePattern.matcher(content);
        List<EXImage> list = new ArrayList<>();
        while (p.find()) {
            String file = p.group(1);
            String inner = p.group(2);
            String thumb = null;
            Matcher i = postThumbPattern.matcher(inner);
            if (p.find()) {
                thumb = p.group(1);
            }
            
            if (checkExtension(file)) {
                EXImage img = new RemoteImage(file, null, null);
                list.add(img);
                img.thumbURL = thumb != null ? thumb : file;
            }
        }
        
        p = postPattern.matcher(content);
        while (p.find()) {
            lastPostId = Optional.of(p.group(1));
        }
        lastPostId.ifPresent((x) -> url = moreUrl.replaceFirst("%s", subreddit).replace("%s", x));
        
        cache = list.toArray(new EXImage[0]);
    }
    
}
