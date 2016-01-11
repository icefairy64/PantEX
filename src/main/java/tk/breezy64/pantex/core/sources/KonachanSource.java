/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.sources;

/**
 *
 * @author breezy
 */
public class KonachanSource extends DanbooruCompatibleSource {

    public KonachanSource() {
        super();
    }

    public KonachanSource(String query) {
        super(query);
    }

    @Override
    protected String getRoot() {
        return "http://konachan.com";
    }

    @Override
    public String getWindowTitle() {
        return "Konachan browser";
    }
    
}
