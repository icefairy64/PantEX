/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import java.io.IOException;
import javafx.scene.image.Image;
import tk.breezy64.pantex.core.EXImage;

/**
 *
 * @author icefairy64
 */
public class FXImage {
    
    private final EXImage image;

    public FXImage(EXImage image) {
        if (image == null) {
            throw new NullPointerException();
        }
        this.image = image;
    }
    
    public Image get() throws IOException {
        return new Image(getEXImage().getImageStream());
    }
    
    public EXImage getEXImage() {
        return image;
    }
    
    public static Image fromEX(EXImage image) {
        try {
            return new Image(image.getImageStream());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
