package com.hardronix.fireflower.javafx.nodes;

import com.hardronix.fireflower.utils.FXUtils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.bff.javampd.server.MPD;

import java.util.Timer;
import java.util.TimerTask;

public class MPDInfo extends VBox {
    MPD mpd;

    public MPDInfo(String address, String password)
    {
        Label trackLabel = new Label();
        Label artistLabel = new Label();
        ProgressBar trackProgress = new ProgressBar();

        trackProgress.setId("trackProgress");
        trackProgress.getStylesheets().add("light.css");

        trackProgress.setPrefWidth(300);
        trackLabel.prefWidthProperty().bind(this.widthProperty());
        artistLabel.prefWidthProperty().bind(this.widthProperty());

        trackLabel.setAlignment(Pos.CENTER);
        artistLabel.setAlignment(Pos.CENTER);

        trackLabel.setId("trackLabel");
        artistLabel.setId("artistLabel");

        trackLabel.setFont(new Font("Lato Thin", 20));
        artistLabel.setFont(new Font("Lato Thin", 20));

        trackLabel.setTextFill(Color.WHITE);
        artistLabel.setTextFill(Color.WHITE);

        FXUtils.addDropShadow(trackLabel, artistLabel);

        this.getChildren().add(trackLabel);
        this.getChildren().add(artistLabel);
        this.getChildren().add(trackProgress);

        setupMpd(address, password);
    }

    public void setupMpd(String address, String password)
    {
        Label trackLabel = (Label) lookup("#trackLabel");
        Label artistLabel = (Label) lookup("#artistLabel");
        ProgressBar trackProgress = (ProgressBar) lookup("#trackProgress");

        String[] addressPort = address.split(":");

        mpd = new MPD.Builder().server(addressPort[0]).password(password).port(Integer.parseInt(addressPort[1])).build();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if(mpd.getPlayer().getCurrentSong() != null) {
                        String songName = mpd.getPlayer().getCurrentSong().getName();
                        String artistName = mpd.getPlayer().getCurrentSong().getArtistName();

                        trackLabel.setText(songName.isEmpty() ? "No Info" : songName);
                        artistLabel.setText(artistName.isEmpty() ? mpd.getPlayer().getCurrentSong().getFile() : artistName);

                        float progress = (float) mpd.getPlayer().getElapsedTime() / mpd.getPlayer().getTotalTime();
                        trackProgress.setProgress(progress);
                    }
                    else
                    {
                        trackLabel.setText("Nothing is playing");
                        artistLabel.setText("right now");
                        trackProgress.setProgress(0);
                    }
                });
            }
        }, 0, 2000);

        if(!mpd.isConnected())
        {
            trackLabel.setText("Can't connect to MPD");
        }
    }
}
