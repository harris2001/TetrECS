package uk.ac.soton.comp1206.component;

import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class RotatingCircles extends BorderPane{
    /**
     * Additional component used for enhancing the UI
     * Create 3 rotating circles spinning at different angles for various durations
     */
    public RotatingCircles(){
        var loadingBox = new StackPane();
        Circle circle1 = new Circle(0,0,10);
        circle1.getStyleClass().add("circle1");
        Circle circle2 = new Circle(0,0,25);
        circle2.getStyleClass().add("circle2");
        Circle circle3 = new Circle(0,0,50);
        circle3.getStyleClass().add("circle3");
        loadingBox.getChildren().addAll(circle1,circle2,circle3);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(100,0,0,30));
        this.setLeft(loadingBox);

        setRotate(circle1,true,360,10);
        setRotate(circle2,true,180,18);
        setRotate(circle3,true,145,24);
    }

    /**
     * Animate the transition
     * @param c the circle
     * @param reverse true-> rotate backwards false-> don't
     * @param angle the angle of rotation
     * @param duration of the transition
     */
    private void setRotate(Circle c, boolean reverse, int angle, int duration){
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(duration),c);

        rotateTransition.setAutoReverse(reverse);

        rotateTransition.setByAngle(angle);
        rotateTransition.setDelay(Duration.millis(0));
        rotateTransition.setRate(3);
        rotateTransition.setCycleCount(18);
        rotateTransition.play();
        }
}
