/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author icefairy64
 */
public class PantEX extends Application {
    
    public static Stage stage;

    @Override
    public void init() throws Exception {
        super.init();
        
        FXCollection.create("Temp");
        Static.pluginManager.loadPlugins();
        Static.pluginManager.startPlugins();
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
        Static.executor.shutdown();
        super.stop();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
