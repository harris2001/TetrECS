package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.Score;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.util.ArrayList;

public class HighScoreScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private final BorderPane mainPane;
    private final Game game;
    private final ArrayList<Score>scores;

    /**
     * Create a high score scene in the case where a player overpasses a high score value
     * @param game the current game
     * @param gameWindow the game window to use the communicator
     * @param scores array of scores
     */
    public HighScoreScene(Game game, GameWindow gameWindow, ArrayList<Score>scores){
        super(gameWindow);
        this.game = game;
        this.scores = scores;
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        mainPane = new BorderPane();
        mainPane.setMaxWidth(gameWindow.getWidth());
        mainPane.setMaxHeight(gameWindow.getHeight());

        mainPane.getStyleClass().add("menu-background");
        root.getChildren().add(mainPane);
    }

    @Override
    public void initialise() {
        Multimedia music = new Multimedia();
        music.playMusic("end.wav");
    }

    @Override
    public void build() {
        var scene =new ScoresScene(gameWindow,game,scores,"");
        scene.initialise();
        scene.build();
        Text newHighScore = new Text("NEW HIGH SCORE");
        newHighScore.setFill(Color.WHITE);
        newHighScore.setFont(Font.font("Orbitron",30));
        mainPane.setTop(newHighScore);

        BorderPane.setAlignment(mainPane.getTop(), Pos.CENTER);
        var input = new TextField();
        mainPane.setCenter(input);
        input.setOnKeyPressed((e)->{
            if(e.getCode()== KeyCode.ENTER){
                scene.writeOnlineScore(input.getText());
                gameWindow.cleanup();
                gameWindow.loadScene(new ScoresScene(gameWindow,game,scores,input.getText()));
            }
        });
    }
}
