/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.sources.ImageSource;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.GaussianBlur;
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
    private List<ThumbBox> imageList;
    private ThumbContainerManager containerManager;
    
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
        imageList = new ArrayList<>();
        containerManager = new ThumbContainerManager(flowPane);
        
        collectionSelector.getSelectionModel().selectedItemProperty().addListener((ChangeListener<FXCollection>)(v, oV, nV) -> {
            imageList.stream()
                    .forEach((ThumbBox x) -> x.setSelected(nV.images.entrySet().stream().anyMatch((z) -> z.getValue().getEXImage().equals(x.getImage().getEXImage()))));
        });
        
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
        
        fetchButton.setOnMouseClicked((e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                e.consume();
                TextInputDialog d = new TextInputDialog();
                d.setHeaderText("Fetch");
                d.setContentText("Pages to fetch:");
                FXStatic.applyCss(d.getDialogPane(), "dialog-pane");
                Optional<String> o = d.showAndWait();
                if (o.isPresent()) {
                    int x = Integer.parseInt(o.get());
                    fetch(x);
                }
            }
        });
    }    

    @FXML
    private void fetchClick(ActionEvent event) {
        event.consume();
        fetch(1);
    }
    
    private void fetch(int count) {
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
        
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < count; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    fetch();
                }
                return null;
            }

            @Override
            protected void scheduled() {
                updateTitle(String.format("Fetching %d pages", count));
                super.scheduled();
            }

            @Override
            protected void done() {
                super.done();
                fetchButton.setDisable(false);
                indicateProgressEnd();
            }

            @Override
            protected void failed() {
                super.failed();
                fetchButton.setDisable(false);
                indicateProgressEnd();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                fetchButton.setDisable(false);
                indicateProgressEnd();
            }
            
        };
        
        FXTask.schedule(task);
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
    }
    
    private void indicateProgressStart() {
        progressIndicator.setVisible(true);
        flowPane.setEffect(new GaussianBlur(25));
    }
    
    private void indicateProgressEnd() {
        progressIndicator.setVisible(false);
        flowPane.setEffect(null);
    }

    @FXML
    private void closeClick(ActionEvent event) {
        flowPane.getScene().getWindow().hide();
    }
    
    private void fetchImage(EXImage img) {
        try {
            ThumbBox box = ThumbBox.create();

            ImageView view = box.getImageBox();
            view.setPreserveRatio(true);
            //box.prefWidthProperty().bind(thumbSize);
            box.prefHeightProperty().bind(thumbSize);
            box.setImage(new FXImage(img));
            box.setSelected(collectionSelector.getSelectionModel().getSelectedItem().images.entrySet().stream().anyMatch(x -> x.getValue().getEXImage().equals(box.getImage().getEXImage())));
            view.setUserData(img);
            view.setCache(true);

            box.setOnMouseClicked((e) -> { 
                if (e.getClickCount() == 2) {
                    if (!box.isSelected()) {
                        collectionSelector.getSelectionModel().getSelectedItem().addImage(img);
                        box.setSelected(true);
                    }
                    else {
                        collectionSelector.getSelectionModel().getSelectedItem().removeImage(img);
                        box.setSelected(false);
                    }
                }
                
                if (e.getButton() == MouseButton.SECONDARY) {
                    FXStatic.currentImage.set(box.getImage());
                }
                
                e.consume();
            });

            Platform.runLater(() -> { 
                //flowPane.getChildren().add(box);
                containerManager.add(box);
                containerManager.pane.requestLayout();
                imageList.add(box);
            });
        }
        catch (Exception e) {
            FXStatic.handleException(e);
        }
    }    
}
