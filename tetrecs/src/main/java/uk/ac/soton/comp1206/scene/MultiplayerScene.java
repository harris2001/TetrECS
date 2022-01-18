package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.Score;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MultiplayerScene extends ChallengeScene{

    private static final Logger logger = LogManager.getLogger(LobbyScene.class);
    private Communicator communicator;
    public TextField input;
    public Text msg;
    private MultiplayerGame multiplayerGame;
    private Timer timer2;

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.DEEPPINK,
            Color.PINK,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.AQUA,
            Color.AQUAMARINE,
    };
    private final ArrayList<Score> scores;
    private int lastScore;

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        this.scores = new ArrayList<>();
    }

    @Override
    public void build() {
        super.build();
        logger.info("Building Multiplayer scene");

        msg = new Text("In-Game Chat: Press T to send a chat message");
        msg.getStyleClass().add("user");
        input = new TextField();
        input.setFont(new Font("Orbitron",15));
        var centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().add(mainPane.getCenter());
        centerBox.getChildren().add(msg);
        centerBox.getChildren().add(input);
        input.setDisable(true);
        input.setVisible(false);
        mainPane.setCenter(centerBox);

        //Adding Components to the right side
        var userBox = new VBox();
        rightBox.getChildren().remove(highScoreBox);
        rightBox.getChildren().remove(levelBox);
        var versus = new Text("Versus");
        versus.getStyleClass().add("heading");
        var incomming = rightBox.getChildren().get(0);
        var current = rightBox.getChildren().get(1);
        var following = rightBox.getChildren().get(2);
        rightBox = new VBox();
        rightBox.setSpacing(20);
        VBox.setVgrow(rightBox, Priority.ALWAYS);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.getChildren().add(versus);
        rightBox.getChildren().add(userBox);
        rightBox.getChildren().add(incomming);
        rightBox.getChildren().add(current);
        rightBox.getChildren().add(following);
        mainPane.setRight(rightBox);
        var users = new ArrayList<String>();

        gameWindow.getCommunicator().addListener((message)-> Platform.runLater(()->{
            if(message.split(" ")[0].equals("HISCORES")){
                lastScore = Integer.parseInt(message.split(" ")[1].split("\n")[9].split(":")[1]);
            }
            if(message.split(" ")[0].equals("MSG")){
                StringBuilder send = new StringBuilder("<" + message.split(" ")[1].split(":")[0] + "> ");
                String[] edited = message.split(":")[1].split(" ");
                for (String s : edited) {
                    send.append(s).append(" ");
                }
                msg.setText(send.toString());
            }
            if (message.split(" ")[0].equals("PIECE")) {
                multiplayerGame.addReceivedPiece(Integer.parseInt(message.split(" ")[1]));
            }
            if (message.split(" ")[0].equals("SCORES")) {
                for(String user: message.split(" ")[1].split("\n")){
                    int pos=-1;
                    for(int i=0; i<users.size(); i++){
                        if(users.get(i).equals(user.split(":")[0])) {
                            pos = i;
                            break;
                        }
                    }
                    var username = user.split(":")[0];
                    var score = user.split(":")[1];
                    var dead = user.split(":")[2];
                    var text = new Text("<"+username+">  :  "+score);
                    text.setFont(new Font("Orbitron",15));
                    DropShadow shadow = new DropShadow();
                    text.setEffect(shadow);
                    text.setFill(COLOURS[Math.abs(username.hashCode()%11)]);
                    if(pos==-1) {
                        users.add(username);
                        userBox.getChildren().add(text);
                        scores.add(new Score(username,Integer.parseInt(score)));
                    }
                    else {
                        scores.set(pos,new Score(username,Integer.parseInt(score)));
                        if(dead.equals("DEAD")){
                            text = new Text(username);
                            text.setStrikethrough(true);
                            text.setFont(new Font("Orbitron",15));
                            text.setEffect(shadow);
                            text.setFill(COLOURS[Math.abs(username.hashCode()%11)]);
                        }
                        userBox.getChildren().set(pos,text);
                    }
                }
            }
        }));

        communicator = gameWindow.getCommunicator();
        // Request new pieces from server
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
        //Get high scores
        communicator.send("HISCORES");
        timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                communicator.send("SCORES");
                communicator.send("SCORE "+multiplayerGame.getScore());
                communicator.send("LIVES "+ multiplayerGame.getLives());
                if(multiplayerGame.getReceivedPieces().size()<10)
                    communicator.send("PIECE");
                if(multiplayerGame.getLives()==-1){
                    Platform.runLater(()->{
                        communicator.send("DIE");
                        timer2.cancel();
                        multiplayerGame.timer.cancel();
                        gameWindow.cleanup();
                        var scene =new ScoresScene(gameWindow,game,scores,"");
                        scene.initialise();
                        scene.build();
                        if(game.getScore()>lastScore) {
                            gameWindow.loadScene(new HighScoreScene(multiplayerGame, gameWindow, scores));
                        }
                        else{
                            gameWindow.cleanup();
                            gameWindow.loadScene(new ScoresScene(gameWindow, game, scores, "You"));
                        }
                    });
                }
                StringBuilder board = new StringBuilder();
                for(int j=0; j<gameBoard().getColumnCount(); j++){
                    for(int i=0; i<gameBoard().getRowCount(); i++){
                        board.append(gameBoard().getBlock(j, i).getValue()).append(" ");
                    }
                }
                communicator.send("BOARD "+board);
            }
        },1,700);
    }

    public TextField getInput() {
        return input;
    }
    @Override
    protected ArrayList<Score> getScores() {
        return scores;
    }

    @Override
    protected void shutdown() {
        timer2.cancel();
        logger.info("shutting down");
        gameWindow.getCommunicator().send("DIE");
        gameWindow.cleanup();
        gameWindow.showMenu();
    }

    /**
     * Setup the game object and model
     */
    @Override
    public void setupGame() {
        logger.info("Starting a new challenge");
        //start a new scorescene
        //Start new game
        multiplayerGame = new MultiplayerGame(5, 5);
        game = multiplayerGame;
    }
    public void sendInput(){
        input.setDisable(false);
        input.setVisible(true);
        input.setOnKeyPressed((k) -> {
            if (k.getCode() == KeyCode.ENTER) {
                gameWindow.getCommunicator().send("MSG " + ((MultiplayerScene) this).getInput().getText());
                input.setVisible(false);
                input.setDisable(true);
                input.clear();
            }
        });
    }
}
