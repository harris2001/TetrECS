package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

public class PieceBoard extends GameBoard {

    public PieceBoard(double width, double height) {
        super(3, 3, width, height);
        this.build();
        if(height==130){
            this.getBlock(1,1).paintCircle();
        }
    }

    /**
     * Displays the incoming block to the game pane
     * @param block the incoming block
     */
    public void displayPiece(GamePiece block){
        logger.info("Displaying incoming piece");
        int[][] blks = block.getBlocks();
        grid.playPiece(block,0,0);
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                grid.set(i,j,blks[i][j]);
            }
        }
    }
}
