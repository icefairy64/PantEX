/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tk.breezy64.pantex.core.ConfigManager;
import tk.breezy64.pantex.core.FileImage;
import tk.breezy64.pantex.core.ImgurHelper;
import tk.breezy64.pantex.core.Static;

/**
 *
 * @author icefairy64
 */
public class PantEX extends Application {
    
    public final static String configFile = "config.json";
    
    public static Stage stage;

    @Override
    public void init() throws Exception {
        super.init();
        
        FXCollection.createAndAdd("Temp");
        Static.pluginManager.loadPlugins();
        Static.pluginManager.startPlugins();
        ConfigManager.load(new File(configFile));
        
        File img = new File("title.png");
        if (img.exists()) {
            FXStatic.currentImage.set(new FXImage(new FileImage(img)));
        }
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("Main.fxml")));
        
        primaryStage.setTitle("PantEX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        ConfigManager.save(new File(configFile));
        FXStatic.executor.shutdown();
        super.stop();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
