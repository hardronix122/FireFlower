package com.hardronix.fireflower.javafx.nodes;

import com.hardronix.fireflower.utils.FXUtils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.text.SimpleDateFormat;
import java.util.*;

public class FancyClock extends VBox {

    SimpleDateFormat timeFormatter;
    SimpleDateFormat dateFormatter;
    Date date;

    public FancyClock()
    {
        timeFormatter = new SimpleDateFormat("HH:mm");
        dateFormatter = new SimpleDateFormat("E d MMM y");
        timeFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        dateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

        date = new Date(System.currentTimeMillis());
        Timer timeUpdateTimer = new Timer();

        //Time label
        Label timeLabel = new Label(timeFormatter.format(date));
        timeLabel.setFont(new Font("Lato Thin", 120));
        timeLabel.setAlignment(Pos.CENTER);
        timeLabel.setTextFill(Color.WHITE);

        //Date label
        Label dateLabel = new Label(dateFormatter.format(date));
        dateLabel.setFont(new Font("Lato Thin", 40));
        dateLabel.setAlignment(Pos.CENTER);
        dateLabel.setTextFill(Color.WHITE);

        FXUtils.addDropShadow(timeLabel, dateLabel);

        this.getChildren().add(timeLabel);
        this.getChildren().add(dateLabel);

        timeUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                date.setTime(System.currentTimeMillis());
                Platform.runLater(() -> timeLabel.setText(timeFormatter.format(date)));
            }
        }, 0, 1000);
    }
}
