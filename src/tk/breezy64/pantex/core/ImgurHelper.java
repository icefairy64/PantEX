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
    
    private static final Pattern imagePattern = Pattern.compile("class=\"image\".+?href=\"(.+?)\"", Pattern.DOTALL);
    
    public static List<EXImage> getAlbumImages(String url) throws IOException {
        List<EXImage> list = new ArrayList<>();
        
        String content = Util.fetchHttpContent(url);
        Matcher m = imagePattern.matcher(content);
        while (m.find()) {
            String imgUrl = "http:" + m.group(1);
            String ext = imgUrl.substring(imgUrl.lastIndexOf("."));
            String thumbUrl = imgUrl.replace(ext, "m" + ext);
            
            EXImage img = new RemoteImage(imgUrl);
            img.thumb = new RemoteImage(thumbUrl);
            Cache.getInstance().find(img.thumb).ifPresent((x) -> img.thumb = x);
            list.add(img);
        }
        
        return list;
    }
    
}
