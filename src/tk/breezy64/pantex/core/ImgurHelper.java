/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author icefairy64
 */
public class ImgurHelper {
    
    private static final Pattern imagePattern = Pattern.compile("class=\"[a-z0-9\\-\\ ]*?image\".+?img.+?src=\"(.+?)\"", Pattern.DOTALL);
    
    public static List<EXImage> getAlbumImages(String url) throws IOException {
        List<EXImage> list = new ArrayList<>();
        
        String content = Util.fetchHttpContent(url);
        Matcher m = imagePattern.matcher(content);
        while (m.find()) {
            String hash = m.group(1).substring(m.group(1).lastIndexOf("/") + 1).substring(0, 7);
            String ext = m.group(1).substring(m.group(1).lastIndexOf("."));
            String imgUrl = "http://i.imgur.com/" + hash + ext;
            String thumbUrl = "http://i.imgur.com/" + hash + "m.jpg";
            
            EXImage img = new RemoteImage(imgUrl);
            img.thumb = new RemoteImage(thumbUrl);
            Cache.getInstance().find(img.thumb).ifPresent((x) -> img.thumb = x);
            list.add(img);
        }
        
        if (list.size() == 0) {
            System.out.println("Warning: Imgur helper probably failed to parse page " + url);
        }
        
        return list;
    }
    
}
