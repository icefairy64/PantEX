/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import java.io.IOException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;
import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.sources.ImageSourceRequester;
import tk.breezy64.pantex.core.sources.LusciousCategory;
import tk.breezy64.pantex.core.sources.LusciousSource;
import tk.breezy64.pantex.gui.FXStatic;
import tk.breezy64.pantex.gui.MainController;

/**
 *
 * @author icefairy64
 */
@Extension
public class LusciousRequester extends ImageSourceRequester<LusciousSource> {

    @Override
    public String getCategory() {
        return "Luscious";
    }

    @Override
    public String getTitle() {
        return "Browse";
    }

    @Override
    protected LusciousSource performRequest() {
        ChoiceDialog<LusciousCategory> chooser = new ChoiceDialog<>(LusciousCategory.list.get(0), LusciousCategory.list);
        chooser.setTitle("Luscious");
        chooser.setHeaderText("Choose a category");
        chooser.setContentText("Select:");
        chooser.getDialogPane().getStylesheets().add(MainController.class.getResource("default.css").toString());
        chooser.getDialogPane().getStyleClass().add("dialog");
        Optional<LusciousCategory> res = chooser.showAndWait();
        return res.isPresent() ? new LusciousSource(res.get().name) : null;
    }
    
}
