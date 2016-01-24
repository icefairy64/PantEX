/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author icefairy64
 */
public final class FXUtil {
    
    public static void showScene(Scene scene, String title) {
        Stage st = new Stage();
        st.setScene(scene);
        st.setTitle(title);
        st.show();
    }
    
}
