/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.HentaiFoundryImage;
import tk.breezy64.pantex.core.Util;

/**
 *
 * @author icefairy64
 */
public class HentaiFoundrySource extends ImageSource {

    private static final String initUrl = "http://www.hentai-foundry.com/?enterAgree=1&size=0";
    private static final String browseUrl = "http://www.hentai-foundry.com/pictures/%s/page/%d";
    private static final String noPageUrl = "http://www.hentai-foundry.com/pictures/%s";
    
    private static final Pattern imgPattern = Pattern.compile("class=\"thumbTitle\".+?a href=\"(.+?)\".+?img class=\"thumb\".+?src=\"(.+?)\"");
    private static final Pattern pathPattern = Pattern.compile("pictures.user\\/(.+?)\\/(.+?)\\/(.+)");
    
    private boolean inited = false;
    private final String url;
    
    public HentaiFoundrySource(String category) {
        url = browseUrl.replace("%s", category);
    }
    
    private HentaiFoundrySource(String url, boolean flag) {
        this.url = url;
    }
    
    public static HentaiFoundrySource random() {
        return new HentaiFoundrySource(noPageUrl.replace("%s", "random"), false);
    }
    
    @Override
    protected void load(int page) throws IOException {
        if (!inited) {
            Util.fetchHttpContent(initUrl);
            inited = true;
        }
        
        String content = Util.fetchHttpContent(url.replace("%d", Integer.toString(page + 1)));
        Matcher m = imgPattern.matcher(content);
        List<EXImage> imgs = new ArrayList<>();
        
        while (m.find()) {
            String thumbUrl = m.group(2).replace("&amp;", "&");
            thumbUrl = thumbUrl.substring(0, thumbUrl.indexOf("size")) + "size=256&shadow=0";
            imgs.add(new HentaiFoundryImage("http://www.hentai-foundry.com" + m.group(1), "http:" + thumbUrl));
        }
        
        cache = imgs.toArray(new EXImage[0]);
    }
    
}
