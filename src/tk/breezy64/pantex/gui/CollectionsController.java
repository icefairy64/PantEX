/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.EXPack;
import tk.breezy64.pantex.core.EXPackException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class CollectionsController implements Initializable {
    @FXML
    private ListView<Collection> collectionsList;
    @FXML
    private ListView<EXImage> imagesList;
    @FXML
    private ProgressIndicator progressIndicator;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        collectionsList.getItems().addAll(Collection.dictionary.values());
        collectionsList.getSelectionModel().select(0);
    }    

    @FXML
    private void exportClick(ActionEvent event) {
        event.consume();
        FileChooser ch = new FileChooser();
        File f = ch.showSaveDialog(imagesList.getScene().getWindow());
        
        indicateProgressStart();
        Static.executor.submit(() -> { 
            try { 
                EXPack.write(collectionsList.getSelectionModel().getSelectedItem(), f); 
            } 
            catch (Exception e) { 
                throw new RuntimeException(e); 
            } 
            Platform.runLater(() -> indicateProgressEnd()); 
        });
    }

    @FXML
    private void renameClick(ActionEvent event) {
        event.consume();
        Collection col = collectionsList.getSelectionModel().getSelectedItem();
        TextInputDialog d = new TextInputDialog(col.title);
        d.setContentText("Enter new collection name:");
        d.showAndWait().ifPresent((x) -> col.title = x);
        
        collectionsList.getItems().set(collectionsList.getSelectionModel().getSelectedIndex(), col);
        collectionsList.getSelectionModel().select(col);
    }

    @FXML
    private void closeClick(ActionEvent event) {
    }
    
    private void indicateProgressStart() {
        progressIndicator.setVisible(true);
    }
    
    private void indicateProgressEnd() {
        progressIndicator.setVisible(false);
    }

    @FXML
    private void importClick(ActionEvent event) {
        event.consume();
        FileChooser ch = new FileChooser();
        File f = ch.showOpenDialog(imagesList.getScene().getWindow());
        
        indicateProgressStart();
        Static.executor.submit(() -> load(f));
        
    }
    
    private void load(File f) {
        try {
            Collection col = EXPack.load(f);
           
            Platform.runLater(() -> { indicateProgressEnd(); collectionsList.getItems().add(col); Static.rebuildImageList(); });
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void addClick(ActionEvent event) {
    }

    @FXML
    private void removeClick(ActionEvent event) {
        Collection col = collectionsList.getSelectionModel().getSelectedItem();
        if (col != null) {
            Collection.dictionary.remove(col.id);
            collectionsList.getItems().remove(col);
        }
    }
    
}
