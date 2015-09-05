/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import com.sun.javafx.collections.ObservableListWrapper;
import tk.breezy64.pantex.core.SimpleCollection;
import tk.breezy64.pantex.core.EXPack;
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
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.Exporter;
import tk.breezy64.pantex.core.Importer;
import tk.breezy64.pantex.core.Static;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class CollectionsController implements Initializable {
    @FXML
    private ListView<FXCollection> collectionsList;
    @FXML
    private ListView<FXImage> imagesList;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ContextMenu cM;
    @FXML
    private MenuButton importButton;
    @FXML
    private MenuButton exportButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        collectionsList.getItems().addAll(FXCollection.dictionary.values());
        collectionsList.getSelectionModel().select(0);
        
        imagesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        imagesList.itemsProperty().bind(Bindings.createObjectBinding(() ->
                new ObservableListWrapper<>(collectionsList.getSelectionModel().getSelectedItem().images.values().stream().collect(Collectors.toList())),
                collectionsList.getSelectionModel().selectedItemProperty(), collectionsList.getSelectionModel().getSelectedItem().images));
        imagesList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<FXImage>)(x, oV, nV) -> 
                FXStatic.currentImage.set(nV));
        
        // Filling import/export buttons
        
        //importButton.getItems().add(createImportItem(EXPackWrapper.getInstance()));
        importButton.getItems().addAll(Static.pluginManager.getExtensions(Importer.class).stream()
                .map((x) -> createImportItem(x)).collect(Collectors.toList()));
        
        //exportButton.getItems().add(createExportItem(EXPackWrapper.getInstance()));
        exportButton.getItems().addAll(Static.pluginManager.getExtensions(Exporter.class).stream()
                .map((x) -> createExportItem(x)).collect(Collectors.toList()));
    }
   
    private MenuItem createImportItem(Importer x) {
        MenuItem item = new MenuItem(x.getTitle());
            item.setOnAction((ev) -> {
                indicateProgressStart();
                FXStatic.executor.submit(() -> load(x));
                ev.consume();
            });
            return item;
    }
    
    private MenuItem createExportItem(Exporter x) {
        MenuItem item = new MenuItem(x.getTitle());
            item.setOnAction((ev) -> {
                indicateProgressStart();
                FXStatic.executor.submit(() -> { 
                    try { 
                        x.afterExport((z) -> Platform.runLater(() -> indicateProgressEnd()))
                                .export(collectionsList.getSelectionModel().getSelectedItem()); 
                    } 
                    catch (Exception e) { 
                        FXStatic.handleException(e); 
                    } 
                });
                ev.consume();
            });
            return item;
    }

    private void exportClick(ActionEvent event) {
        event.consume();
        FileChooser ch = new FileChooser();
        File f = ch.showSaveDialog(imagesList.getScene().getWindow());
        
        indicateProgressStart();
        FXStatic.executor.submit(() -> { 
            try { 
                EXPack.writeCollection(collectionsList.getSelectionModel().getSelectedItem(), f); 
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
        FXCollection col = collectionsList.getSelectionModel().getSelectedItem();
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

    private void importClick(ActionEvent event) {
        event.consume();
        indicateProgressStart();
        FXStatic.executor.submit(() -> load(EXPackWrapper.getInstance()));
    }
    
    private void load(Importer imp) {
        try {
            FXCollection col = FXCollection.create("");
            imp.afterImport((x) -> Platform.runLater(() -> { indicateProgressEnd(); collectionsList.getItems().add(col); }))
                    .load(col);
        }
        catch (Exception e) {
            FXStatic.handleException(e);
        }
    }

    @FXML
    private void addClick(ActionEvent event) {
    }

    @FXML
    private void removeClick(ActionEvent event) {
        FXCollection col = collectionsList.getSelectionModel().getSelectedItem();
        if (col != null) {
            FXCollection.dictionary.remove(col.id);
            collectionsList.getItems().remove(col);
        }
    }

    @FXML
    private void removeImagesClick(ActionEvent event) {
        for (FXImage img : imagesList.getSelectionModel().getSelectedItems()) {
            img.getEXImage().collection.removeImage(img.getEXImage());
        }
        
        event.consume();
    }

    @FXML
    private void moveImagesClick(ActionEvent event) throws IOException {
        ChoiceDialog<FXCollection> dialog = new ChoiceDialog<>(FXCollection.defaultCollection, FXCollection.dictionary.values());
        dialog.setHeaderText("Choose a collection");
        dialog.setContentText("Collection:");
        
        Optional<FXCollection> col = dialog.showAndWait();
        col.ifPresent((x) ->
            imagesList.getSelectionModel().getSelectedItems().stream().forEach((img) -> 
                    img.getEXImage().collection.moveImage(img.getEXImage(), x)));
        
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
                        FileOutputStream s = new FileOutputStream(new File(dir, img.getEXImage().title));
                        img.getEXImage().writeImage(s);
                        s.close();
                    }
                    catch (Exception e) {
                        FXStatic.handleException(e);
                    }
                });
        }
    }
    
}
