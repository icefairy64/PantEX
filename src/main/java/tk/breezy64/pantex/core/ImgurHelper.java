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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author icefairy64
 */
public class ImgurHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(ImgurHelper.class);
    
    private static final Pattern[] imagePatterns = new Pattern[] {
        Pattern.compile("data-src=\"(.+?)\""),
        Pattern.compile("twitter:image.+?content=\"(.+?)\""),
        Pattern.compile("class=\"(?:[a-z0-9\\-\\ ]*?\\ )??image(?:\\ ?[a-z0-9\\-\\ ]*?)??\".+?(?:img|a).+?(?:src|href)=\"(.+?)\"", Pattern.DOTALL),
        Pattern.compile("class=\"[^\"]+?post-image.+?img.+?src=\"(.+?)\"", Pattern.DOTALL),
        Pattern.compile("property=\"og:image\".+?content=\"(.+?)\"")
    };
    
    public static List<EXImage> getAlbumImages(String url) throws IOException {
        List<EXImage> list = new ArrayList<>();
        List<String> hashes = new ArrayList<>();
        
        String content = Util.fetchHttpContent(url);
        int i = 0;
        while (i < imagePatterns.length) {
            Matcher m = imagePatterns[i++].matcher(content);
            while (m.find()) {
                String match = m.group(1);
                String filename = match.substring(match.lastIndexOf("/") + 1);

                if ((!match.contains("i.imgur") && !match.contains("imgur2_")) 
                        || filename.length() < 7 || !filename.contains(".")) {
                    continue;
                }

                String hash = filename.substring(0, 7) + filename.substring(filename.lastIndexOf("."));
                if (!hashes.stream().anyMatch((x) -> x.substring(0, 7).equals(hash.substring(0, 7)))) {
                    hashes.add(hash);
                }
            }
        }
        
        for (String hash : hashes) {
            String ext = hash.substring(hash.lastIndexOf("."));
            String imgUrl = "http://i.imgur.com/" + hash;
            String thumbUrl = "http://i.imgur.com/" + hash.replace(ext, "m" + ext);

            EXImage img = new RemoteImage(imgUrl);
            img.thumb = Cache.getInstance().tryFind(new RemoteImage(thumbUrl));
            list.add(img);
        }
        
        if (list.isEmpty()) {
            logger.warn("Warning: Imgur helper probably failed to parse page " + url);
        }
        
        return list;
    }
    
}
