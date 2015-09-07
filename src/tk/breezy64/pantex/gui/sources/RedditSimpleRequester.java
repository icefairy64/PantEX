/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import java.util.Optional;
import javafx.scene.control.TextInputDialog;
import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.sources.ImageSourceRequester;
import tk.breezy64.pantex.core.sources.RedditSource;

/**
 *
 * @author icefairy64
 */
@Extension
public class RedditSimpleRequester extends ImageSourceRequester<RedditSource>{

    @Override
    public String getCategory() {
        return "Reddit";
    }
    
    @Override
    public String getTitle() {
        return "Simple (new)";
    }

    @Override
    protected RedditSource performRequest() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Reddit");
        d.setHeaderText("Subreddit chooser");
        d.setContentText("Enter a subreddit:");
        Optional<String> res = d.showAndWait();
        if (res.isPresent()) {
            return new RedditSource(res.get());
        }
        return null;
    }
    
}
