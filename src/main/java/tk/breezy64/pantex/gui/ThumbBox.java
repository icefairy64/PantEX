/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tk.breezy64.pantex.gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import tk.breezy64.pantex.gui.FXImage;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class ThumbBox extends AnchorPane implements Initializable {
    @FXML
    private ImageView imageBox;
    @FXML
    private Polygon selectionMarker;    
    @FXML
    private AnchorPane rootPane;
    
    private SimpleBooleanProperty selected;
    private FXImage image;

    public ThumbBox() {
        super();
    }

    public ThumbBox(Node... children) {
        super(children);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selected = new SimpleBooleanProperty(false);
        
        imageBox.fitHeightProperty().bind(rootPane.prefHeightProperty());
        rootPane.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> {
            return imageBox.getImage() != null ?
                    imageBox.getImage().getWidth() * (rootPane.getPrefHeight()/ imageBox.getImage().getHeight()) :
                    imageBox.getFitWidth();
        }, imageBox.imageProperty(), rootPane.heightProperty()));
        rootPane.maxWidthProperty().bind(rootPane.prefWidthProperty());
        imageBox.fitWidthProperty().bind(rootPane.prefWidthProperty());
        
        selectionMarker.visibleProperty().bind(selected);
        //selectionMarker.scaleXProperty().bind(rootPane.heightProperty().divide(100));
        //selectionMarker.scaleYProperty().bind(selectionMarker.scaleXProperty());
        //selectionMarker.translateXProperty().bind(rootPane.heightProperty().subtract(100).divide(100).multiply(30).negate());
        //selectionMarker.translateYProperty().bind(selectionMarker.translateXProperty());
    }
    
    public void setImage(FXImage img) throws IOException {
        image = img;
        imageBox.setImage(new FXImage(img.getEXImage().getThumb()).get());
    }
    
    public FXImage getImage() {
        return image;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }
    
    public void setSelected(boolean value) {
        selected.set(value);
    }
    
    public boolean isSelected() {
        return selected.get();
    }
    
    public ImageView getImageBox() {
        return imageBox;
    }
    
    // Static members
    
    public static ThumbBox create() {
        FXMLLoader ldr = new FXMLLoader(ThumbBox.class.getResource("/src/main/resources/fxml/ThumbBox.fxml"));
        ThumbBox tmp = new ThumbBox();
        ldr.setController(tmp);
        ldr.setRoot(tmp);
        try {
            ldr.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tmp;
    }
}
