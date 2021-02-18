package com.hardronix.fireflower.javafx;

import com.hardronix.fireflower.Config;
import com.hardronix.fireflower.javafx.nodes.FancyClock;
import com.hardronix.fireflower.javafx.nodes.MPDInfo;
import com.hardronix.fireflower.nasa.EPICEntry;
import com.hardronix.fireflower.nasa.NASAImageRequester;
import com.hardronix.fireflower.utils.FXUtils;
import com.hardronix.fireflower.utils.MathUtils;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.collections4.iterators.LoopingListIterator;

import java.util.Timer;
import java.util.TimerTask;

public class FireFlower extends Application {
    public static Config config;

    Scene rootScene;
    NASAImageRequester imageRequester;
    LoopingListIterator<EPICEntry> epicImages;

    @Override
    public void start(Stage stage) {
        StackPane rootPane = new StackPane();
        imageRequester = new NASAImageRequester(config.getString("nasa_api_key"));
        rootScene = new Scene(rootPane);

        setupNodes();
        setupBackground();

        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        stage.setScene(rootScene);
        stage.show();
    }

    private void setupNodes() {
        StackPane rootPane = (StackPane) rootScene.getRoot();

        //Background
        ImageView background = new ImageView();
        background.setId("background");
        rootPane.getChildren().add(background);

        //Elements grid panel
        GridPane elementsPane = new GridPane();
        elementsPane.setAlignment(Pos.CENTER);

        //Fancy clock
        FancyClock fancyClock = new FancyClock();
        fancyClock.setAlignment(Pos.CENTER);
        elementsPane.add(fancyClock, 0, 0);

        if(config.getBoolean("enable_mpd")) {
            //Music Player Daemon Info
            MPDInfo mpdInfo = new MPDInfo(config.getString("mpd_address"), config.getString("mpd_password"));
            mpdInfo.setAlignment(Pos.CENTER);
            elementsPane.add(mpdInfo, 0, 1);
        }

        rootPane.getChildren().add(elementsPane);

        //EPIC (NASA) Caption
        Label epicCaption = new Label();
        epicCaption.setId("epic_caption");
        epicCaption.setTextFill(Color.WHITE);
        epicCaption.setFont(new Font("Lato Thin", 20));
        epicCaption.setAlignment(Pos.BOTTOM_RIGHT);
        epicCaption.setPadding(new Insets(0, 5, 0, 0));
        StackPane.setAlignment(epicCaption, Pos.BOTTOM_RIGHT);

        FXUtils.addDropShadow(epicCaption);

        rootPane.getChildren().add(epicCaption);

    }

    private void setupBackground() {
        epicImages = imageRequester.getEPICImages();

        StackPane rootPane = (StackPane) rootScene.getRoot();
        ImageView background = (ImageView) rootScene.lookup("#background");
        Label epicCaption = (Label) rootScene.lookup("#epic_caption");

        rootPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.4);
        background.setEffect(colorAdjust);

        if (epicImages != null && epicImages.next() != null) {
            Image image = new Image(epicImages.next().buildURL("jpg"), true);
            image.progressProperty().addListener((observable, oldValue, newValue) -> {
                epicCaption.setText("Fetching data (" + (int) MathUtils.lerp(0, 100, newValue.floatValue()) + "%)...");
                if (newValue.doubleValue() == 1.0) {
                    FadeTransition ft = new FadeTransition(Duration.millis(1000), background);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.setInterpolator(Interpolator.LINEAR);
                    ft.play();
                    epicCaption.setText(epicImages.previous().getCaption());
                }
            });
            background.setImage(image);

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    EPICEntry epicEntry = epicImages.next();
                    Image image = new Image(epicEntry.buildURL("jpg"), true);
                    image.progressProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.doubleValue() == 1.0) {
                            background.setImage(image);
                            epicCaption.setText(epicEntry.getCaption());
                        }
                    });
                }
            }, 5000, 5000);
        }
        else
        {
            epicCaption.setText("Unable to connect to server.");
        }
    }

    public void start() {
        FireFlower.config = new Config();
        launch();
    }

}