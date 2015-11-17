/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class CollectionSelectorController implements Initializable {
    @FXML
    private ComboBox<FXCollection> collectionSelector;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        collectionSelector.getItems().addAll(FXCollection.dictionary.values());
    }    

    @FXML
    private void okClick(ActionEvent event) {
        event.consume();
        okButton.getScene().setUserData(collectionSelector.getSelectionModel().getSelectedItem());
        okButton.getScene().getWindow().hide();
    }

    @FXML
    private void cancelClick(ActionEvent event) {
        event.consume();
        cancelButton.getScene().getWindow().hide();
    }
    
}
