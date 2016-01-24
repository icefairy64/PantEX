/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.sources;

import java.util.regex.Pattern;

/**
 *
 * @author icefairy64
 */
public class BehoimiSource extends DanbooruCompatibleSource {

    public BehoimiSource() {
        imgPattern = Pattern.compile("source\":\"(.*?)\"");
    }
    
    @Override
    protected String getRoot() {
        return "http://behoimi.org";
    }

    @Override
    public String getWindowTitle() {
        return "Behoimi source";
    }
    
    // Creators
    
    public static BehoimiSource browse() {
        BehoimiSource res = new BehoimiSource();
        res.url = res.getRoot() + "/post/index.json?page=%d";
        return res;
    }
    
    public static BehoimiSource search(String tags) {
        BehoimiSource res = new BehoimiSource();
        res.url = res.getRoot() + "/post/index.json?tags=" + tags + "&page=%d";
        return res;
    }
    
}
