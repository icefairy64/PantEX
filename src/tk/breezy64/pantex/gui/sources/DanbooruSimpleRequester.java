/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.sources.DanbooruSource;
import tk.breezy64.pantex.core.sources.ImageSourceRequester;

/**
 *
 * @author icefairy64
 */
@Extension
public class DanbooruSimpleRequester extends ImageSourceRequester<DanbooruSource> {

    @Override
    public String getCategory() {
        return "Danbooru";
    }

    @Override
    public String getTitle() {
        return "Browse";
    }
    
    @Override
    protected DanbooruSource performRequest() {
        return new DanbooruSource();
    }
    
}
