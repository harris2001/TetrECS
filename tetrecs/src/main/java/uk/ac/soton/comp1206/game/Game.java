package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.util.*;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);
    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * The current piece that user needs to place in the grid
     */
    private GamePiece currentPiece;

    /**
     * The current piece that user needs to place in the grid
     */
    private GamePiece followingPiece;
    /**
     * Simple integer properties that can be trigger events
     */
    private final IntegerProperty scoreProperty;
    private final IntegerProperty levelProperty;
    private final IntegerProperty livesProperty;
    private final IntegerProperty multiplierProperty;

    /**
     * Instantiating next piece listener
     */
    private NextPieceListener nextPieceListener;
    /**
     * Instantiating line cleared listener
     */
    private LineClearedListener lineClearedListener;
    /**
     * Instantiating game loop listener
     */
    private GameLoopListener gameLoopListener;

    //Defining a multimedia player
    Multimedia music = new Multimedia();
    public Timer timer;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);

        Random r = new Random();
        int n = r.nextInt(15);
        //Initializing the next and following piece
        currentPiece = GamePiece.createPiece(n);
        n = r.nextInt(15);
        followingPiece = GamePiece.createPiece(n);

        //Initialization of bindable integer properties
        scoreProperty = new SimpleIntegerProperty(0);
        levelProperty = new SimpleIntegerProperty(0);
        livesProperty = new SimpleIntegerProperty(3);
        multiplierProperty = new SimpleIntegerProperty(1);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        //Call game loop every getTimerDelay mills
        gameLoop();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        Multimedia music = new Multimedia();
        music.playMusic("game_start.wav");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        logger.info("Block ({},{}) pressed",x,y);

        if(grid.playPiece(currentPiece,x,y)){
            afterPiece();
            nextPiece();
            music.playAudio("place.wav");
            //reset the timer
            if(timer!=null)
                timer.cancel();
            gameLoopListener.loop(getTimerDelay());
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    gameLoop();
                }
            }, getTimerDelay());
        }
        else{
            music.playAudio("fail.wav");
        }
    }

    /**
     * Handle when hover over a block
     * @param block the block under the mouse
     */
    public void blockHoveredIn(GameBlock block,boolean canPlace) {
        block.hoverIn(canPlace);
    }
    /**
     * Handle when hover away from a block
     * @param block the block previously under the mouse
     */
    public void blockHoveredOut(GameBlock block) {
        block.hoverOut();
    }

    /**
     * Generates a random game piece
     * @return randomly created game piece
     */
    public GamePiece spawnPiece() {
        Random r = new Random();
        int n = r.nextInt(15);
        return GamePiece.createPiece(n);
    }

    /**
     * Clears full lines
     */
    private void afterPiece(){
        var full_rows = new ArrayList<Integer>();
        var full_columns = new ArrayList<Integer>();

        //used to call score method
        int totalLines;
        int totalBlocks=0;
        var cleared=new HashSet<GameBlockCoordinate>();

        //check for full rows
        for(int i=0; i<rows; i++){
            boolean full_r = true;
            for(int j=0; j<cols; j++){
                if(grid.get(i,j) == 0){
                    full_r = false;
                    break;
                }
            }
            if(full_r)
                full_rows.add(i);
        }
        //check for full columns
        for(int j=0; j<cols; j++){
            boolean full_c = true;
            for(int i=0; i<rows; i++){
                if(grid.get(i,j) == 0){
                    full_c = false;
                    break;
                }
            }
            if(full_c)
                full_columns.add(j);
        }
        totalLines=full_columns.size()+full_rows.size();
        //Clearing blocks of full lines
        for(int r:full_rows){
            for(int j=0; j<cols; j++){
                //increase the amount of blocks cleared
                totalBlocks += 1;
                grid.set(r,j,0);
                cleared.add(new GameBlockCoordinate(r,j));
            }
        }
        for(int c:full_columns){
            for(int i=0; i<rows; i++){
                if(grid.get(i,c)==0) {
                    continue;
                }
                //increase the amount of blocks cleared if they haven't been counted already
                totalBlocks += 1;
                grid.set(i,c,0);
                cleared.add(new GameBlockCoordinate(i,c));
            }
        }
        notifyLineClearedListener(cleared);
        score(totalLines,totalBlocks);
        int levelup = getLevel();
        setLevelProperty(getScore()/1000);
        if(getLevel()-levelup>0){
            music.playAudio("level.wav");
        }
    }

    /**
     * Replaces the current and following piece and calls the next Piece listeners
     */
    public void nextPiece() {
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        updateListener();
        logger.info("Current piece is now: "+currentPiece);
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Calculate the score based on the lines & blocks cleared and multiplier
     * @param lines number of lines cleared
     * @param blocks number of blocks cleared
     */
    private void score(int lines, int blocks){
        int score = getScore();
        int increase = lines * blocks * 10 * getMultiplier();
        logger.info("{} lines and {} blocks cleared => Score increases by {}",lines,blocks,increase);
        score += increase;
        setScoreProperty(score);

        if(increase>0) {
            //increasing multiplier
            setMultiplierProperty(getMultiplier() + 1);
        }
        else{
            setMultiplierProperty(1);
        }
    }


    /**
     * Properties getters
     */
    public IntegerProperty getScoreProperty(){
        return scoreProperty;
    }
    public IntegerProperty getLevelProperty(){
        return levelProperty;
    }
    public IntegerProperty getLivesProperty(){
        return livesProperty;
    }
    public IntegerProperty getMultiplierProperty(){
        return multiplierProperty;
    }
    /**
     * Properties setters
     */
    public void setScoreProperty(int score){
        scoreProperty.set(score);
    }
    public void setLevelProperty(int level){
        levelProperty.set(level);
    }
    public void setLivesProperty(int lives){
        livesProperty.set(lives);
    }
    public void setMultiplierProperty(int multiplier){
        multiplierProperty.set(multiplier);
    }
    /**
     * Properties values getters
     */
    public int getScore(){
        return scoreProperty.get();
    }
    public int getLevel(){
        return levelProperty.get();
    }
    public int getLives(){
        return livesProperty.get();
    }
    public int getMultiplier(){
        return multiplierProperty.get();
    }

    /**
     * Updating the listener for the upcoming piece
     */
    public void updateListener(){
        //The listener might not have yet been instantiated
        if(nextPieceListener!=null) {
            nextPieceListener.nextPiece(currentPiece,followingPiece);
        }
    }

    protected NextPieceListener getNextPieceListener(){
        return nextPieceListener;
    }

    /**
     * Used for setting up the next piece listener
     * @param listener the listener with its implementation
     */
    public void setNextPieceListener(NextPieceListener listener){
        this.nextPieceListener = listener;
        //We catch up the listener regarding the current piece
        listener.nextPiece(currentPiece,followingPiece);
    }

    /**
     * Rotates current piece
     */
    public void rotateCurrentPiece(GameBoard board){
        logger.info("Rotating current piece");
        //Need to clear previous hovered position first
        board.blockHovered(MouseEvent.MOUSE_EXITED, board.getPos(), currentPiece);
        //Rotate current piece
        currentPiece.rotate();
        //Call to update shadow position with the new piece
        board.setPiece(board.getPos(), currentPiece);
    }

    /**
     * Swaps pieces
     */
    public void swapCurrentPiece(GameBoard board) {
        logger.info("Swapping pieces");
        GamePiece tmp = currentPiece;
        currentPiece = followingPiece;
        followingPiece = tmp;
        nextPieceListener.nextPiece(currentPiece, followingPiece);
        //Call to update shadow position with the new piece
        board.setPiece(board.getPos(),currentPiece);
    }

    /**
     * Subscribe to line cleared listener
     * @param listener line cleared listener
     */
    public void setOnLineClearedListener(LineClearedListener listener){
        this.lineClearedListener = listener;
    }

    /**
     * Notify listeners when a line is cleared
     * @param lines the set of Block Coordinates that were cleared
     */
    private void notifyLineClearedListener(HashSet<GameBlockCoordinate>lines){
        if(this.lineClearedListener!=null){
            this.lineClearedListener.clear(lines);
        }
    }

    /**
     * Returns the delay for the player to make their move
     * @return the delay
     */
    public int getTimerDelay(){
        return Math.max(2500,12000-(500*getLevel()));
    }

    /**
     * The game loop function gets called every time the time is up
     * and one live is lost at the end of the loop
     */
    protected void gameLoop(){
        if(timer!=null) {
            timer.cancel();
            setLivesProperty(getLives()-1);
            nextPiece();
            music.playAudio("lifelose.wav");
        }
        gameLoopListener.loop(getTimerDelay());
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                gameLoop();
            }
        },getTimerDelay());
    }


    /**
     * Subscribes listener to gameLoopListener
     * @param listener for the gameLoop
     */
    public void setOnGameLoop(GameLoopListener listener){
        this.gameLoopListener = listener;
    }

    public void setCurrentPiece(GamePiece piece){
        currentPiece=piece;
    }
    public void setFollowingPiece(GamePiece piece){
        followingPiece=piece;
    }
    public GamePiece getCurrentPiece(){
        return currentPiece;
    }
    public GamePiece getFollowingPiece(){
        return followingPiece;
    }
    protected GameLoopListener getGameLoopListener(){
        return gameLoopListener;
    }
}
