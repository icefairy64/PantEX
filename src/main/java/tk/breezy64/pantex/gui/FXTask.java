/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import com.sun.javafx.collections.ObservableListWrapper;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author breezy
 */
public class FXTask {
    
    private static final Logger logger = LoggerFactory.getLogger(FXTask.class);
    
    public final static ObservableList<Task<?>> list = new ObservableListWrapper<>(new ArrayList<Task<?>>());
    
    private static boolean running;
    
    private static void run(Task<?> task) {
        running = true;
        FXStatic.executor.submit(task);
    }
    
    public static void schedule(Task<?> task) {
        final EventHandler<WorkerStateEvent> prev = task.getOnSucceeded();
        task.onSucceededProperty().set(e -> {
            running = false;
            if (prev != null) {
                prev.handle(e);
            }
            else {
                e.consume();
            }
            list.removeAll(task);
            logger.info("Task finished: " + task.getTitle());
            if (!list.isEmpty()) {
                run(list.get(0));
            }
        });
        
        final EventHandler<WorkerStateEvent> prez = task.getOnRunning();
        task.onRunningProperty().set(e -> {
            if (prez != null) {
                prez.handle(e);
            }
            else {
                e.consume();
            }
            logger.info("Running task: " + task.getTitle());
        });
        
        if (!list.contains(task)) {
            list.add(task);
        }
        
        if (!running) {
            run(task);
        }
        else {
            logger.info("Scheduling another task");
        }
    }
    
    public static void stop(Task<?> task) {
        task.cancel();
        list.remove(task);
    }
    
}
