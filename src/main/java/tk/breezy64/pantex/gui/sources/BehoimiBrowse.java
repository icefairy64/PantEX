/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.sources.BehoimiSource;
import tk.breezy64.pantex.core.sources.ImageSourceRequester;

/**
 *
 * @author icefairy64
 */
@Extension
public class BehoimiBrowse extends ImageSourceRequester<BehoimiSource> {

    @Override
    public String getCategory() {
        return "Behoimi";
    }

    @Override
    public String getTitle() {
        return "Browse";
    }

    @Override
    protected BehoimiSource performRequest() {
        return BehoimiSource.browse();
    }
    
}
