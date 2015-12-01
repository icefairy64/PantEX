/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.sources.ImageSourceRequester;
import tk.breezy64.pantex.core.sources.YandereSource;

/**
 *
 * @author icefairy64
 */
@Extension
public class YandereBrowse extends ImageSourceRequester<YandereSource> {

    @Override
    public String getCategory() {
        return "Yande.re";
    }

    @Override
    public String getTitle() {
        return "Browse";
    }
    
    @Override
    protected YandereSource performRequest() {
        return new YandereSource();
    }
    
}
