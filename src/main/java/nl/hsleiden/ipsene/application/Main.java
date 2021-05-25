package nl.hsleiden.ipsene.application;

import javafx.application.Application;
import javafx.stage.Stage;
import nl.hsleiden.ipsene.views.AccountView;
import nl.hsleiden.ipsene.views.LobbyView;

public class Main extends Application {

  public static void run(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    LobbyView b = new LobbyView(primaryStage);
  }
}
