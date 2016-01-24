/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.sources.ImageSourceRequester;
import tk.breezy64.pantex.core.sources.TBIBSource;

/**
 *
 * @author icefairy64
 */
@Extension
public class TBIBBrowse extends ImageSourceRequester<TBIBSource> {

    public TBIBBrowse() {
    }

    @Override
    public String getCategory() {
        return "TBIB";
    }

    @Override
    public String getTitle() {
        return "Browse";
    }

    @Override
    protected TBIBSource performRequest() {
        return new TBIBSource();
    }
    
}
