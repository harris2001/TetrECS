package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

public class IntroScene extends BaseScene {

    GameWindow game;
    Multimedia music;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public IntroScene(GameWindow gameWindow) {
        super(gameWindow);
        music = new Multimedia();
        this.game=gameWindow;
    }

    @Override
    public void initialise() {
        music.playMusic("intro.wav");
    }

    @Override
    public void build() {

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());


        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        var image = new Image(ChallengeScene.class.getResource("/images/ECSGames.png").toExternalForm());
        var ecs = new ImageView(image);
        ecs.setPreserveRatio(true);
        ecs.setFitWidth(250);
        menuPane.getChildren().add(ecs);
        FadeTransition ft = new FadeTransition(Duration.millis(3000), ecs);
        ft.setFromValue(0);
        ft.setToValue(1);
        FadeTransition ft2 = new FadeTransition(Duration.millis(2000), ecs);
        ft2.setFromValue(1);
        ft2.setToValue(0);
        FadeTransition ft3 = new FadeTransition(Duration.millis(100), mainPane);
        ft3.setFromValue(0);
        ft3.setToValue(1);

        SequentialTransition sq = new SequentialTransition(ft, ft2, ft3);
        sq.play();
        sq.setOnFinished((e) -> game.showMenu());
    }
}
