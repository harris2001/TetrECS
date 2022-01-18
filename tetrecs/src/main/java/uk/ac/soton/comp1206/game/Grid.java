package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for(var y = 0; y < rows; y++) {
            for(var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }

    /**
     * Gets a game piece and checks if it can be placed at a certain position
     * @param piece for a particular game piece
     * @param x piece x coordinate
     * @param y piece y coordinate
     * @return true if the piece can be placed and false otherwise
     */
    public boolean canPlayPiece(GamePiece piece, int x, int y) {
        int[][] blocks = piece.getBlocks();

        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                // If the user tries to place a block that extends overflows the grid dimensions, return false
                if(x==0 && blocks[0][j] > 0){
                    return false;
                }
                if(x==rows-1 && blocks[2][j] > 0){
                    return false;
                }
                if(y==0 && blocks[i][0] > 0){
                    return false;
                }
                if(y==cols-1 && blocks[i][2] > 0){
                    return false;
                }
                // If there is even one block that is already occupied, return false
                if(blocks[i][j] > 0 && grid[x+i-1][y+j-1].getValue() > 0){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Places the piece block to the game grid
     * @param piece game piece
     * @param x grid
     */
    public boolean playPiece(GamePiece piece, int x, int y){
        if(canPlayPiece(piece,x,y)){
            int[][] blocks=piece.getBlocks();
            for(int i=0; i<3; i++){
                for(int j=0; j<3; j++){
                    if(blocks[i][j] > 0){
                        this.set(x+i-1,y+j-1, piece.getValue());
                    }
                }
            }
            return true;
        }
        return false;
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

}
