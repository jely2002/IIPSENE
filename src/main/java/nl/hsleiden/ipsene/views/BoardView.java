package nl.hsleiden.ipsene.views;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import nl.hsleiden.ipsene.controllers.BoardController;
import nl.hsleiden.ipsene.interfaces.View;

public class BoardView implements View {

  private final int WIDTH = 1600;
  private final int HEIGHT = 900;

  private Stage primaryStage;

  private static BoardView boardView;

  BoardController boardController;

  public BoardView(Stage s) {
    primaryStage = s;
    loadPrimaryStage((Pane) createInitialPane());
  }

  private void loadPrimaryStage(Pane pane) {
    try {
      Scene scene = new Scene(pane, WIDTH, HEIGHT);
      primaryStage.setScene(scene);
      primaryStage.setTitle("Keezbord-Game");
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Object createInitialPane() {
    Pane pane = new Pane();

    return pane;
  }

  private Pane createUpdatedPane() {
    Pane pane = new Pane();

    return pane;
  }

  @Override
  public void update() {
    loadPrimaryStage(createUpdatedPane());
  }
}
