package uk.ac.soton.comp1206.component;

import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.BlockHoveredListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.game.GamePiece;

import java.util.HashSet;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 *
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 *
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    protected static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    private final int cols;

    /**
     * Number of rows in the board
     */
    private final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    private final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    private final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;

    /**
     * The listener to call when a specific block is hovered
     */
    private BlockHoveredListener blockHoveredListener;

    /**
     * The listener to call when right click is pressed
     */
    private RightClickedListener rightClickedListener;

    //Storing current position
    private GameBlock pos;
    private GamePiece piece;

    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;

        //Build the GameBoard
        build();
    }

    /**
     * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols number of columns for internal grid
     * @param rows number of rows for internal grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols,rows);

        //Build the GameBoard
        build();
    }

    /**
     * Get a specific block from the GameBoard, specified by it's row and column
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}",cols,rows);

        setMaxWidth(width);
        setMaxHeight(height);

        setGridLinesVisible(true);

        blocks = new GameBlock[cols][rows];

        for(var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                createBlock(x,y);
            }
        }
        pos = this.getBlock(2,2);
        this.piece = GamePiece.createPiece(3);
    }

    /**
     * Create a block at the given x and y position in the GameBoard
     * @param x column
     * @param y row
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Add to the GridPane
        add(block,x,y);

        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x,y));

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) -> blockClicked(e,block));

        //Handle hovering on enter
        block.setOnMouseEntered((e) -> this.setPiece(block,this.piece));

        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     * @param event mouse event
     * @param block block clicked on
     */
    private void blockClicked(MouseEvent event, GameBlock block) {
        if((event.getButton() == MouseButton.SECONDARY && this.getWidth()==400) || (event.getButton() == MouseButton.PRIMARY && this.getWidth()==132)){
            rightClick();
            return;
        }
        if(blockClickedListener != null) {
            blockClickedListener.blockClicked(block);
        }
    }

    /**
     * Subscribe to hover listener
     * @param listener to subscribe
     */
    public void subscribeToHover(BlockHoveredListener listener){
        this.blockHoveredListener=listener;
    }

    /**
     * Notify hover listener that the block was hovered
     * @param e Mouse event
     * @param block block that we are hovering over
     * @param piece current game piece
     */
    public void blockHovered(EventType<MouseEvent> e, GameBlock block,GamePiece piece){
        if(blockHoveredListener!=null) {
            pieceShadow(this.pos, piece,false);
            if(e==MouseEvent.MOUSE_ENTERED){
                pieceShadow(block, piece,true);
            }
            else if(e==MouseEvent.MOUSE_EXITED){
                pieceShadow(block, piece,false);
            }
        }
    }

    /**
     * Itereate over all the blocks of the piece to create their shadow
     * @param block the current block that we hover above
     * @param piece the game piece
     * @param create true if it creates the shadow / false if it restores the previous
     */
    private void pieceShadow(GameBlock block, GamePiece piece,boolean create) {
        boolean canPlayPiece = grid.canPlayPiece(piece,block.getX(),block.getY());
        int[][] blocks = piece.getBlocks();
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                if (blocks[i][j] > 0) {
                    int x = block.getX() + i - 1;
                    int y = block.getY() + j - 1;
                    if (x < 0 || y < 0 || x >= this.getRowCount() || y >= this.getColumnCount()) {
                        continue;
                    }
                    GameBlock piecePos = this.getBlock(x, y);
                    blockHoveredListener.hover(piecePos, create, canPlayPiece);
                }
            }
        }
    }


    /**
     * Drops the piece to the selected block
     * @param block the block that the piece is dropped to
     */
    public void dropPiece(GameBlock block){
        if(blockClickedListener != null) {
            blockClickedListener.blockClicked(block);
        }
    }

    /**
     * Notifies listener that a right click was pressed on the block
     */
    void rightClick(){
        if(rightClickedListener!=null) {
            rightClickedListener.rightClick();
        }
    }

    /**
     * Sets right click listener
     * @param listener Right click listener
     */
    public void setOnRightClick(RightClickedListener listener){
        this.rightClickedListener=listener;
    }

    /**
     * Pos getter
     * @return pos the current position the user points at
     */
    public GameBlock getPos() {
        return pos;
    }

    /**
     * Pos setter
     * @param pos the value of the current position the user points at
     */
    public void setPiece(GameBlock pos, GamePiece gamePiece) {
        this.blockHovered(MouseEvent.MOUSE_EXITED,this.pos,this.piece);
        this.pos = pos;
        this.piece = gamePiece;
        this.blockHovered(MouseEvent.MOUSE_ENTERED,this.pos,this.piece);
    }

    /**
     * Piece getter
     * @return piece
     */
    public GamePiece getPiece() {
        return this.piece;
    }

    /**
     * Trigers a set of blocks to fade out
     * @param coordinates a set of coordinates
     */
    public void fadeOut(HashSet<GameBlockCoordinate> coordinates){
        for(GameBlockCoordinate coordinate: coordinates){
            this.getBlock(coordinate.getX(),coordinate.getY()).fadeOut();
        }
    }
}
