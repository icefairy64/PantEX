/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author icefairy64
 */
public class TaskManagerController implements Initializable {

    @FXML
    private Button cancelButton;
    @FXML
    private ListView<Task<?>> taskList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        taskList.itemsProperty().set(FXTask.list);
        taskList.cellFactoryProperty().set(v -> {
            return new TextFieldListCell<>(new StringConverter<Task<?>>() {
                @Override
                public String toString(Task<?> object) {
                    return object.getTitle();
                }

                @Override
                public Task<?> fromString(String string) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
        });
        
        cancelButton.disableProperty().bind(taskList.selectionModelProperty().get().selectedItemProperty().isNull());
    }    

    @FXML
    private void cancelClick(ActionEvent event) {
        FXTask.stop(taskList.getSelectionModel().getSelectedItem());
    }
    
}

