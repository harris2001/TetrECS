package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class InstructionsScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {
    }

    @Override
    public void build() {
        logger.info("Displaying instructions");

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var stackPane = new StackPane();
        stackPane.setMaxWidth(gameWindow.getWidth());
        stackPane.setMaxHeight(gameWindow.getHeight());
        stackPane.getStyleClass().add("menu-background");
        root.getChildren().add(stackPane);

        BorderPane instructionsPane = new BorderPane();
        stackPane.getChildren().add(instructionsPane);

        //Adding title and description
        VBox descriptionBox = new VBox();
        descriptionBox.setAlignment(Pos.CENTER);
        Text title = new Text("Instructions");
        title.getStyleClass().add("title");
        Text description = new Text("TetrECS is a fast-paced gravity-free block placement game, where you must survive by clearing rows through careful placement of the");
        Text description_cont = new Text("upcoming blocks before the time runs out. Lose all 3 lives and you're destroyed!");
        description.getStyleClass().add("instructions");
        description_cont.getStyleClass().add("instructions");
        descriptionBox.getChildren().add(title);
        descriptionBox.getChildren().add(description);
        descriptionBox.getChildren().add(description_cont);

        instructionsPane.setTop(descriptionBox);

        //Adding the instructions image
        Image instructions = new Image(InstructionsScene.class.getResource("/images/Instructions.png").toExternalForm());
        ImageView imageView = new ImageView(instructions);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(300);
        instructionsPane.setCenter(imageView);

        //Adding gamePieces
        VBox gamePiecesBox = new VBox();
        gamePiecesBox.setAlignment(Pos.CENTER);
        gamePiecesBox.setSpacing(10);
        Text gamePieces = new Text("Game Pieces");
        gamePieces.getStyleClass().add("title");
        gamePiecesBox.getChildren().add(gamePieces);

        for(int i=0; i<3; i++){
            HBox hbox = new HBox();
            hbox.setSpacing(10);
            hbox.setAlignment(Pos.CENTER);
            for(int j=0; j<5; j++){
                PieceBoard pieceBoard = new PieceBoard(50,50);
                GamePiece gamePiece = GamePiece.createPiece(i*5+j);
                pieceBoard.displayPiece(gamePiece);
                hbox.getChildren().add(pieceBoard);
            }
            gamePiecesBox.getChildren().add(hbox);
        }
        instructionsPane.setBottom(gamePiecesBox);
    }
}
