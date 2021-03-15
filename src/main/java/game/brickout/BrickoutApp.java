/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package game.brickout;

import com.almasb.fxgl.animation.AnimatedValue;
import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.StartupScene;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import game.brickout.components.BallComponent;
import game.brickout.components.BatComponent;
import game.brickout.ui.AppMainMenu;
import game.brickout.ui.NewLevelSubScene;
import game.brickout.ui.StartUpScene;
import javafx.animation.PathTransition;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static game.brickout.BrickoutType.*;


public class BrickoutApp extends GameApplication{

    public static final int WIDTH = 14 * 96;        //1344
    public static final int HEIGHT = 22 * 32;       //704

    Entity ball;
    Entity brick;
    Entity bat;

    boolean moving = false;


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL Brickout");
        settings.setVersion("0.0");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setFontUI("plaguard.otf");
        settings.setAppIcon("icon/icon.png");

        settings.setIntroEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);

        settings.setMainMenuEnabled(true);
        settings.setDeveloperMenuEnabled(true);
     //   settings.setEnabledMenuItems(EnumSet.of(MenuItem.EXTRA));
        settings.setGameMenuEnabled(true);

        settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);


        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu(){
                return new AppMainMenu();
            }

            @Override
            public LoadingScene newLoadingScene(){
                return new AppMainMenu.LoadingScreen();
            }

            @Override
            public StartUpScene newStartup(int width, int height) {
                return new StartUpScene(getAppWidth(), getAppHeight());
            }
        });
    }

    @Override
    protected void onPreInit() {
        getSettings().setGlobalMusicVolume(0.0);
        getSettings().setGlobalSoundVolume(0.2);
        loopBGM("BGM.mp3");
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                getBatControl().left();
            }

            @Override
            protected void onActionEnd() {
                getBatControl().stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                getBatControl().right();
            }

            @Override
            protected void onActionEnd() {
                getBatControl().stop();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Escape") {
            @Override
            protected void onAction() {
                getGameController().gotoGameMenu();
            }
        }, KeyCode.ESCAPE);

        getInput().addAction(new UserAction("Fire Ball") {
            @Override
            protected void onAction() {
                if(!moving) {
                    ball.removeFromWorld();
                    ball = spawn("ball", bat.getX() + 70, getAppHeight() - 85);

                    getBallControl().release();
                    moving = true;
                }
            }
        }, KeyCode.SPACE);

     /*   getInput().addAction(new UserAction("Angle") {
            @Override
            protected void onAction() {
                spawn("line");
                System.out.println("aaaa");
            }
        }, KeyCode.M); */
    }


    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("lives", 3);
        vars.put("score", 0);
        vars.put("level", 1);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new BrickoutFactory());

        setLevel(1);

        initBackground();
    }

    private void initBackground() {
        spawn("background");

        entityBuilder()
                .type(WALL)
                .collidable()
                .with(new IrremovableComponent())
                .buildScreenBoundsAndAttach(40);
    }

    private void playHitSound() {
        play("hit.wav");
    }

    private void resetBall(){
        ball = spawn("ball", bat.getX() + 70, getAppHeight() - 85);
    }

    private void stopGame(){
        ball.removeFromWorld();
        getDialogService().showMessageBox("GAME OVER", getGameController()::gotoMainMenu);
    }

    public void youWin(){
        ball.removeFromWorld();
        getDialogService().showMessageBox("YOU WIN", getGameController()::gotoMainMenu);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        onCollisionBegin(BALL, BRICK, (ball, brick) -> {
            playHitSound();

            brick.call("onHit");

            inc("score", +50);
        });

        onCollisionBegin(BAT, BALL, (bat, ball) -> playHitSound());

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(BALL, WALL) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                playHitSound();
                getGameScene().getViewport().shake(0.3, 0.5);
            }
        });
    }


    @Override
    protected void onUpdate(double tpf) {
        if (ball.getBottomY() - 70 > getAppHeight()) {
            ball.removeFromWorld();
            getWorldProperties().setValue("score", geti("score") / 2);
            getWorldProperties().increment("lives", -1);
            getDialogService().showMessageBox("YOU HAVE " + getWorldProperties().getValue("lives") + " LIVES");

            moving = false;
            resetBall();
            getGameTimer().runOnceAfter(() -> System.out.println("eeeeee"), Duration.millis(500));

        }
        if(getWorldProperties().getValue("lives").toString().equals("0"))
            stopGame();

        cameraAnimation.onUpdate(tpf);

        if (byType(BRICK).isEmpty()) {
            nextLevel();
        }
    }

    @Override
    protected void initUI() {
        var textScore = getUIFactoryService().newText(getip("score").asString());
        var textLives = getUIFactoryService().newText(getip("lives").asString());
        var score = getUIFactoryService().newText("SCORE : ");
        var lives = getUIFactoryService().newText("LIVES LEFT : ");


        score.fillProperty().setValue(Color.WHITE);
        score.setScaleY(2);
        score.setScaleX(1.5);
        addUINode(score, 1150, 50);

        lives.fillProperty().setValue(Color.WHITE);
        lives.setScaleY(2);
        lives.setScaleX(1.5);
        addUINode(lives, 50, 50);


        textScore.setScaleY(2);
        textLives.setScaleY(2);
        textScore.setScaleX(1.5);
        textLives.setScaleX(1.5);
        addUINode(textScore, 1240, 50);
        addUINode(textLives, 190, 50);

        var view = new KeyView(KeyCode.ESCAPE, Color.BLUEVIOLET, 17.0);
        var hBox = new HBox(15, getUIFactoryService().newText("EXIT", 22.0), view);
        addUINode(hBox, 1240, 680);

        var textLevel = getUIFactoryService().newText(getip("level").asString());
        var level = getUIFactoryService().newText("LEVEL : ");
        level.fillProperty().setValue(Color.WHITE);
        textLevel.setScaleY(2);
        textLevel.setScaleX(1.5);
        level.setScaleY(2);
        level.setScaleX(1.5);
        addUINode(level, 640, 50);
        addUINode(textLevel, 730, 50);
    }

    private BatComponent getBatControl() {
        return getGameWorld().getSingleton(BAT).getComponent(BatComponent.class);
    }

    private BallComponent getBallControl() {
        return getGameWorld().getSingleton(BALL).getComponent(BallComponent.class);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void nextLevel() {
        var levelNum = geti("level");

        if (levelNum >= 4) youWin();

        inc("level", +1);

        levelNum = geti("level");
        moving = false;
        setLevel(levelNum);
    }

    private void setLevel(int levelNum) {
        getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);
        setLevelFromMap("tmx/lvl" + levelNum + ".tmx");

        bat = spawn("bat", getAppWidth() / 2, getAppHeight() - 180);
        resetBall();

        animateCamera(() -> getSceneService().pushSubScene(new NewLevelSubScene(levelNum)));
    }

    private Animation<Double> cameraAnimation;

    private void animateCamera(Runnable onAnimationFinished) {
        AnimatedValue<Double> value = new AnimatedValue<>(getAppWidth() * 1.0, 0.0);

        cameraAnimation = animationBuilder()
                .duration(Duration.seconds(2.5))
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .onFinished(onAnimationFinished::run)
                .animate(value)
                .onProgress(x -> getGameScene().getViewport().setX(x))
                .build();

        cameraAnimation.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
