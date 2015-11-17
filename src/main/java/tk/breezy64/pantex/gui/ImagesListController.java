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
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class ImagesListController implements Initializable {
    @FXML
    private ListView<FXImage> imagesList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imagesList.setItems(FXStatic.images);
        imagesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void removeItem(ActionEvent event) {
        FXImage img = imagesList.getSelectionModel().getSelectedItem();
        img.getEXImage().collection.removeImage(img.getEXImage());
    }

    @FXML
    private void selectImage(MouseEvent event) {
        event.consume();
        if (event.getClickCount() == 2) {
            FXImage img = imagesList.getSelectionModel().getSelectedItem();
            FXStatic.currentImage.set(img);
        }
    }
    
}
