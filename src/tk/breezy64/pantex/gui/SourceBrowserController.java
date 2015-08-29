/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.Util;
import tk.breezy64.pantex.core.sources.ImageSource;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Rectangle;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class SourceBrowserController implements Initializable {
    
    private ImageSource src;
    private IntegerProperty thumbSize;
    
    @FXML
    private FlowPane flowPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Rectangle shadeRect;
    @FXML
    private ComboBox<Collection> collectionSelector;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        flowPane.prefWidthProperty().bind(scrollPane.widthProperty().subtract(2));
        shadeRect.widthProperty().bind(scrollPane.widthProperty());
        shadeRect.heightProperty().bind(scrollPane.heightProperty());
        collectionSelector.getItems().addAll(Collection.dictionary.values());
        collectionSelector.getSelectionModel().select(Collection.dictionary.get(0));
        
        thumbSize = new SimpleIntegerProperty(100);
    }    

    @FXML
    private void fetchClick(ActionEvent event) {
        if (src == null) {
            src = (ImageSource)flowPane.getScene().getUserData();
        }
        
        indicateProgressStart();
        Static.executor.submit(() -> fetch());
    }
    
    private void fetch() {
        EXImage[] imgs = src.next();
        if (imgs == null) {
            return;
        } 
        
        for (EXImage img : imgs) {
            Static.executor.submit(() -> fetchImage(img));
        }
        
        indicateProgressEnd();
    }
    
    private void indicateProgressStart() {
        progressIndicator.setVisible(true);
        //shadeRect.setVisible(true);
        flowPane.setEffect(new GaussianBlur(25));
    }
    
    private void indicateProgressEnd() {
        progressIndicator.setVisible(false);
        //shadeRect.setVisible(false);
        flowPane.setEffect(null);
    }

    @FXML
    private void closeClick(ActionEvent event) {
        flowPane.getScene().getWindow().hide();
    }
    
    private void fetchImage(EXImage img) {
        ImageView view = new ImageView(new Image(Util.fetchStream(img.thumbURL)));
        view.setPreserveRatio(true);
        view.fitWidthProperty().bind(thumbSize);
        view.fitHeightProperty().bind(thumbSize);
        view.setUserData(img);
            
        view.setOnMouseClicked((e) -> {
            if (e.getClickCount() == 2) {
                collectionSelector.getSelectionModel().getSelectedItem().addImage(img);
                Platform.runLater(() -> Static.rebuildImageList());
            }
            e.consume();
        });
            
        Platform.runLater(() -> flowPane.getChildren().add(view));
    }    
}
