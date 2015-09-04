/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.sources.ImageSourceRequester;
import tk.breezy64.pantex.core.sources.LusciousSource;
import tk.breezy64.pantex.gui.Static;

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
    protected synchronized LusciousSource performRequest() {
        try {
            Scene browserScene = new Scene(FXMLLoader.load(getClass().getResource("LusciousOptions.fxml")));
            browserScene.setUserData(new Integer(0));
            Stage browser = new Stage();
            browser.setTitle("Luscious");
            browser.setScene(browserScene);
            browser.show();
            try {
                browserScene.getUserData().wait();
            }
            catch (InterruptedException e) {
            }
            return (LusciousSource)browserScene.getUserData();
        }
        catch (IOException e) {
            Static.handleException(e);
        }
        return null;
    }
    
}
