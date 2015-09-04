/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import com.sun.javafx.collections.ObservableListWrapper;
import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.EXPack;
import tk.breezy64.pantex.core.EXPackException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    @FXML
    private ContextMenu cM;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        collectionsList.getItems().addAll(Collection.dictionary.values());
        collectionsList.getSelectionModel().select(0);
        
        imagesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        imagesList.itemsProperty().bind(Bindings.createObjectBinding(() ->
                new ObservableListWrapper<>(collectionsList.getSelectionModel().getSelectedItem().images.values().stream().collect(Collectors.toList())),
                collectionsList.getSelectionModel().selectedItemProperty(), collectionsList.getSelectionModel().getSelectedItem().images));
        imagesList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<EXImage>)(x, oV, nV) -> 
                Static.currentImage.set(nV));
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
           
            Platform.runLater(() -> { indicateProgressEnd(); collectionsList.getItems().add(col); /*Static.rebuildImageList();*/ });
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

    @FXML
    private void removeImagesClick(ActionEvent event) {
        for (EXImage img : imagesList.getSelectionModel().getSelectedItems()) {
            img.collection.removeImage(img);
        }
        
        event.consume();
    }

    @FXML
    private void moveImagesClick(ActionEvent event) throws IOException {
        ChoiceDialog<Collection> dialog = new ChoiceDialog<>(Collection.defaultCollection, Collection.dictionary.values());
        dialog.setHeaderText("Choose a collection");
        dialog.setContentText("Collection:");
        
        Optional<Collection> col = dialog.showAndWait();
        col.ifPresent((x) ->
            imagesList.getSelectionModel().getSelectedItems().stream().forEach((img) -> 
                    img.collection.moveImage(img, x)));
        
        event.consume();
    }

    @FXML
    private void exportImagesClick(ActionEvent event) {
        DirectoryChooser dialog = new DirectoryChooser();
        dialog.setTitle("Choose a directory");
        File dir = dialog.showDialog(imagesList.getScene().getWindow());
        if (dir != null) {
            imagesList.getSelectionModel().getSelectedItems().stream().forEach((img) -> {
                    try {
                        FileOutputStream s = new FileOutputStream(new File(dir, img.title));
                        img.writeImage(s);
                        s.close();
                    }
                    catch (Exception e) {
                        Static.handleException(e);
                    }
                });
        }
    }
    
}
