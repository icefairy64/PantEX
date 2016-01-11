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
import tk.breezy64.pantex.core.sources.KonachanSource;
import tk.breezy64.pantex.gui.MainController;

/**
 *
 * @author breezy
 */
@Extension
public class KonachanSearch extends ImageSourceRequester<KonachanSource> {

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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Konachan [search]");
        dialog.setHeaderText("Search on Konachan");
        dialog.setContentText("Tags:");
        dialog.getDialogPane().getStylesheets().add(MainController.class.getResource("/src/main/resources/css/default.css").toString());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return null;
        }
        
        return new KonachanSource(result.get());
    } 
    
}
