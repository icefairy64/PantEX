/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.SankakuComplexImage;
import tk.breezy64.pantex.core.Tag;
import tk.breezy64.pantex.core.Util;
import tk.breezy64.pantex.core.sources.ImageSource;

/**
 *
 * @author icefairy64
 */
public class SankakuComplexSource extends ImageSource {

    private static final Pattern THUMB_PATTERN = Pattern.compile("span class=\"thumb.+?a href=\"(/post/show/[\\d]+?)\".+?src=\"(.+?)\".+?title=\"(.+?)\"");
    
    private final String url;
    private final String subdomain;

    private SankakuComplexSource(String url, String subdomain) {
        this.url = url;
        this.subdomain = subdomain;
    }
    
    @Override
    protected void load(int page) throws IOException {
        String content = Util.fetchHttpContent(url.replace("%d", String.valueOf(page + 1)));
        Matcher m = THUMB_PATTERN.matcher(content);
        List<EXImage> imgs = new LinkedList<>();
        
        while (m.find()) {
            String[] tags = m.group(3).split(" ");
            EXImage img = new SankakuComplexImage(m.group(1), m.group(2), subdomain);
            img.tags.addAll(Arrays.stream(tags).map((x) -> Tag.getOrCreate(x)).collect(Collectors.toList()));
            imgs.add(img);
        }
        
        cache = imgs.toArray(new EXImage[0]);
    }

    @Override
    public String getWindowTitle() {
        return "Sankaku Complex browser";
    }
    
    // Builders
    
    public static SankakuComplexSource browseChannel() {
        return new SankakuComplexSource("https://chan.sankakucomplex.com/?page=%d", "chan");
    }
    
}
