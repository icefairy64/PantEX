/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import java.util.Optional;
import javafx.scene.control.TextInputDialog;
import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.sources.DanbooruSource;
import tk.breezy64.pantex.core.sources.ImageSourceRequester;
import tk.breezy64.pantex.gui.MainController;

/**
 *
 * @author icefairy64
 */
@Extension
public class DanbooruSearchRequester extends ImageSourceRequester<DanbooruSource> {

    @Override
    public String getCategory() {
        return "Danbooru";
    }

    @Override
    public String getTitle() {
        return "Search";
    }
    
    @Override
    protected DanbooruSource performRequest() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Danbooru [search]");
        dialog.setHeaderText("Search on Danbooru");
        dialog.setContentText("Tags:");
        dialog.getDialogPane().getStylesheets().add(MainController.class.getResource("default.css").toString());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return null;
        }
        
        return new DanbooruSource(result.get());
    }
    
}
