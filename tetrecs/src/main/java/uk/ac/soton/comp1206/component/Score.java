package uk.ac.soton.comp1206.component;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;

import java.util.Observable;


public class Score extends Pair<String,Integer> implements Comparable<Score> {

    /**
     * Used to manage and sort the scores
     * @param player the name of the player
     * @param value the score they reached
     */
    public Score(String player,Integer value) {
        super(player,value);
    }

    @Override
    /**
     * Used to sort scores based on their value
     */
    public int compareTo(Score o) {
        return -this.getValue().compareTo(o.getValue());
    }

}
