/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.sources.ImageSourceRequester;
import tk.breezy64.pantex.core.sources.KonachanSource;

/**
 *
 * @author breezy
 */
@Extension
public class KonachanBrowse extends ImageSourceRequester<KonachanSource> {

    @Override
    public String getCategory() {
        return "Konachan";
    }

    @Override
    public String getTitle() {
        return "Browse";
    }

    @Override
    protected KonachanSource performRequest() {
        return new KonachanSource();
    } 
    
}
