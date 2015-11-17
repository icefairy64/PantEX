/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.sources.FurAffinitySource;
import tk.breezy64.pantex.core.sources.ImageSourceRequester;

/**
 *
 * @author icefairy64
 */
@Extension
public class FurAffinityBrowse extends ImageSourceRequester<FurAffinitySource> {

    @Override
    public String getTitle() {
        return "Browse";
    }

    @Override
    public String getCategory() {
        return "FurAffinity";
    }

    @Override
    protected FurAffinitySource performRequest() {
        return new FurAffinitySource();
    }
    
}
