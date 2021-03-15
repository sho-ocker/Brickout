package game.brickout.dialogs;

import com.almasb.fxgl.app.scene.FXGLMenu;
import game.brickout.ui.AppMainMenu;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static com.almasb.fxgl.dsl.FXGL.fire;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class GameOverDialog extends Stage {
    GameOverDialog(String header, String content){
        Pane root = new Pane();

        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.APPLICATION_MODAL);

        Text headerText = new Text(header);
        headerText.setFont(Font.font(20));
        Text contentText = new Text(content);
        contentText.setFont(Font.font(16));

        VBox box = new VBox(10, headerText, contentText);

        Button btnReturn = new Button("Return");
        Button btnAgain = new Button("Play Again");

    //    DialogButton btnQuit = new DialogButton("Return to main menu", () -> fire());
      //  DialogButton btnAgain = new DialogButton("Quit", () -> fireExitToMainMenu());


        root.getChildren().add(box);
    }


    private class DialogButton extends StackPane {
        private final Color SELECTED_COLOR = Color.BLUEVIOLET;
        private final Color NOT_SELECTED_COLOR = Color.WHITE;

        private String name;
        private String description;
        private Runnable action;
        private Text text;
        private Rectangle selector;

        public DialogButton(String name, Runnable action) {
            this.name = name;
            this.action = action;


            text = getUIFactoryService().newText(name, Color.WHITE, 35.0);
            text.fillProperty().setValue(NOT_SELECTED_COLOR);

            selector = new Rectangle(5, 30, Color.WHITE);
            selector.setTranslateX(-20);
            selector.setTranslateY(-2);


            setAlignment(Pos.CENTER_LEFT);
            setFocusTraversable(true);

            selector.visibleProperty().bind(hoverProperty());


            setOnMouseEntered(e -> {
                text.fillProperty().setValue(SELECTED_COLOR);
                text.strokeProperty().setValue(SELECTED_COLOR);
                text.setStrokeWidth(1.5);
            });

            setOnMouseExited(e -> {
                text.fillProperty().setValue(NOT_SELECTED_COLOR);
                text.strokeProperty().setValue(NOT_SELECTED_COLOR);
                text.setStrokeWidth(0);
            });

            setOnMouseClicked(e -> {
                if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1)
                    action.run();
            });

            getChildren().addAll(text, selector);
        }
    }
}
