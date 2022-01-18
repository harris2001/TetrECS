package uk.ac.soton.comp1206.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.soton.comp1206.component.Score;

import java.util.ArrayList;

public class ScoresList extends VBox {

    private final SimpleListProperty<Score>scoreListProperty;

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DEEPSKYBLUE,
            Color.DARKTURQUOISE,
            Color.BLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    /**
     * Scorelist UI Constructor
     * @param local true-> local scores /False-> online scores
     */
    public ScoresList(boolean local){
        this.setAlignment(Pos.CENTER);
        this.scoreListProperty = new SimpleListProperty<>();
        Text title;
        if(local){
            title = new Text("Local Scores");
        }
        else{
            title = new Text("Online scores");
        }
        title.getStyleClass().add("scorelist");
        this.getChildren().add(title);
    }

    /**
     * Produces an animation arraylist and passes it to the animate class to animate
     */
    public void reveal() {
        ArrayList<FadeTransition> transitionArray = new ArrayList<>();
        for (int i = 0; i < Math.min(10, scoreListProperty.getSize()); i++) {
            var score = scoreListProperty.get(i);
            Text scoreElement = new Text(score.getKey() + ": " + score.getValue());
            scoreElement.setFill(COLOURS[i]);
            scoreElement.getStyleClass().add("scoreitem");
            scoreElement.setOpacity(0);
            this.getChildren().add(scoreElement);
            FadeTransition transition = new FadeTransition(new Duration(500), scoreElement);
            transition.setFromValue(0);
            transition.setToValue(1);
            transitionArray.add(transition);
        }
        if(transitionArray.size()>0)
            animate(transitionArray,0,transitionArray.size());
    }

    /**
     * Animates scores transition
     * @param transitions the transitions for every one of the ten scores
     * @param i the current index of the transition
     * @param N the maximum score list size
     */
    protected void animate(ArrayList<FadeTransition> transitions,int i,int N){
        Transition transition = transitions.get(i);
        transition.play();
        transition.setOnFinished((e)->{
            if(i+1==N) {
                return;
            }
            animate(transitions,i+1,N);
        });
    }

    /**
     * Simple list property setter (used to bind with the scene list property)
     * @return score list property holding the scores
     */
    public SimpleListProperty<Score> getScoreListProperty() {
        return scoreListProperty;
    }

}
