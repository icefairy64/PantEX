/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.sources.HentaiFoundrySource;
import tk.breezy64.pantex.core.sources.ImageSourceRequester;

/**
 *
 * @author icefairy64
 */
@Extension
public class HentaiFoundryFeatured extends ImageSourceRequester<HentaiFoundrySource>{
    
    @Override
    public String getCategory() {
        return "Hentai Foundry";
    }

    @Override
    public String getTitle() {
        return "Featured";
    }
    
    @Override
    protected HentaiFoundrySource performRequest() {
        return new HentaiFoundrySource("featured");
    }
    
}
