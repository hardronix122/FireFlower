package com.hardronix.fireflower.utils;

import com.hardronix.fireflower.javafx.FireFlower;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;

public class FXUtils {

    public static void addDropShadow(Node... nodes) {

        if(FireFlower.config.getBoolean("text_shadow")) {
            DropShadow shadow = new DropShadow();
            shadow.setRadius(50);
            shadow.setSpread(0.7);
            for (Node node : nodes) {
                if (node instanceof Label) {
                    shadow.setRadius(((Label) node).getFont().getSize());
                }
                node.setEffect(shadow);
            }
        }
    }
}
