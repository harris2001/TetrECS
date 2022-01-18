package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.PINK,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.BLUE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    private final GameBoard gameBoard;

    private final double width;
    private final double height;

    private Integer storedValue=0;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);
    private boolean center;

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);

    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if(value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            Color color = COLOURS[value.get()];
            Color associated = Color.rgb((int)(color.getRed()*255),(int)(color.getGreen()*255),(int)(color.getBlue()*255),0.7);
            paintColor(COLOURS[value.get()],associated);
        }
    }

    /**
     * Creates a circle in the middle of the current piece
     */
    public void paintCircle(){
        center=true;
    }

    /**
     * Handles hoverIn
     * @param canPlace true-> paint gray/ false ->paint red
     */
    public void hoverIn(boolean canPlace){
        storedValue=getValue();
        if(canPlace) {
            paintColor(Color.rgb(128,128,128,0.5),Color.rgb(128,128,128,0.5));
        }
        else
            paintColor(Color.rgb(255,0,0,0.5),Color.rgb(255,0,0,0.5));
    }
    public void hoverOut(){
        value.setValue(storedValue);
        paint();
    }

    public void pointCurrent(){
        if(value.get() == 0){
            paintColor(Color.RED,Color.RED);
        }
    }

    public void moveCurrent(){
        if(value.get() == 0){
            paintEmpty();
        }
    }
    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Fill
        gc.setFill(Color.rgb(0,0,0,0.3));
        gc.fillRect(0,0, width, height);

        //Border
        gc.setStroke(Color.WHITE);
        gc.strokeRect(0,0,width,height);

        storedValue=getValue();
    }

    /**
     * Paint this canvas with the given colour
     * @param primaryColour of the tile
     * @param secondaryColour of the tile
     */
    private void paintColor(Paint secondaryColour,Paint primaryColour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Colour fill
        gc.setFill(primaryColour);
        gc.fillRect(0,0, width, height);
        gc.setFill(secondaryColour);
        gc.fillPolygon(new double[]{0,0,width},new double[]{height,0,height},3);
        gc.setFill(Color.rgb(0,0,0,0.3));
        gc.fillRect(0,0, width, height);

        if(center){
            gc.setFill(Color.rgb(0,0,0,0.6));
            gc.fillOval(0, 0, width, height);
        }

        //Border
        gc.setStroke(Color.WHITE);
        gc.strokeRect(0,0,width,height);
        storedValue=getValue();

    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bindBidirectional((Property<Number>) input);
    }

    /**
     * Fade out animation when a line is cleared
     */
    public void fadeOut(){
        logger.info("Fading Out");
        paintEmpty();
        paintColor(Color.GREEN,Color.GREEN);

        AnimationTimer timer = new AnimationTimer() {
            double opacity=1;
            @Override
            public void handle(long l) {
                paintColor(Color.rgb(0,255,0,opacity),Color.rgb(0,255,0,opacity));
                opacity-=0.05;
                if(opacity<=0){
                    stop();
                }
            }
        };
        timer.start();

    }


}
