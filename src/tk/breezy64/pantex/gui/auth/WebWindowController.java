/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.auth;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class WebWindowController implements Initializable {
    
    public static Pattern tokenPattern = Pattern.compile("access_token=([0-9a-f]+)");
    
    @FXML
    private WebView webView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        webView.getEngine().locationProperty().addListener((x, oV, nV) -> handleRelocation(nV));
        Platform.runLater(() -> { try { Thread.sleep(10); } catch (InterruptedException e) {} webView.getEngine().load((String)webView.getScene().getUserData()); });
    }
    
    private void handleRelocation(String location) {
        Matcher m = tokenPattern.matcher(location);
        if (m.find()) {
            String token = m.group(1);
            webView.getScene().setUserData(Optional.of(location));
            webView.getScene().getWindow().hide();
        }
        else if (location.contains("error=")) {
            webView.getScene().setUserData(Optional.empty());
            webView.getScene().getWindow().hide();
        }
        
    }
    
}
