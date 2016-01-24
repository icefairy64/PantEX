/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.sources;

/**
 *
 * @author icefairy64
 */
public class TBIBSource extends DAPICompatibleSource {

    public TBIBSource() {
        super(false);
    }

    public TBIBSource(String query) {
        super(false, query);
    }
    
    @Override
    protected String getRoot() {
        return "https://tbib.org";
    }

    @Override
    public String getWindowTitle() {
        return "TBIB Browser";
    }
    
}
