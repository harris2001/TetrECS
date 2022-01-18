package uk.ac.soton.comp1206.scene;

import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    InstructionsScene instructions;

    Multimedia music;
    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
        //Building a new instruction scene once the menu is called
        instructions = new InstructionsScene(gameWindow);
        //Instantiate multimedia player
         music = new Multimedia();
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());


        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());


        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        menuPane.getStyleClass().add("menu-background");
        VBox menuBox = new VBox();
        menuBox.setAlignment(Pos.CENTER);
        Image title = new Image(MenuScene.class.getResource("/images/TetrECS.png").toExternalForm());
        ImageView imageView = new ImageView(title);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(120);
        imageView.fitWidthProperty().bind(mainPane.widthProperty());
        imageView.setRotate(-5);
        //Transition for the Logo
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(3000),imageView);
        rotateTransition.setByAngle(10);
        rotateTransition.setCycleCount(999);
        rotateTransition.setAutoReverse(true);
        rotateTransition.play();

        menuBox.getChildren().add(imageView);
        mainPane.setCenter(imageView);

        //Adding Menu Options
        VBox menuOptionsBox = new VBox();
        menuOptionsBox.setSpacing(20);
        menuOptionsBox.getStyleClass().add("menu");
        menuOptionsBox.setAlignment(Pos.CENTER);
        Text singlePlayer = new Text("Single Player");
        singlePlayer.getStyleClass().add("menuItem");
        Text multiPlayer = new Text("Multi Player");
        multiPlayer.getStyleClass().add("menuItem");
        Text howToPlay = new Text("How to PLay");
        howToPlay.getStyleClass().add("menuItem");
        Text exit = new Text("Exit");
        exit.getStyleClass().add("menuItem");
        menuOptionsBox.getChildren().add(singlePlayer);
        menuOptionsBox.getChildren().add(multiPlayer);
        menuOptionsBox.getChildren().add(howToPlay);
        menuOptionsBox.getChildren().add(exit);

        mainPane.setBottom(menuOptionsBox);

        singlePlayer.setOnMouseDragOver((event)->music.playAudio("pling.wav"));

        //Redirecting user based on the selected option
        singlePlayer.setOnMouseClicked(this::startGame);
        howToPlay.setOnMouseClicked(this::showInstructions);
        multiPlayer.setOnMouseClicked(this::startMultiplayer);
        exit.setOnMouseClicked(event -> shutdown());
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        music.playMusic("menu.mp3");
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(MouseEvent event) {
        gameWindow.startChallenge();
    }

    /**
     * Handle when the Start Multiplayer Game button is pressed
     * @param event event
     */
    private void startMultiplayer(MouseEvent event) {
        gameWindow.startMultiplayer();
    }
    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void showInstructions(MouseEvent event) {
        gameWindow.showInstructions();
    }

    @Override
    public void shutdown(){
        gameWindow.cleanup();
        System.exit(0);
    }
}
