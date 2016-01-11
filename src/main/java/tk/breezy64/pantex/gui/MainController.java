/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import tk.breezy64.pantex.core.SimpleCollection;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.FileImage;
import tk.breezy64.pantex.core.sources.DanbooruSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.Static;
import tk.breezy64.pantex.core.sources.ImageSource;
import tk.breezy64.pantex.core.sources.ImageSourceRequester;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class MainController implements Initializable {

    private DoubleProperty zoomFactor = new SimpleDoubleProperty(1.0);
    
    @FXML
    private MenuItem addImagesButton;
    @FXML
    private MenuItem collectionsButton;
    @FXML
    private ImageView image;
    @FXML
    private HBox imagePane;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private VBox vBox;
    @FXML
    private ScrollPane imageScrollPane;
    @FXML
    private StackPane imageStackPane;
    @FXML
    private Menu sourcesMenu;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageScrollPane.prefWidthProperty().bind(imageStackPane.widthProperty());
        imageScrollPane.prefHeightProperty().bind(imageStackPane.heightProperty());
        imagePane.prefWidthProperty().bind(imageScrollPane.widthProperty());
        imagePane.prefHeightProperty().bind(imageScrollPane.heightProperty());
        image.fitWidthProperty().bind(imageScrollPane.widthProperty().multiply(zoomFactor));
        image.fitHeightProperty().bind(imageScrollPane.heightProperty().multiply(zoomFactor));
        
        zoomFactor.addListener((x, oV, nV) -> {
            double delta = nV.doubleValue() / oV.doubleValue();
            imageScrollPane.setVvalue(imageScrollPane.getVvalue() * delta);
            imageScrollPane.setHvalue(imageScrollPane.getHvalue() * delta);
        });
        
        FXStatic.currentImage.addListener((ChangeListener<FXImage>)(o, oV, nV) -> {
            indicateProgressStart();
            FXStatic.executor.submit(() -> { Image x = nV.get(); onImageLoaded(x); });
        });
        
        if (FXStatic.currentImage.get() != null) {
            image.setImage(FXStatic.currentImage.get().get());
            imageStackPane.setPrefHeight(image.getImage().getHeight() * (vBox.getPrefWidth() / image.getImage().getWidth()));
        }
        
        refreshSources();
    }    

    @FXML
    private void addImages(ActionEvent event) throws Exception {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(FXStatic.imageExtFilter);
        List<File> files = chooser.showOpenMultipleDialog(PantEX.stage);
        
        ChoiceDialog<Collection> dialog = new ChoiceDialog<>(FXCollection.defaultCollection, FXCollection.dictionary.values().stream().collect(Collectors.toList()));
        dialog.setTitle("Collection");
        dialog.setHeaderText("Choose a collection");
        dialog.setContentText("Select:");
        Optional<Collection> col = dialog.showAndWait();
        
        if (col.isPresent()) {
            EXImage[] imgs = new EXImage[files.size()];
            for (int i = 0; i < imgs.length; i++) {
                imgs[i] = new FileImage(files.get(i).getPath(), col.get(), files.get(i).getName(), null);
            }
            col.get().addImages(imgs);
            
            //Static.rebuildImageList();
        }
        
        event.consume();
    }

    @FXML
    private void showCollections(ActionEvent event) throws IOException {
        Util.showScene(new Scene(FXMLLoader.load(getClass().getResource("/src/main/resources/fxml/Collections.fxml"))), "Collections");
    }
    
    private void onImageLoaded(Image img) {
        indicateProgressEnd();
        zoomFactor.set(1.0);
        image.setImage(img);
        image.setFitWidth(image.getParent().getBoundsInLocal().getWidth());
        image.setFitHeight(image.getParent().getBoundsInLocal().getHeight());
    }

    private void indicateProgressStart() {
        loadingIndicator.setVisible(true);
        imagePane.setEffect(new GaussianBlur(25));
    }
    
    private void indicateProgressEnd() {
        loadingIndicator.setVisible(false);
        imagePane.setEffect(null);
    }
    
    private void onFetch(EXImage[] imgs) {
        indicateProgressEnd();
        for (EXImage img : imgs) {
            img.collection = SimpleCollection.dictionary.get(0);
            img.collection.addImage(img);
        }
        
        //Platform.runLater(() -> FXStatic.rebuildImageList());
    }
    
    private void showSourceBrowser(ImageSource src) {
        try {
            Scene browserScene = new Scene(FXMLLoader.load(getClass().getResource("/src/main/resources/fxml/SourceBrowser.fxml")));
            browserScene.setUserData(src);
            Stage browser = new Stage();
            browser.setTitle(src.getWindowTitle());
            browser.setScene(browserScene);
            browser.show();
        }
        catch (Exception e) {
            FXStatic.handleException(e);
        }
    }
    
    private void refreshSources() {
        List<MenuItem> list = sourcesMenu.getItems();
        list.clear();
        List<Menu> catList = new ArrayList<>();
        
        for (ImageSourceRequester<? extends ImageSource> req : Static.pluginManager.getExtensions(ImageSourceRequester.class)) {
            MenuItem item = new MenuItem(req.getTitle());
            item.setOnAction((e) -> {
                req.onFinish((x) -> showSourceBrowser(x)).request();
                e.consume();
            });
            
            Menu res = catList.stream().reduce(null, (a, x) -> a == null ? (req.getCategory().equals(x.getText()) ? x : null) : a);
            if (res == null) {
                res = new Menu(req.getCategory());
                catList.add(res);
            }
            
            res.getItems().add(item);
        }
        
        Platform.runLater(() -> sourcesMenu.getItems().addAll(catList));
    }

    @FXML
    private void imageZoom(ScrollEvent event) {
        zoomFactor.set(zoomFactor.get() + zoomFactor.get() * 0.2 * Math.signum(event.getDeltaY()));
        event.consume();
    }

    @FXML
    private void showImageList(ActionEvent event) throws IOException {
        Util.showScene(new Scene(FXMLLoader.load(getClass().getResource("/src/main/resources/fxml/ImagesList.fxml"))), "Image list");
    }
    
}
