package game.brickout.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;


public class AppMainMenu extends FXGLMenu {

    private ObjectProperty<AppButton> selectedButton;   //object property so we can observe it

    private VBox startMenu;
    private VBox optionsMenu;
    private HBox hBox;
    private KeyView view;
    private Text textDescription;

    private AppButton btnOptions;

    public AppMainMenu() {
        super(MenuType.MAIN_MENU);

        AppButton btnPlayGame = new AppButton("Play Game", "Start new game", () -> {
            new LoadingScreen();
            fireNewGame();
        });

        btnOptions = new AppButton("Options", "Adjust in-game options", () ->
                getContentRoot().getChildren().add(new OptionsMenu())
        );
        AppButton btnQuit = new AppButton("Quit", "Exit to desktop", () -> fireExit());

        selectedButton = new SimpleObjectProperty<>();

        textDescription = getUIFactoryService().newText("");

        textDescription.textProperty().bind(
                Bindings.createStringBinding(() -> (selectedButton.get() != null) ? selectedButton.get().description : "", selectedButton)
        );

        startMenu = new VBox(30,
                btnPlayGame,
                btnOptions,
                btnQuit,
                new Text(""),
                new LineSeparator(),
                textDescription);


        startMenu.setTranslateX(720);
        startMenu.setTranslateY(290);


        view = new KeyView(KeyCode.ESCAPE, Color.BLUEVIOLET, 22.0);
        view.setTranslateY(-10);

        hBox = new HBox(15, getUIFactoryService().newText("EXIT", 20.0), view);
        hBox.setTranslateX(getAppWidth() - 270);
        hBox.setTranslateY(515);


        getContentRoot().getChildren().addAll(
                createBackground(getAppWidth(), getAppHeight()),
                startMenu,
                hBox
        );
    }

    protected Node createBackground(double w, double h) {
        return texture("background/universe.jpg");
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class AppButton extends StackPane {
        private final Color SELECTED_COLOR = Color.BLUEVIOLET;
        private final Color NOT_SELECTED_COLOR = Color.WHITESMOKE;

        private String name;
        private String description;
        private Runnable action;
        private Text text;
        private Rectangle selector;


        public AppButton(String name, String description, Runnable action) {
            this.name = name;
            this.description = description;
            this.action = action;

            text = getUIFactoryService().newText(name, Color.WHITE, 38.0);
            text.fillProperty().setValue(NOT_SELECTED_COLOR);

            selector = new Rectangle(5, 30, Color.WHITE);
            selector.setTranslateX(-20);
            selector.setTranslateY(-2);


            setAlignment(Pos.CENTER_LEFT);
            setFocusTraversable(true);

            selector.visibleProperty().bind(hoverProperty());


            hoverProperty().addListener(e -> {
                if (isHover()) {
                    selectedButton.setValue(this);
                }
            });

            setOnMouseEntered(e -> {
                text.fillProperty().setValue(SELECTED_COLOR);
                text.strokeProperty().setValue(SELECTED_COLOR);
                text.setStrokeWidth(1.2);
            });

            setOnMouseExited(e -> {
                text.fillProperty().setValue(NOT_SELECTED_COLOR);
                text.strokeProperty().setValue(NOT_SELECTED_COLOR);
                text.setStrokeWidth(0);
            });

            setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1)
                    action.run();
            });

            setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE)
                    fireExit();
            });

            getChildren().addAll(text, selector);
        }
    }

    private static class LineSeparator extends Parent {
        private Rectangle line = new Rectangle(400, 2);

        public LineSeparator() {
            var gradient = new LinearGradient(0, 0.5, 1, 0.5, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.WHITE),
                    new Stop(0.5, Color.GRAY),
                    new Stop(1.0, Color.TRANSPARENT)
            );
            line.setFill(gradient);
            getChildren().addAll(line);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public class OptionsMenu extends Pane {
        public OptionsMenu() {
            optionsMenu = new VBox(30);

            optionsMenu.setTranslateX(720);
            optionsMenu.setTranslateY(290);

            AppButton btnBack = new AppButton("Back", "Back to Start menu", () -> {
            });

            btnBack.setOnMouseClicked(event -> {
                getChildren().add(startMenu);

                FadeTransition ft = new FadeTransition(Duration.seconds(0.5), optionsMenu);
                ft.setFromValue(1);
                ft.setToValue(0);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), optionsMenu);
                tt.setToX((optionsMenu.getTranslateX() - 150));

                TranslateTransition kk = new TranslateTransition(Duration.seconds(0.5), startMenu);
                kk.setToX(optionsMenu.getTranslateX());

                tt.play();
                kk.play();
                ft.play();

                tt.setOnFinished(e -> {
                    getChildren().remove(optionsMenu);
                });
            });

            textDescription = getUIFactoryService().newText("");

            textDescription.textProperty().bind(
                    Bindings.createStringBinding(() -> (selectedButton.get() != null) ? selectedButton.get().description : "", selectedButton)
            );

            AppButton btnSound = new AppButton("Sound", "Change audio settings", () -> {
            });
            AppButton btnVideo = new AppButton("Video", "Change video settings", () -> {
            });

            optionsMenu.getChildren().addAll(btnBack, btnSound, btnVideo, new Text(""), new LineSeparator(), textDescription);

            view = new KeyView(KeyCode.ESCAPE, Color.BLUEVIOLET, 22.0);
            view.setTranslateY(-10);

            hBox = new HBox(15, getUIFactoryService().newText("EXIT", 20.0), view);
            hBox.setTranslateX(getAppWidth() - 270);
            hBox.setTranslateY(515);

            getChildren().addAll(createBackground(getAppWidth(), getAppHeight()), optionsMenu, hBox);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class LoadingScreen extends LoadingScene {
        private final int APP_W = 1344;
        private final int APP_H = 704;

        private Line loadingBar = new Line();

        private ResourceLoadingTask task = new ResourceLoadingTask();

        public LoadingScreen(){
            Pane root = new Pane();
            root.setPrefSize(APP_W, APP_H);

            Rectangle bg = new Rectangle(APP_W, APP_H);

            LoadingCircle loadingCircle = new LoadingCircle();
            loadingCircle.setTranslateX(APP_W - 120);
            loadingCircle.setTranslateY(APP_H - 100);

            Line loadingBarBG = new Line(100, APP_H - 70, APP_W - 100, APP_H - 70);
            loadingBarBG.setStroke(Color.GREY);

            loadingBar.setStartX(100);
            loadingBar.setStartY(APP_H - 70);
            loadingBar.setEndX(100);
            loadingBar.setEndY(APP_H - 70);
            loadingBar.setStroke(Color.WHITE);

            task.progressProperty().addListener((obs, old, newValue) -> {
                double progress = newValue.doubleValue();
                loadingBar.setEndX(100 + progress * (APP_W - 200));
            });

            Thread t = new Thread(task);
            t.start();
            getContentRoot().getChildren().addAll(bg, loadingCircle, loadingBarBG, loadingBar);
        }


        private class LoadingCircle extends Parent{
            private RotateTransition animation;

            public LoadingCircle() {
                Circle circle = new Circle(20);
                circle.setFill(null);
                circle.setStroke(Color.WHITE);
                circle.setStrokeWidth(2);

                Rectangle rect = new Rectangle(20, 20);

                Shape shape = Shape.subtract(circle, rect);
                shape.setFill(Color.WHITE);

                getChildren().add(shape);

                animation = new RotateTransition(Duration.seconds(2.5), this);
                animation.setByAngle(-360);
                animation.setInterpolator(Interpolator.SPLINE(0,0, 1, 1));
                animation.setCycleCount(Animation.INDEFINITE);
                animation.play();
            }
        }

        private class ResourceLoadingTask extends Task<Void> {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < 100; i++) {
                    Thread.sleep((int) (Math.random()) * 100);
                    updateProgress(i + 1, 100);
                }
                return null;
            }
        }
    }
}