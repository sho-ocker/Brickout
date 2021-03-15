package game.brickout.ui;

import com.almasb.fxgl.app.scene.StartupScene;
import javafx.animation.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class StartUpScene extends StartupScene {
    public StartUpScene(int appWidth, int appHeight) {
        super(appWidth, appHeight);

        Rectangle bg = new Rectangle(getAppWidth(), getAppHeight());

        var txt = new Text("BRICKOUT");

        txt.setX(getAppWidth()/4 + 50);
        txt.setY(getAppHeight()/2);
        txt.setFill(Color.BLUEVIOLET);
        txt.setFont(Font.font(120));
        txt.setStrokeWidth(20);
        txt.setStroke(Color.BLUEVIOLET);

        Reflection r = new Reflection();
        r.setFraction(0.7);

        txt.setEffect(r);

        FadeTransition fadeTransition =
                new FadeTransition(Duration.millis(2000), txt);
        fadeTransition.setFromValue(1.0f);
        fadeTransition.setToValue(0.0f);
        fadeTransition.setCycleCount(1);
        fadeTransition.setAutoReverse(true);

        ScaleTransition scaleTransition =
                new ScaleTransition(Duration.millis(2000), txt);
        scaleTransition.setToX(2f);
        scaleTransition.setToY(2f);
        scaleTransition.setCycleCount(Timeline.INDEFINITE);
        scaleTransition.setAutoReverse(true);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(
                scaleTransition,
                fadeTransition
        );
        parallelTransition.setCycleCount(Timeline.INDEFINITE);
        parallelTransition.play();

        getContentRoot().getChildren().addAll(bg, txt);
    }
}
