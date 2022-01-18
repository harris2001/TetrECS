package uk.ac.soton.comp1206.ui;

import javafx.animation.FadeTransition;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.soton.comp1206.component.Score;

import java.util.ArrayList;

public class LeaderBoard extends ScoresList{

    private ArrayList<Score>scores;
    /**
     * Scorelist UI Constructor
     *
     * @param local true-> local scores /False-> online scores
     */
    public LeaderBoard(boolean local, ArrayList<Score>scores) {
        super(local);
        this.scores = scores;
    }

    /**
     * Produces an animation arraylist and passes it to the animate class to animate
     */
    @Override
    public void reveal() {
        ArrayList<FadeTransition> transitionArray = new ArrayList<>();
        for (int i = 0; i < Math.min(10, scores.size()); i++) {
            var score = scores.get(i);
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

}
