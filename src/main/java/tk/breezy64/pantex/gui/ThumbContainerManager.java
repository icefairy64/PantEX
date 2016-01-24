/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.FlowPane;

/**
 *
 * @author icefairy64
 */
public class ThumbContainerManager {
    
    public FlowPane pane;
    
    private List<ThumbBox> currentRow;

    public ThumbContainerManager(FlowPane pane) {
        this.pane = pane;
        currentRow = new ArrayList<>();
    }
    
    private double getRowWidth() {
        double tmp = 0;
        for (ThumbBox b : currentRow) {
            tmp += b.getPrefWidth();
        }
        return tmp;
    }
    
    private double getIdealRowWidth() {
        return pane.getWidth() - pane.getPadding().getLeft() - pane.getPadding().getRight();
    }
    
    private double getIdealRowWidth(int n) {
        return pane.getWidth() - pane.getPadding().getLeft() - pane.getPadding().getRight() - pane.getHgap() * (n - 1);
    }
    
    public void add(ThumbBox img) {
        currentRow.add(img);
        if (getRowWidth() > getIdealRowWidth()) {
            double h = img.getPrefHeight();
            int n = currentRow.size();
            double pk = (getIdealRowWidth(n) - pane.getHgap()) / getRowWidth();
            double rW = getIdealRowWidth(n);
            for (ThumbBox b : currentRow) {
                b.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> {
                    double k = getIdealRowWidth(n) / rW;
                    return h * pk * k;
                }, pane.widthProperty()));
            }
            currentRow.clear();
        }
        pane.getChildren().add(img);
    }
    
    public void reset() {
        currentRow.clear();
    }
    
}
