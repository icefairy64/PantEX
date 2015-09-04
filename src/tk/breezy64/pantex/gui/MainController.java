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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class MainController implements Initializable {
    
    private DanbooruSource danbooru = new DanbooruSource();
    
    @FXML
    private MenuItem addImagesButton;
    @FXML
    private MenuItem collectionsButton;
    @FXML
    private MenuItem removeItemButton;
    @FXML
    private ListView<FXImage> imagesList;
    @FXML
    private ImageView image;
    @FXML
    private HBox imagePane;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Rectangle imageBg;
    @FXML
    private VBox vBox;
    @FXML
    private ScrollPane imageScrollPane;
    @FXML
    private StackPane imageStackPane;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imagesList.setItems(Static.images);
        imagesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        imageStackPane.prefWidthProperty().bind(imageScrollPane.widthProperty());
        imageStackPane.prefHeightProperty().bind(imageScrollPane.heightProperty());
        imagePane.prefWidthProperty().bind(imageScrollPane.widthProperty());
        imagePane.prefHeightProperty().bind(imageScrollPane.heightProperty());
        image.fitWidthProperty().bind(imageScrollPane.widthProperty());
        image.fitHeightProperty().bind(imageScrollPane.heightProperty());
        imageBg.widthProperty().bind(imageScrollPane.widthProperty());
        imageBg.heightProperty().bind(imageScrollPane.heightProperty());
        
        Static.currentImage.addListener((ChangeListener<FXImage>)(o, oV, nV) -> {
            indicateProgressStart();
            Static.executor.submit(() -> { Image x = nV.get(); onImageLoaded(x); });
        });
    }    

    @FXML
    private void addImages(ActionEvent event) throws Exception {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(Static.imageExtFilter);
        List<File> files = chooser.showOpenMultipleDialog(PantEX.stage);
        
        Scene dialogScene = new Scene(FXMLLoader.load(getClass().getResource("CollectionSelectorDialog.fxml")));
        Stage dialog = new Stage();
        dialog.setScene(dialogScene);
        dialog.showAndWait();
        
        SimpleCollection col = (SimpleCollection)dialogScene.getUserData();
        if (col != null) {
            EXImage[] imgs = new EXImage[files.size()];
            for (int i = 0; i < imgs.length; i++) {
                imgs[i] = new FileImage(files.get(i).getPath(), col, files.get(i).getName(), null);
            }
            col.addImages(imgs);
            
            //Static.rebuildImageList();
        }
        
        dialog.close();
    }

    @FXML
    private void showCollections(ActionEvent event) throws IOException {
        Util.showScene(new Scene(FXMLLoader.load(getClass().getResource("Collections.fxml"))), "Collections");
    }

    @FXML
    private void removeItem(ActionEvent event) {
    }

    @FXML
    private void selectImage(MouseEvent event) {
        event.consume();
        if (event.getClickCount() == 2) {
            FXImage img = imagesList.getSelectionModel().getSelectedItem();
            Static.currentImage.set(img);
        }
    }
    
    private void onImageLoaded(Image img) {
        indicateProgressEnd();
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
    
    @FXML
    private void fetchDanbooru(ActionEvent event) {
        indicateProgressStart();
        Static.executor.submit(() -> onFetch(danbooru.next()));
        event.consume();
    }
    
    private void onFetch(EXImage[] imgs) {
        indicateProgressEnd();
        for (EXImage img : imgs) {
            img.collection = SimpleCollection.dictionary.get(0);
            img.collection.addImage(img);
        }
        
        //Platform.runLater(() -> Static.rebuildImageList());
    }

    @FXML
    private void browseDanbooru(ActionEvent event) throws Exception {
        Scene browserScene = new Scene(FXMLLoader.load(getClass().getResource("SourceBrowser.fxml")));
        browserScene.setUserData(danbooru);
        
        Stage browser = new Stage();
        browser.setTitle("Danbooru browser");
        browser.setScene(browserScene);
        browser.show();
        
        event.consume();
    }

    @FXML
    private void browseLusciousSimple(ActionEvent event) throws Exception {
        Scene browserScene = new Scene(FXMLLoader.load(getClass().getResource("sources/LusciousOptions.fxml")));
        Stage browser = new Stage();
        browser.setTitle("Luscious");
        browser.setScene(browserScene);
        browser.show();
        event.consume();
    }

    @FXML
    private void browseDanbooruSearch(ActionEvent event) throws Exception {
        Scene browserScene = new Scene(FXMLLoader.load(getClass().getResource("SourceBrowser.fxml")));
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Danbooru [search]");
        dialog.setHeaderText("Search on Danbooru");
        dialog.setContentText("Tags:");
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return;
        }
        
        browserScene.setUserData(new DanbooruSource(result.get()));
        
        Stage browser = new Stage();
        browser.setTitle("Danbooru browser");
        browser.setScene(browserScene);
        browser.show();
        
        event.consume();
    }
    
}
