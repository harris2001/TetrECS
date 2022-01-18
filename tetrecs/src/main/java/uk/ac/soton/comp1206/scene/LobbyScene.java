package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.soton.comp1206.component.RotatingCircles;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class LobbyScene extends BaseScene{
    private Timer timer;
    private final Multimedia music = new Multimedia();
    private static final Logger logger = LogManager.getLogger(LobbyScene.class);
    private BorderPane mainPane;
    private VBox gamesBox;
    private TextField name;
    private VBox channels;
    private String joined;
    private Communicator communicator;
    private BorderPane messagePane;
    private ScrollPane messageBox;
    private BorderPane channelPane;
    private TextField input;
    private Button start;
    private String nickName;
    private HBox users;
    private VBox messageVbox;
    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.DEEPPINK,
            Color.PINK,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.AQUA,
            Color.AQUAMARINE,
    };
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     */
    private RotatingCircles rotatingCircles;

    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {
        this.communicator = gameWindow.getCommunicator();
        this.rotatingCircles = new RotatingCircles();
        music.playMusic("menu.mp3");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                communicator.send("LIST");
            }
        },1,5000);
        this.getScene().setOnKeyPressed((e)->{
            if(e.getCode()==KeyCode.ESCAPE){
                shutdown();
            }
        });
        handleMessages();
    }

    @Override
    public void build() {

        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        StackPane stackPane = new StackPane();
        mainPane = new BorderPane();
        mainPane.setMaxWidth(gameWindow.getWidth());
        mainPane.setMaxHeight(gameWindow.getHeight());

        stackPane.setMaxWidth(gameWindow.getWidth());
        stackPane.setMaxHeight(gameWindow.getHeight());

        ImageView img1 = new ImageView(new Image(this.getClass().getResource("/images/background.jpg").toExternalForm()));
        img1.setFitWidth(gameWindow.getWidth());
        ImageView img2 = new ImageView(new Image(this.getClass().getResource("/images/logo.png").toExternalForm()));
        stackPane.getChildren().addAll(img1,img2,mainPane);
        root.getChildren().add(stackPane);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(2000), img2);
        scaleTransition.setToX(1.1f);
        scaleTransition.setToY(1.1f);
        scaleTransition.setCycleCount(800);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();

        Text title = new Text("Multiplayer");
        title.getStyleClass().add("title");
        BorderPane.setAlignment(title, Pos.CENTER);
        mainPane.setTop(title);
        gamesBox = new VBox();
        gamesBox.setAlignment(Pos.CENTER);
        gamesBox.setSpacing(10);
        VBox.setVgrow(gamesBox,Priority.ALWAYS);
        Text currentGames = new Text("Current Games");
        currentGames.getStyleClass().add("heading");
        Text newGame = new Text("Host New Game");
        newGame.getStyleClass().add("channelItem");
        channels = new VBox();
        channels.setAlignment(Pos.CENTER);
        channels.setSpacing(10);
        channels.setPadding(new Insets(0,0,0,10));
        newGame.setOnMouseClicked(this::createNewGame);
        name = new TextField();
        name.setAlignment(Pos.CENTER);
        name.setFont(new Font("Orbitron",15));
        name.setDisable(true);
        name.setOpacity(0);
        gamesBox.getChildren().addAll(currentGames,newGame,name,channels);
        gamesBox.setPadding(new Insets(20,0,10,10));
        mainPane.setLeft(gamesBox);
        gamesBox.setPrefWidth(150);
        channelPane = new BorderPane();

        messagePane = new BorderPane();
        channelPane.setCenter(messagePane);
        BorderPane.setMargin(channelPane,new Insets(0,50,50,50));
        messagePane.getStyleClass().add("gameBox");
        messageBox = new ScrollPane();
        messageVbox = new VBox();
        messageBox.setContent(messageVbox);
        messageBox.getStyleClass().add("scroller");
        messagePane.setCenter(messageBox);
        //setting up bottom
        VBox bottom = new VBox();
        bottom.setSpacing(10);
        input = new TextField();
        input.setOnKeyPressed((e)->{
            if(e.getCode()==KeyCode.ENTER){
                if(input.getText().split(" ")[0].equals("/nick")){
                    communicator.send("NICK "+input.getText().split(" ")[1]);
                }
                else {
                    communicator.send("MSG " + input.getText());
                }
                input.clear();
            }
        });
        bottom.getChildren().add(input);
        HBox options = new HBox();
        options.setSpacing(250);
        start = new Button("Start Game");
        start.setAlignment(Pos.CENTER_LEFT);
        start.setOnMouseClicked((e)-> communicator.send("START"));
        start.setDisable(true);
        start.setVisible(false);
        Button end = new Button("Leave Game");
        end.getStyleClass().add("leaveBtn");
        start.setAlignment(Pos.CENTER_RIGHT);
        start.getStyleClass().add("startBtn");
        end.setOnMouseClicked((e)->{
            communicator.send("PART");
            start.setDisable(true);
            start.setVisible(false);
            joined=null;
            communicator.send("LIST");
            loading();
        });
        start.setAlignment(Pos.CENTER_RIGHT);
        options.getChildren().addAll(start,end);
        bottom.getChildren().add(options);
        messagePane.setBottom(bottom);

        mainPane.setCenter(channelPane);
        loading();

    }

    /**
     * Creates a new game when the button is clicked
     * @param event the mouse event
     */
    private void createNewGame(MouseEvent event) {
        name.setDisable(false);
        name.setOpacity(1);
        name.setOnKeyPressed((e)->{
            if(e.getCode()==KeyCode.ENTER){
                Text myChanel = new Text(name.getText());
                channels.getChildren().add(myChanel);
                communicator.send("CREATE " + name.getText());
                communicator.send("LIST");
                name.setOpacity(0);
                name.clear();
            }
        });
    }

    /**
     * Handles incoming communicator messages
     */
    private void handleMessages(){
        communicator.addListener(message -> Platform.runLater(()->{
            if(message.split(" ")[0].equals("ERROR")){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Error");
                errorAlert.setContentText(message.replace("ERROR",""));
                errorAlert.showAndWait();
            }
            if(message.split(" ")[0].equals("JOIN")){
                if(joined==null){
                    music.playAudio("pling.wav");
                    for(Node node: channels.getChildren()){
                        if(node.toString().split("\"")[1].equals(message.split(" ")[1])){
                            node.getStyleClass().add("mychannel");
                            joined=message.split(" ")[1];
                            channelPane.setDisable(false);
                            channelPane.setVisible(true);
                            Text txt = new Text(joined);
                            txt.getStyleClass().add("heading-colored");
                            channelPane.setTop(txt);
                            BorderPane.setAlignment(txt, Pos.CENTER);
                            BorderPane.setMargin(channelPane.getTop(),new Insets(20,0,10,0));
                        }
                    }
                    gamesBox.getChildren().remove(rotatingCircles);
                    mainPane.setCenter(channelPane);
                    gamesBox.getChildren().add(rotatingCircles);
                }

            }
            if(message.split(" ")[0].equals("CHANNELS")){
                gamesBox.getChildren().remove(channels);
                channels = new VBox();
                channels.setAlignment(Pos.CENTER);
                channels.setSpacing(10);
                for(String channel : message.replace("CHANNELS","").strip().split("\n")) {
                    Text txt = new Text(channel);
                    if(txt.getText().equals(joined)) {
                        txt.getStyleClass().add("mychannel");
                    }
                    else {
                        txt.getStyleClass().add("channelItem");
                    }
                    txt.setOnMouseClicked((e)-> communicator.send("JOIN "+txt.getText()));
                    channels.getChildren().add(txt);
                }
                gamesBox.getChildren().remove(rotatingCircles);
                gamesBox.getChildren().add(channels);
                gamesBox.getChildren().add(rotatingCircles);
            }
            if(message.split(" ")[0].equals("HOST")) {
                start.setDisable(false);
                start.setVisible(true);
            }
            if(message.split(" ")[0].equals("USERS")) {
                users = new HBox();
                for(String user: message.replace("USERS","").strip().split("\n")){
                    Text userText = new Text(user+" ");
                    if(user.equals(nickName)) {
                        userText.getStyleClass().add("nick");
                    }
                    else {
                        userText.getStyleClass().add("user");
                    }
                    users.getChildren().add(userText);
                }
                messagePane.setTop(users);
            }
            if(message.split(" ")[0].equals("NICK")) {
                users = new HBox();
                Text userText = new Text(message.split(" ")[1]+" ");
                userText.getStyleClass().add("nick");
                users.getChildren().add(userText);
                this.nickName=message.split(" ")[1];
            }
            if(message.split(" ")[0].equals("MSG")) {
                String player = message.split(" ")[1].split(":")[0];
                String msg = message.replace("MSG","").split(":")[1];
                var textBox = new HBox();
                Text text = new Text("<"+player+"> ");
                Text messageTxt = new Text(msg);
                textBox.getChildren().add(text);
                textBox.getChildren().add(messageTxt);
                text.getStyleClass().add("messages");
                messageTxt.getStyleClass().add("messages");
                messageTxt.getStyleClass().add("messages");
                text.setFill(COLOURS[Math.abs(player.hashCode()%11)]);
                messageTxt.setFill(Color.WHITE);
                messageVbox.getChildren().add(textBox);
                music.playAudio("message.wav");
            }
            if(message.split(" ")[0].equals("PARTED")){
                messageBox = new ScrollPane();
                messageVbox = new VBox();
                messageBox.setContent(messageVbox);
                messageBox.getStyleClass().add("scroller");
                messagePane.setCenter(messageBox);

            }
            if(message.split(" ")[0].equals("START")) {
                gameWindow.cleanup();
                gameWindow.loadScene(new MultiplayerScene(gameWindow));
            }
        }));
    }

    /**
     * Escapes the current scene and returns to the previous
     */
    @Override
    protected void shutdown() {
        logger.info("shutting down");
        communicator.send("PART");
        this.timer.cancel();
        gameWindow.cleanup();
        gameWindow.showMenu();
    }

    /**
     * Removes previous conversations
     */
    private void loading() {
        mainPane.setPrefWidth(gameWindow.getScene().getWidth());
        mainPane.setCenter(new BorderPane());
        BorderPane.setAlignment(mainPane,Pos.CENTER_LEFT);
    }
}
