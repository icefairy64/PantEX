/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import tk.breezy64.pantex.core.EXPack;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.Exporter;
import tk.breezy64.pantex.core.Importer;
import tk.breezy64.pantex.core.Static;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class CollectionsController implements Initializable {
    
    private static final int LOAD_THRESHOLD = 300;
    private static final int LOAD_SIZE = 12;
    
    private boolean adding = false;
    private int pos = 0;
    
    @FXML
    private ListView<FXCollection> collectionsList;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private MenuButton importButton;
    @FXML
    private MenuButton exportButton;
    @FXML
    private FlowPane imagesGrid;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label imageCountLabel;
    
    private List<ThumbBox> selection;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selection = new ArrayList<>();
        
        collectionsList.getItems().addAll(FXCollection.dictionary.values());
        collectionsList.getSelectionModel().select(0);
        
        FXCollection.dictionary.addListener((MapChangeListener<Integer, FXCollection>)(x) -> {
            Platform.runLater(() -> {
                if (x.wasRemoved()) {
                    collectionsList.getItems().remove(x.getValueRemoved());
                }
                else if (x.wasAdded()) {
                    collectionsList.getItems().add(x.getValueAdded());
                }
            });
        });
        
        collectionsList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<FXCollection>)(x, oV, nV) -> {
            pos = 0;
            adding = false;
            imageCountLabel.setVisible(true);
            imageCountLabel.setText(String.format("Images: %d", nV.images.size()));
            scrollPane.setVvalue(scrollPane.getVmax());
            imagesGrid.getChildren().clear();
        });
        
        /*imagesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        imagesList.itemsProperty().bind(Bindings.createObjectBinding(() ->
                collectionsList.getSelectionModel().getSelectedItem() == null ?
                    new ObservableListWrapper<>(new ArrayList<>()) :
                    new ObservableListWrapper<>(collectionsList.getSelectionModel().getSelectedItem().images.values().stream().collect(Collectors.toList())),
                collectionsList.getSelectionModel().selectedItemProperty(), collectionsList.getSelectionModel().getSelectedItem().images, FXStatic.images));
        imagesList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<FXImage>)(x, oV, nV) -> 
                FXStatic.currentImage.set(nV));*/
        
        imagesGrid.prefWidthProperty().bind(scrollPane.widthProperty());
        
        scrollPane.vvalueProperty().addListener((ChangeListener<Number>)(x, oV, nV) -> {
            System.out.println(nV.toString());
            FXCollection col = collectionsList.getSelectionModel().getSelectedItem();
            if (!adding && col != null && nV.doubleValue() >= scrollPane.getVmax() - (LOAD_THRESHOLD / imagesGrid.getHeight())) {
                addWhileVisible(col.getImagesReverse(), pos);
            }
        });
        
        // Filling import/export buttons
        
        importButton.getItems().addAll(Static.pluginManager.getExtensions(Importer.class).stream()
                .map((x) -> createImportItem(x)).collect(Collectors.toList()));
        
        exportButton.getItems().addAll(Static.pluginManager.getExtensions(Exporter.class).stream()
                .map((x) -> createExportItem(x)).collect(Collectors.toList()));
    }
    
    private void addWhileVisible(EXImage[] imgs, int index) {
        if (scrollPane.getVvalue() >= scrollPane.getVmax() - (LOAD_THRESHOLD / imagesGrid.getHeight()) && index < imgs.length) {
            adding = true;
            for (int i = 0; i < Integer.min(LOAD_SIZE, imgs.length - index); i++) {
                final int z = i;
                FXStatic.executor.submit(() -> {
                    addImage(imgs[index + z]);
                    pos = index + LOAD_SIZE;
                    if (z == LOAD_SIZE - 1) {
                        Platform.runLater(() -> addWhileVisible(imgs, index + LOAD_SIZE));
                    }
                });
            }
        } else {
            adding = false;
        }
    }
    
    private void addImage(EXImage img) {
        try {
            ThumbBox box = ThumbBox.create();
            box.setImage(new FXImage(img));
            ImageView view = box.getImageBox();
            view.setPreserveRatio(true);
            box.setPrefHeight(100);
            view.setCache(true);

            view.setOnMouseClicked((e) -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    if (e.isControlDown()) {
                        box.setSelected(!box.isSelected());
                    }
                    else {
                        for (ThumbBox b : selection) {
                            if (b != box)
                                b.setSelected(false);
                        }
                        box.setSelected(true);
                    }
                    
                    if (!box.isSelected() && selection.contains(box)) {
                        selection.remove(box);
                    }
                    else if (box.isSelected() && !selection.contains(box))  {
                        selection.add(box);
                    }
                } 
                
                if (e.getButton() == MouseButton.SECONDARY) {
                    FXStatic.currentImage.set(box.getImage());
                }
                e.consume();
            });

            Platform.runLater(() -> imagesGrid.getChildren().add(box));
        }
        catch (IOException e) {
            FXStatic.handleException(e);
        }
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
                                .onExportProgress((c, t) -> Platform.runLater(() -> progressIndicator.setProgress((double)c / t)))
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
        File f = ch.showSaveDialog(exportButton.getScene().getWindow());
        
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
        d.getDialogPane().getScene().getStylesheets().add(getClass().getResource("/src/main/resources/css/default.css").toString());
        d.getDialogPane().getStyleClass().add("dialog-pane");
        d.setContentText("Enter new collection name:");
        d.showAndWait().ifPresent((x) -> col.title = x);
        
        collectionsList.getItems().set(collectionsList.getSelectionModel().getSelectedIndex(), col);
        collectionsList.getSelectionModel().select(col);
    }

    @FXML
    private void closeClick(ActionEvent event) {
        progressIndicator.getScene().getWindow().hide();
    }
    
    private void indicateProgressStart() {
        progressIndicator.setProgress(0.0);
        progressIndicator.setVisible(true);
    }
    
    private void indicateProgressEnd() {
        progressIndicator.setVisible(false);
    }

    @FXML
    private void importClick(ActionEvent event) {
        event.consume();
        indicateProgressStart();
        FXStatic.executor.submit(() -> load(EXPackWrapper.getInstance()));
    }
    
    private void load(Importer imp) {
        try {
            FXCollection col = FXCollection.create("");
            imp.afterImport((x) -> Platform.runLater(() -> {
                        FXCollection.dictionary.put(col.index, col);
                        indicateProgressEnd();
                    }))
                    .onImportProgress((c, t) -> Platform.runLater(() -> progressIndicator.setProgress((double)c / t)))
                    .load(col);
        }
        catch (Exception e) {
            FXStatic.handleException(e);
        }
    }

    @FXML
    private void addClick(ActionEvent event) {
        event.consume();
        TextInputDialog d = new TextInputDialog();
        d.getDialogPane().getScene().getStylesheets().add(getClass().getResource("/src/main/resources/css/default.css").toString());
        d.getDialogPane().getStyleClass().add("dialog-pane");
        d.setContentText("Enter new collection name:");
        d.showAndWait().ifPresent((x) -> FXCollection.createAndAdd(x));
    }

    @FXML
    private void removeClick(ActionEvent event) {
        FXCollection col = collectionsList.getSelectionModel().getSelectedItem();
        if (col != null) {
            FXCollection.dictionary.remove(col.index);
        }
    }

    @FXML
    private void removeImagesClick(ActionEvent event) {
        for (ThumbBox img : selection) {
            img.getImage().getEXImage().collection.removeImage(img.getImage().getEXImage());
        }

        event.consume();
    }

    @FXML
    private void moveImagesClick(ActionEvent event) throws IOException {
        ChoiceDialog<FXCollection> dialog = new ChoiceDialog<>(FXCollection.defaultCollection, FXCollection.dictionary.values());
        dialog.setHeaderText("Choose a collection");
        dialog.setContentText("Collection:");
        
        Optional<FXCollection> col = dialog.showAndWait();
        col.ifPresent((x) -> {
            List<FXImage> l = new ArrayList<>(selection.stream().map(z -> z.getImage()).collect(Collectors.toList()));
            l.forEach((img) -> img.getEXImage().collection.moveImage(img.getEXImage(), x));
        });
        
        event.consume();
    }

    @FXML
    private void exportImagesClick(ActionEvent event) {
        DirectoryChooser dialog = new DirectoryChooser();
        dialog.setTitle("Choose a directory");
        File dir = dialog.showDialog(exportButton.getScene().getWindow());
        if (dir != null) {
            indicateProgressStart();
            final AtomicInteger i = new AtomicInteger();
            FXStatic.executor.submit(() -> {
                selection.stream().forEach((img) -> {
                    try {
                        FileOutputStream s = new FileOutputStream(new File(dir, img.getImage().getEXImage().title));
                        img.getImage().getEXImage().writeImage(s);
                        s.close();
                        Platform.runLater(() -> progressIndicator.setProgress(i.incrementAndGet() / (double)selection.size()));
                    }
                    catch (Exception e) {
                        FXStatic.handleException(e);
                    }
                });
            });
            indicateProgressEnd();
        }
    }
    
}
