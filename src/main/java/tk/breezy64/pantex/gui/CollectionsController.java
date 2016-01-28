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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.Exporter;
import tk.breezy64.pantex.core.Importer;
import tk.breezy64.pantex.core.Static;
import tk.breezy64.pantex.core.Util;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class CollectionsController implements Initializable {
    
    private static final Logger logger = LoggerFactory.getLogger(CollectionsController.class);
    private static final int LOAD_THRESHOLD = 300;
    private static final int LOAD_SIZE = 12;
    
    private ThumbContainerManager containerManager;
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
        containerManager = new ThumbContainerManager(imagesGrid);
        
        collectionsList.getItems().addAll(FXCollection.dictionary.values());
        
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
            containerManager.reset();
        });
        
        imagesGrid.prefWidthProperty().bind(scrollPane.widthProperty());
        
        scrollPane.vvalueProperty().addListener((ChangeListener<Number>)(x, oV, nV) -> {
            FXCollection col = collectionsList.getSelectionModel().getSelectedItem();
            if (!adding && col != null && nV.doubleValue() >= scrollPane.getVmax() - (LOAD_THRESHOLD / imagesGrid.getHeight())) {
                addWhileVisible(col.getImagesReverse(), pos);
            }
        });
        
        // Filling import/export buttons
        
        importButton.getItems().addAll(Static.pluginManager.getExtensions(Importer.class).stream()
                .map((x) -> createImportItem(x))
                .collect(Collectors.toList()));
        
        exportButton.getItems().addAll(Static.pluginManager.getExtensions(Exporter.class).stream()
                .map((x) -> createExportItem(x))
                .collect(Collectors.toList()));
        
        // Setting collection
        
        collectionsList.getSelectionModel().select(0);
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

            Platform.runLater(() -> containerManager.add(box));
        }
        catch (IOException e) {
            FXStatic.handleException(e);
        }
    }    
    
    private MenuItem createImportItem(Importer x) {
        MenuItem item = new MenuItem(x.getTitle());
            item.setOnAction((ev) -> {
                Object conf = x.createImportConfiguration();
                ImportTask t = new ImportTask(x, conf);
                progressIndicator.progressProperty().bind(t.progressProperty());
                t.setOnSucceeded((z) -> {
                    progressIndicator.progressProperty().unbind();
                    indicateProgressEnd();
                    FXCollection.dictionary.put(t.getValue().index, t.getValue());
                    z.consume();
                });
                FXTask.schedule(t);
                indicateProgressStart();
                ev.consume();
            });
            return item;
    }
    
    private MenuItem createExportItem(Exporter x) {
        MenuItem item = new MenuItem(x.getTitle());
        item.setOnAction((ev) -> {
            Collection col = collectionsList.getSelectionModel().getSelectedItem();
            Object conf = x.createExportConfiguration(col);
            if (conf != null) {
                ExportTask t = new ExportTask(x, conf, col);
                progressIndicator.progressProperty().bind(t.progressProperty());
                t.setOnSucceeded((z) -> {
                    progressIndicator.progressProperty().unbind();
                    indicateProgressEnd();
                    z.consume();
                });
                FXTask.schedule(t);
                indicateProgressStart();
            }
            ev.consume();
        });
        return item;
    }

    @FXML
    private void renameClick(ActionEvent event) {
        event.consume();
        FXCollection col = collectionsList.getSelectionModel().getSelectedItem();
        TextInputDialog d = new TextInputDialog(col.title);
        FXStatic.applyCss(d.getDialogPane(), "dialog-pane");
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
        //progressIndicator.setProgress(0.0);
        progressIndicator.setVisible(true);
    }
    
    private void indicateProgressEnd() {
        progressIndicator.setVisible(false);
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
            imagesGrid.getChildren().remove(imagesGrid.getChildren().stream().filter(z -> ((ThumbBox)z).getImage().equals(img.getImage())).findAny().get());
        }

        event.consume();
    }

    @FXML
    private void moveImagesClick(ActionEvent event) throws IOException {
        ChoiceDialog<FXCollection> dialog = new ChoiceDialog<>(FXCollection.defaultCollection, FXCollection.dictionary.values());
        dialog.setHeaderText("Choose a collection");
        dialog.setContentText("Collection:");
        dialog.getDialogPane().getScene().getStylesheets().add(getClass().getResource("/src/main/resources/css/default.css").toString());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        
        Optional<FXCollection> col = dialog.showAndWait();
        col.ifPresent((x) -> {
            List<FXImage> l = new ArrayList<>(selection.stream().map(z -> z.getImage()).collect(Collectors.toList()));
            l.forEach((img) -> {
                img.getEXImage().collection.moveImage(img.getEXImage(), x);
                imagesGrid.getChildren().remove(imagesGrid.getChildren().stream().filter(z -> ((ThumbBox)z).getImage().equals(img)).findAny().get());
            });
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
