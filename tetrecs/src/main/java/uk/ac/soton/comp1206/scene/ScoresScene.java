package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.Score;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.*;

import java.io.*;
import java.util.*;

public class ScoresScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private final Multimedia music;
    private final ListProperty localScoresProperty;
    private final ListProperty remoteScoresProperty;
    private final ArrayList<Score> remoteScoresList;
    private final IntegerProperty highScoreProperty;
    private final Game game;
    private BorderPane borderPane;
    private ScoresList localScoresBox;
    private ScoresList onlineScoresBox;

    /**
     * Constructor
     * @param gameWindow required by superclass
     */
    public ScoresScene(GameWindow gameWindow,Game game,ArrayList<Score>scores,String username){
        super(gameWindow);
        music = new Multimedia();
        this.game = game;
        ArrayList<Score> localScoresList = new ArrayList<>(10);
        this.remoteScoresList = new ArrayList<>(10);
        ObservableList<Score> localObservable = FXCollections.observableArrayList(localScoresList);
        ObservableList<Score> remoteObservable = FXCollections.observableArrayList(this.remoteScoresList);
        this.localScoresProperty = new SimpleListProperty(localObservable);
        this.remoteScoresProperty = new SimpleListProperty(remoteObservable);
        this.highScoreProperty = new SimpleIntegerProperty(0);
        this.onlineScoresBox = new ScoresList(false);
        if(scores.isEmpty()) {
            this.localScoresBox = new ScoresList(true);
        }
        else{
            this.localScoresBox = new LeaderBoard(true,scores);
        }
        localScoresBox.getScoreListProperty().bind(this.localScoresProperty);
        onlineScoresBox.getScoreListProperty().bind(this.remoteScoresProperty);
    }

    @Override
    public void initialise() {
        music.playMusic("end.wav",gameWindow);
        game.timer.cancel();
    }

    @Override
    public void build() {

        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        //update local score value
        ArrayList<Score> scores = loadScores();
        for (Score score : scores) {
            updateLocalScores(score);
        }
        //adding current user score to the list
        updateLocalScores(new Score("You", game.getScore()));

        //update online score value
        gameWindow.getCommunicator().addListener(communication -> {
            if (communication.split(" ")[0].equals("HISCORES")) {
                Platform.runLater(() -> {
                    ArrayList<Score> onlineScores = loadOnlineScores(communication);
                    int counter =0;
                    for (Score score : onlineScores) {
                        counter++;
                        updateOnlineScores(score);
                        if(counter>10)
                            break;
                    }
                    onlineScoresBox.reveal();
                });
            }
        });


        gameWindow.getCommunicator().send("HISCORES UNIQUE");
        createUI();
    }

    /**
     * Creating the user interface
     */
    private void createUI(){
        borderPane = new BorderPane();
        root.getChildren().add(borderPane);
        borderPane.getStyleClass().add("menu-background");
        ImageView title = new ImageView(new Image(MenuScene.class.getResource("/images/TetrECS.png").toExternalForm()));
        title.setPreserveRatio(true);
        title.setFitHeight(110);
        Text gameOver = new Text("Game Over");
        gameOver.getStyleClass().add("bigtitle");
        Text highScores = new Text("High Scores");
        highScores.getStyleClass().add("title");
        //Page content
        var content = new VBox();
        content.setSpacing(20);
        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().add(title);
        content.getChildren().add(gameOver);
        content.getChildren().add(highScores);

        //Display scores
        HBox scoresBox = new HBox();
        HBox.setHgrow(scoresBox,Priority.ALWAYS);
        scoresBox.setSpacing(100);
        //local scores
        localScoresBox.reveal();
        scoresBox.getChildren().add(localScoresBox);
        scoresBox.setAlignment(Pos.TOP_CENTER);
        //online scores
        onlineScoresBox.reveal();
        scoresBox.getChildren().add(onlineScoresBox);
        scoresBox.setAlignment(Pos.TOP_CENTER);

        content.getChildren().add(scoresBox);


        borderPane.setCenter(content);

        //Write local scores
        writeScores();
    }
    /**
     * Update local score list
     * @param newScore new User-Score pair
     */
    public void updateLocalScores(Score newScore){
        //if the score worth to be on the list, we dump one entry
        if(this.localScoresProperty.size()==10){
            var lastScore = (Score)this.localScoresProperty.get(9);
            if(lastScore.getValue()<=newScore.getValue()) {
                this.localScoresProperty.remove(9);
            }
        }
        this.localScoresProperty.add(newScore);
        Collections.sort(this.localScoresProperty);
    }
    /**
     * Update online score list
     * @param newScore new User-Score pair
     */
    public void updateOnlineScores(Score newScore){
        //if the score worth to be on the list, we dump one entry
        if(this.remoteScoresProperty.size()==10){
            Score lastScore = (Score) this.remoteScoresProperty.get(9);
            if(lastScore.getValue()<=newScore.getValue()) {
                this.remoteScoresProperty.remove(9);
            }
        }
        this.remoteScoresProperty.add(newScore);
        Collections.sort(this.remoteScoresProperty);
    }

    /**
     * Updates scores from scores.txt log file
     * @return an arraylist of scores
     */
    private ArrayList<Score> loadScores() {
        var scores = new ArrayList<Score>();
        try {
            Reader reader = new FileReader("scores.txt");
            BufferedReader bufferedReader= new BufferedReader(reader);
            String score;
            while((score=bufferedReader.readLine())!=null){
                scores.add(new Score(score.split(":")[0],Integer.parseInt(score.split(":")[1])));
            }
            reader.close();
        } catch (Exception e) {
            for(int i=10000; i>=1000; i-=1000){
                scores.add(new Score("Oli",i));
            }
        }
        return scores;
    }

    /**
     * Load online scores from the server
     * @param communication the message received
     * @return scores array
     */
    private ArrayList<Score> loadOnlineScores(String communication){
        ArrayList<Score> scores = new ArrayList<>();
        String[] recv = communication.split(" ")[1].split("\n");
        int counter = 0;
        for(String line : recv){
            counter++;
            String[] score = line.split(":");
            Score newScore = new Score(score[0],Integer.parseInt(score[1]));
            scores.add(newScore);
            if(counter>10)
                break;
        }
        return scores;
    }


    /**
     * Writes scores to scores.txt file
     */
    private void writeScores(){
        try {
            Writer writer = new FileWriter("scores.txt");
            for (Object o : localScoresProperty) {
                Score score = (Score) o;
                writer.write(score.getKey() + ":" + score.getValue() + "\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send highscore to the server if the user has achieved a new high score
     * @param username user preferred username
     */
    protected void writeOnlineScore(String username){
        if(game.getScore()>remoteScoresList.get(remoteScoresList.size()-1).getValue()){
            gameWindow.getCommunicator().send("HIGHSCORE "+username+":"+game.getScore());
        }
        gameWindow.getCommunicator().send("HIGHSCORES");
    }

}
