/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import java.io.IOException;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.sources.ImageSource;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class SourceBrowserController implements Initializable {
    
    private ImageSource src;
    private IntegerProperty thumbSize;
    private int lastPage;
    
    @FXML
    private FlowPane flowPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Rectangle shadeRect;
    @FXML
    private ComboBox<FXCollection> collectionSelector;
    @FXML
    private HBox rightHBox;
    @FXML
    private Slider thumbSizeSlider;
    @FXML
    private AnchorPane bottomPanel;
    @FXML
    private HBox leftHBox;
    @FXML
    private TextField page;
    @FXML
    private Button fetchButton;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        flowPane.prefWidthProperty().bind(scrollPane.widthProperty().subtract(2));
        shadeRect.widthProperty().bind(scrollPane.widthProperty());
        shadeRect.heightProperty().bind(scrollPane.heightProperty());
        try {
            collectionSelector.getItems().addAll(FXCollection.dictionary.values());
        }
        catch (Exception e) {
            FXStatic.handleException(e);
        }
        collectionSelector.getSelectionModel().select(FXCollection.dictionary.values().iterator().next());
        rightHBox.prefWidthProperty().bind(bottomPanel.widthProperty().subtract(leftHBox.widthProperty()));
        
        thumbSize = new SimpleIntegerProperty(100);
        thumbSize.bind(thumbSizeSlider.valueProperty());
    }    

    @FXML
    private void fetchClick(ActionEvent event) {
        if (src == null) {
            src = (ImageSource)flowPane.getScene().getUserData();
            src.onException((x) -> FXStatic.handleException(x));
        }
        
        int curPage = lastPage;
        try {
            curPage = Integer.parseInt(page.getText());
        }
        catch (NumberFormatException e) {
            
        }
        
        if (lastPage != curPage) {
            src.setPage(curPage);
        }
        
        indicateProgressStart();
        fetchButton.setDisable(true);
        FXStatic.executor.submit(() -> fetch());
    }
    
    private void fetch() {
        try {
            EXImage[] imgs = src.next();
            if (imgs == null) {
                return;
            }

            for (EXImage img : imgs) {
                FXStatic.executor.submit(() -> fetchImage(img));
            }
        }
        catch (Exception e) {
            FXStatic.handleException(e);
        }
        
        lastPage = src.getPage();
        page.setText(String.valueOf(lastPage));
        fetchButton.setDisable(false);
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
        try {
            ImageView view = new ImageView(new Image(img.getThumb().getImageStream()));
            view.setPreserveRatio(true);
            //view.fitWidthProperty().bind(thumbSize);
            view.fitHeightProperty().bind(thumbSize);
            view.setUserData(img);
            view.setCache(true);

            view.setOnMouseClicked((e) -> {
                if (e.getClickCount() == 2) {
                    collectionSelector.getSelectionModel().getSelectedItem().addImage(img);
                }
                
                if (e.getButton() == MouseButton.SECONDARY) {
                    FXStatic.currentImage.set(new FXImage((EXImage)view.getUserData()));
                }
                e.consume();
            });

            Platform.runLater(() -> flowPane.getChildren().add(view));
        }
        catch (IOException e) {
            FXStatic.handleException(e);
        }
    }    
}
