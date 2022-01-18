package uk.ac.soton.comp1206.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.util.*;

public class MultiplayerGame extends Game {

    private static final Logger logger = LogManager.getLogger(Game.class);
    /**
     * Create a new multiplayer game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    private final Queue<Integer> receivedPieces;

    public MultiplayerGame(int cols, int rows) {
        super(cols, rows);
        receivedPieces = new LinkedList<>();
    }

    /**
     * Generates a random game piece
     * @return randomly created game piece
     */
    @Override
    public GamePiece spawnPiece() {
        return GamePiece.createPiece(receivedPieces.poll());
    }

    @Override
    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        super.initialiseGame();
        //spawn new pieces to update the random values assigned to current and following pieces
        setCurrentPiece(spawnPiece());
        setFollowingPiece(spawnPiece());
        //update the scene by calling the next piece listener
        updateListener();
    }

    /**
     * Receives a new piece from the multiplayer scene and adds it to the queue
     * @param piece the piece provided by the server
     */
    public void addReceivedPiece(Integer piece){
        receivedPieces.add(piece);
    }

    /**
     * Return the queue that stores the spawned pieces
     * @return
     */
    public Queue<Integer> getReceivedPieces(){
        return this.receivedPieces;
    }
}