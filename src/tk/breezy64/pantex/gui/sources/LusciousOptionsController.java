/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui.sources;

import tk.breezy64.pantex.core.sources.LusciousCategory;
import tk.breezy64.pantex.core.sources.LusciousSource;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tk.breezy64.pantex.gui.SourceBrowserController;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class LusciousOptionsController implements Initializable {
    @FXML
    private ComboBox<LusciousCategory> categoryField;
    @FXML
    private VBox vbox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryField.prefWidthProperty().bind(vbox.widthProperty());
        categoryField.getItems().addAll(LusciousCategory.list);
    }    

    @FXML
    private void okClick(ActionEvent event) throws Exception {
        Scene browserScene = new Scene(FXMLLoader.load(SourceBrowserController.class.getResource("SourceBrowser.fxml")));
        browserScene.setUserData(new LusciousSource(categoryField.getSelectionModel().getSelectedItem().name));
        
        Stage browser = new Stage();
        browser.setTitle("Luscious browser");
        browser.setScene(browserScene);
        browser.show();
        
        categoryField.getScene().getWindow().hide();
        browser.toFront();
    }

    @FXML
    private void cancelClick(ActionEvent event) {
        categoryField.getScene().getWindow().hide();
    }
}
