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
public class YandereSource extends DanbooruCompatibleSource {
    
    public YandereSource() {
        super();
    }
    
    public YandereSource(String query) {
        super(query);
    }

    @Override
    protected String getRoot() {
        return "http://yande.re";
    }
    
    @Override
    public String getWindowTitle() {
        return "Yande.re browser";
    }
    
}
