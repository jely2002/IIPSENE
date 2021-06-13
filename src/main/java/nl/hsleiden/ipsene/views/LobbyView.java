package nl.hsleiden.ipsene.views;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import nl.hsleiden.ipsene.controllers.GameController;
import nl.hsleiden.ipsene.controllers.LobbyController;
import nl.hsleiden.ipsene.interfaces.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LobbyView implements View {

  private static final Logger logger = LoggerFactory.getLogger(LobbyView.class.getName());

  private final LobbyController lobbyController;

  private final int WIDTH = 1600;
  private final int HEIGHT = 900;
  private final Stage primaryStage;
  private final String RED = "#FF0000";
  private final String BLUE = "#0000FF";
  private final String GREEN = "#00FF00";
  private final String YELLOW = "#FFFF00";

  private Button player1Join;
  private Button player2Join;
  private Button player3Join;
  private Button player4Join;
  private Button startButton;

  private boolean player1Available = false;
  private boolean player2Available = false;
  private boolean player3Available = false;
  private boolean player4Available = false;

  private Label waitingForPlayersLabel;

  public LobbyView(Stage primaryStage, LobbyController lobbyController) {
    this.lobbyController = lobbyController;
    this.primaryStage = primaryStage;
    lobbyController.registerObserver(this);
    loadPrimaryStage(createPane());
  }

  private Pane createPane() {
    Pane pane = new Pane();

    player1Available = lobbyController.getPlayerAvailable(1);
    player2Available = lobbyController.getPlayerAvailable(2);
    player3Available = lobbyController.getPlayerAvailable(3);
    player4Available = lobbyController.getPlayerAvailable(4);

    String lobbyID = lobbyController.getToken();

    Label title = lobbyHeaderLabelBuilder(lobbyID);
    ViewHelper.setNodeCoordinates(title, 10, 10);

    Label player1Display = playerDisplayLabel("Player 1", player1Available, RED);
    ViewHelper.setNodeCoordinates(player1Display, 10, 150);

    Label player2Display = playerDisplayLabel("Player 2", player2Available, BLUE);
    ViewHelper.setNodeCoordinates(player2Display, 10, 295);

    Label player3Display = playerDisplayLabel("Player 3", player3Available, GREEN);
    ViewHelper.setNodeCoordinates(player3Display, 10, 520);

    Label player4Display = playerDisplayLabel("Player 4", player4Available, YELLOW);
    ViewHelper.setNodeCoordinates(player4Display, 10, 660);

    this.player1Join = joinButtonBuilder(1, player1Available);
    player1Join.setId("1");
    ViewHelper.setNodeCoordinates(player1Join, 220, 150);
    player1Join.addEventFilter(MouseEvent.MOUSE_CLICKED, playerButtonClicked);

    this.player2Join = joinButtonBuilder(2, player2Available);
    player2Join.setId("2");
    ViewHelper.setNodeCoordinates(player2Join, 220, 295);
    player2Join.addEventFilter(MouseEvent.MOUSE_CLICKED, playerButtonClicked);

    this.player3Join = joinButtonBuilder(3, player3Available);
    player3Join.setId("3");
    ViewHelper.setNodeCoordinates(player3Join, 220, 520);
    player3Join.addEventFilter(MouseEvent.MOUSE_CLICKED, playerButtonClicked);

    this.player4Join = joinButtonBuilder(4, player4Available);
    player4Join.setId("4");
    ViewHelper.setNodeCoordinates(player4Join, 220, 660);
    player4Join.addEventFilter(MouseEvent.MOUSE_CLICKED, playerButtonClicked);

    this.startButton = startButtonBuilder();
    ViewHelper.setNodeCoordinates(startButton, 1400, 700);
    startButton.addEventFilter(MouseEvent.MOUSE_CLICKED, playButtonClicked);

    this.waitingForPlayersLabel = WaitingForPlayersLabelBuilder("Waiting on players");
    ViewHelper.setNodeCoordinates(waitingForPlayersLabel, 10, 790);
    WaitingForPlayersThread wfpt = new WaitingForPlayersThread(waitingForPlayersLabel);
    Thread wfptThread = new Thread(wfpt);
    wfptThread.setDaemon(true); // Zorgt ervoor dat deze thread samen afsluit met de View
    wfptThread.start();

    ImageView imageView = ViewHelper.createLogo(400);
    ViewHelper.setNodeCoordinates(imageView, 600, 200);

    pane.getChildren()
        .addAll(title, player1Display, player2Display, player3Display, player4Display);
    pane.getChildren().addAll(player1Join, player2Join, player3Join, player4Join, startButton);
    pane.getChildren().addAll(waitingForPlayersLabel, imageView);
    return pane;
  }

  private void loadPrimaryStage(Pane pane) {
    try {
      Pane root = pane;
      Scene scene = new Scene(root, WIDTH, HEIGHT);
      primaryStage.setScene(scene);
      primaryStage.setTitle("Keezbord");
      primaryStage.show();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  private Label lobbyHeaderLabelBuilder(String lobbyID) {
    Label lbl = new Label();

    lbl.setText("LobbyID: " + lobbyID);
    lbl.setStyle("-fx-font-size: 75; -fx-font-family: 'Comic Sans MS'");
    ViewHelper.applyDropShadow(lbl);

    return lbl;
  }

  private Label WaitingForPlayersLabelBuilder(String txt) {
    Label lbl = new Label();

    lbl.setText(txt);
    lbl.setStyle("-fx-font-size: 75; -fx-font-family: 'Comic Sans MS'");
    ViewHelper.applyDropShadow(lbl);

    return lbl;
  }

  private Label playerDisplayLabel(String txt, boolean isAvailable, String hex) {
    Label lbl = new Label();
    String avIndicator;

    if (isAvailable) {
      avIndicator = "#03a9f4";
    } else {
      avIndicator = "#FF0000";
    }

    lbl.setText(txt);
    lbl.setPrefWidth(200);
    lbl.setPrefHeight(125);
    lbl.setStyle(
        "-fx-font-size: 35; -fx-padding: 20; -fx-border-width: 10; "
            + "-fx-background-color: #AAAAAA; -fx-border-color: "
            + avIndicator
            + avIndicator
            + avIndicator
            + hex);

    return lbl;
  }

  private Button joinButtonBuilder(int id, boolean isAvailable) {
    Button btn = new Button();
    String bgColor;
    String buttonText;

    if (lobbyController.hasSelectedPlayer() && lobbyController.getSelectedPlayer() == id) {
      bgColor = "#00FFFF";
      buttonText = "Joined";
    } else if (!isAvailable) {
      bgColor = RED;
      buttonText = "Taken";
    } else {
      bgColor = GREEN;
      buttonText = "Join";
    }

    btn.setPrefWidth(125);
    btn.setPrefHeight(125);
    btn.setText(buttonText);
    btn.setStyle("-fx-font-size: 20; -fx-background-color: " + bgColor);
    ViewHelper.applyDropShadow(btn);

    return btn;
  }

  private Button startButtonBuilder() {
    Button button = new Button();
    button.setText("start game");
    button.setPrefWidth(125);
    button.setPrefHeight(125);
    button.setStyle("-fx-font-size: 20; -fx-background-color: " + RED);
    ViewHelper.applyDropShadow(button);
    return button;
  }

  private Button buttonBuilder(String txt, boolean isAvailable) {
    Button btn = new Button();
    String bgColor;

    if (isAvailable) {
      bgColor = "#00FF00";
    } else {
      bgColor = "#FF0000";
    }

    btn.setPrefWidth(125);
    btn.setPrefHeight(125);
    btn.setText(txt);
    btn.setStyle("-fx-font-size: 20; -fx-background-color: " + bgColor);
    ViewHelper.applyDropShadow(btn);

    return btn;
  }

  EventHandler<MouseEvent> playerButtonClicked =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
          Object source = e.getSource();
          if (!(source instanceof Button)) return;
          Button clickedButton = (Button) e.getSource();
          int playerId = Integer.parseInt(clickedButton.getId());
          if (clickedButton.getText().equals("Taken")) return;
          if (lobbyController.hasSelectedPlayer()) {
            if (clickedButton.getText().equals("Joined")) {
              lobbyController.setSelectedPlayer(null);
              lobbyController.setPlayerAvailable(playerId, true);
            } else if (clickedButton.getText().equals("Join")) {
              lobbyController.setPlayerAvailable(lobbyController.getSelectedPlayer(), true);
              lobbyController.setPlayerAvailable(playerId, false);
              lobbyController.setSelectedPlayer(playerId);
            }
          } else {
            if (clickedButton.getText().equals("Join")) {
              lobbyController.setSelectedPlayer(playerId);
              lobbyController.setPlayerAvailable(playerId, false);
            }
          }
        }
      };

  EventHandler<MouseEvent> playButtonClicked =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
          lobbyController.setGameHasStarted();
          startGame();
        }
      };

  // TODO finetune and check if boardstage really comes after lobbyView
  private void startGame() {
    // if either all slots are filled or the game has started
    if (lobbyController.hasGameStarted()
        || (!player1Available && !player2Available && !player3Available && !player4Available)) {
      GameController gameController = lobbyController.startGame(this);
      new BoardView(primaryStage, gameController);
      gameController.serialize();
    }
  }

  @Override
  public void update() {
    Platform.runLater(() -> loadPrimaryStage(createPane()));
    Platform.runLater(() -> startGame());
  }
}
