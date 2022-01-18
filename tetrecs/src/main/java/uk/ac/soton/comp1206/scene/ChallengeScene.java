package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.component.Score;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.util.ArrayList;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

    private GamePiece current;
    private GamePiece following;
    private GameBoard board;
    private PieceBoard pieceBoard;
    private PieceBoard pieceBoardSmall;
    private Multimedia music;
    private Rectangle rectangle;
    private final IntegerProperty highScoreProperty = new SimpleIntegerProperty(0);
    protected VBox rightBox;
    protected VBox highScoreBox;
    protected BorderPane mainPane;
    protected VBox levelBox;
    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        mainPane = new BorderPane();
        mainPane.setMaxWidth(gameWindow.getWidth());
        mainPane.setMaxHeight(gameWindow.getHeight());

        mainPane.getStyleClass().add("menu-background");
        root.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(),(float)gameWindow.getWidth()/2,(float)gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
        //Handle block hover in
        board.subscribeToHover(this::gameBlockHovered);

        // Display lives
        HBox currentGame = new HBox();
        currentGame.setAlignment(Pos.CENTER);
        currentGame.setSpacing(180);
        HBox.setHgrow(currentGame,Priority.ALWAYS);

        //Adding score text and label to the current game pane
        VBox scoreBox = new VBox();
        scoreBox.setAlignment(Pos.CENTER);
        Text scoreLabel = new Text("Score");
        scoreLabel.getStyleClass().add("heading");
        Text scoreText = new Text();
        scoreText.getStyleClass().add("score");
        //binding scoreField with scoreProperty
        scoreText.textProperty().bind(game.getScoreProperty().asString());
        scoreBox.getChildren().add(scoreLabel);
        scoreBox.getChildren().add(scoreText);
        currentGame.getChildren().add(scoreBox);

        //Adding title to the current game pane
        Text title = new Text("Challenge Mode");
        title.getStyleClass().add("title");
        currentGame.getChildren().add(title);

        //Adding lives text and label to the current game pane
        VBox livesBox = new VBox();
        livesBox.setAlignment(Pos.CENTER);
        Text livesLabel = new Text("Lives");
        livesLabel.getStyleClass().add("heading");
        livesBox.getChildren().add(livesLabel);
        Text livesText = new Text();
        //binding lives text with the lives property
        livesText.textProperty().bind(game.getLivesProperty().asString());
        livesText.getStyleClass().add("lives");
        livesBox.getChildren().add(livesText);
        currentGame.getChildren().add(livesBox);

        //Adding currentGameBox to the top of the game pane
        mainPane.setTop(currentGame);

        //Adding Components to the right side
        rightBox = new VBox();
        rightBox.setSpacing(20);
        rightBox.setAlignment(Pos.CENTER);

        //Creating High Score components
        getHighScore();
        highScoreBox = new VBox();
        highScoreBox.setAlignment(Pos.CENTER);
        Text highScoreLbl = new Text("HighScore");
        Text highScoreText = new Text("100");
        highScoreLbl.getStyleClass().add("heading");
        highScoreText.getStyleClass().add("level");
        highScoreText.textProperty().bind(highScoreProperty.asString());
        highScoreBox.getChildren().add(highScoreLbl);
        highScoreBox.getChildren().add(highScoreText);
        //Adding high score components to box
        rightBox.getChildren().add(highScoreBox);

        //Creating level components
        levelBox = new VBox();
        levelBox.setAlignment(Pos.CENTER);
        Text levelLbl = new Text("Level");
        Text levelText = new Text();
        levelLbl.getStyleClass().add("heading");
        levelText.getStyleClass().add("level");
        levelText.textProperty().bind(game.getLevelProperty().asString());
        levelBox.getChildren().add(levelLbl);
        levelBox.getChildren().add(levelText);
        //Adding level components to box
        rightBox.getChildren().add(levelBox);

        Text incomingText = new Text("Incoming");
        incomingText.getStyleClass().add("heading");
        rightBox.getChildren().add(incomingText);
        //Adding incoming piece to the right of the game pane
        pieceBoard = new PieceBoard(130,130);
        //Adding incoming piece to the right of the game pane
        pieceBoardSmall = new PieceBoard(80,80);

        //Subscribing to the NewPieceListener to update the current piece every time a new one arrives
        game.setNextPieceListener((current,following)->{
            this.current=current;
            this.following = following;
            pieceBoard.displayPiece(current);
            pieceBoardSmall.displayPiece(following);
            board.setPiece(board.getPos(), current);

            if(game.getScore()> highScoreProperty.get()){
                highScoreProperty.bind(game.getScoreProperty());
            }
        });

        //Subscribing board to Right click listener
        board.setOnRightClick(()->{
            music.playAudio("rotate.wav");
            game.rotateCurrentPiece(board);
            pieceBoard.displayPiece(current);
            pieceBoardSmall.displayPiece(following);
        });
        //Subscribing pieceboard to Left click listener
        pieceBoard.setOnRightClick(()->{
            music.playAudio("rotate.wav");
            game.rotateCurrentPiece(board);
            pieceBoard.displayPiece(current);
            pieceBoardSmall.displayPiece(following);
        });

        //Subscribing to line clear
        game.setOnLineClearedListener(coordinates -> board.fadeOut(coordinates));

        rightBox.getChildren().add(pieceBoard);
        rightBox.getChildren().add(pieceBoardSmall);

        mainPane.setRight(rightBox);

        rectangle = new Rectangle(0,10,20,30);
        int maxWidth = gameWindow.getWidth();
        rectangle.setWidth(maxWidth);
        rectangle.setHeight(30);

        game.setOnGameLoop((time)->{
            if(game.getLives()==-1){
                Platform.runLater(()->{
                    game.timer.cancel();
                    gameWindow.cleanup();
                    gameWindow.loadScene(new ScoresScene(gameWindow,game,this.getScores(),"You"));
                });
            }
            timer(time);
        });


        mainPane.setBottom(rectangle);
    }

    protected ArrayList<Score> getScores() {
        return new ArrayList<>();
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Handle hover over a block
     * @param gameBlock which block
     * @param in or out
     */
    private void gameBlockHovered(GameBlock gameBlock,boolean in,boolean canPlace) {
        if(in)
            game.blockHoveredIn(gameBlock,canPlace);
        else
            game.blockHoveredOut(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");
        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
        controls();
    }

    /**
     * Creating keyboard listeners for the game
     */
    public void controls(){
        //Initializing mulitmedia player
        music = new Multimedia();

        //Listen to keypress after the game starts
        this.getScene().setOnKeyPressed((e)->{
            //Exiting game mode
            if(e.getCode()==KeyCode.ESCAPE){
                shutdown();
            }
            //Rotate right
            else if(e.getCode()==KeyCode.Q || e.getCode()==KeyCode.E){
                music.playAudio("rotate.wav");
                game.rotateCurrentPiece(board);
                pieceBoard.displayPiece(current);
                pieceBoardSmall.displayPiece(following);
            }
            //Rotate left
            else if(e.getCode()==KeyCode.Z || e.getCode()==KeyCode.C){
                music.playAudio("rotate.wav");
                game.rotateCurrentPiece(board);
                game.rotateCurrentPiece(board);
                game.rotateCurrentPiece(board);
                pieceBoard.displayPiece(current);
                pieceBoardSmall.displayPiece(following);
            }
            else if(e.getCode()==KeyCode.SPACE || e.getCode()==KeyCode.R){
                game.swapCurrentPiece(board);
                music.playAudio("rotate.wav");
            }
            else if(e.getCode() == KeyCode.T){

                Platform.runLater(()-> {
                    if((this instanceof MultiplayerScene)) {
                        ((MultiplayerScene) this).sendInput();
                    }
                });
            }
            else {
                boolean moved=false;
                GameBlock posPrevious= board.getPos();
                if (e.getCode()==KeyCode.UP){
                    board.setPiece(board.getBlock(board.getPos().getX(),Math.max(board.getPos().getY()-1,0)),current);
                    moved=true;
                }
                else if (e.getCode()==KeyCode.DOWN){
                    board.setPiece(board.getBlock(board.getPos().getX(),Math.min(board.getPos().getY()+1,board.getColumnCount()-1)),current);
                    moved=true;
                }
                else if (e.getCode()==KeyCode.LEFT){
                    board.setPiece(board.getBlock(Math.max(board.getPos().getX()-1,0),board.getPos().getY()),current);
                    moved=true;
                }
                else if (e.getCode()==KeyCode.RIGHT){
                    board.setPiece(board.getBlock(Math.min(board.getPos().getX()+1,board.getRowCount()-1),board.getPos().getY()),current);
                    moved=true;
                }
                if(moved){
                    board.blockHovered(MouseEvent.MOUSE_EXITED,posPrevious,current);
                    board.getPos().moveCurrent();
                    board.getPos().pointCurrent();
                    board.blockHovered(MouseEvent.MOUSE_ENTERED,board.getPos(),current);
                }
                else if(e.getCode()==KeyCode.ENTER|| e.getCode()==KeyCode.X){
                    if(!((this instanceof MultiplayerScene) || !((MultiplayerScene) this).getInput().isDisabled()))
                        board.dropPiece(board.getPos());
                }
            }
        });
    }

    /**
     * Escapes the current scene and returns to the previous
     */
    @Override
    protected void shutdown() {
        logger.info("shutting down");
        game.timer.cancel();
        gameWindow.cleanup();
        gameWindow.showMenu();
    }

    /**
     * Game timer displayed at the bottom of the game scene
     * @param time for the player to respond
     */
    private void timer(int time){
        rectangle.setWidth(gameWindow.getWidth());
        rectangle.setFill(Color.GREEN);

        Timeline timeline1 = new Timeline();

        Color color1 = Color.YELLOW;
        Color color2 = Color.ORANGE;
        Color color3 = Color.RED;

        timeline1.getKeyFrames().add(
            new KeyFrame(Duration.millis(time/4), new KeyValue(rectangle.fillProperty(),color1,Interpolator.EASE_BOTH))
        );
        timeline1.getKeyFrames().add(
            new KeyFrame(Duration.millis(time/2), new KeyValue(rectangle.fillProperty(),color2,Interpolator.EASE_BOTH))
        );
        timeline1.getKeyFrames().add(
            new KeyFrame(Duration.millis(time*3/4), new KeyValue(rectangle.fillProperty(),color3,Interpolator.EASE_BOTH))
        );
        timeline1.getKeyFrames().add(
        new KeyFrame(Duration.millis(time), new KeyValue(rectangle.widthProperty(),0, Interpolator.EASE_BOTH))
        );
        timeline1.play();
    }

    /**
     * Compares the current score to the default highscores
     */
    protected void getHighScore(){
        gameWindow.getCommunicator().addListener(message-> Platform.runLater(() -> {
            if(message.split(" ")[0].equals("HISCORES")) {
                this.highScoreProperty.set(Integer.parseInt(message.split(":")[1].split(" ")[0].split("\n")[0]));
            }
        }));
        gameWindow.getCommunicator().send("HISCORES DEFAULT");
    }

    /**
     * Game Board getter
     * @return gameboard
     */
    protected GameBoard gameBoard(){
        return this.board;
    }

}
