package nl.hsleiden.ipsene.controllers;

import com.google.cloud.firestore.DocumentSnapshot;
import nl.hsleiden.ipsene.interfaces.Controller;
import nl.hsleiden.ipsene.interfaces.View;
import nl.hsleiden.ipsene.models.Board;
import nl.hsleiden.ipsene.models.Deck;

public class BoardController implements Controller {

  private Deck cards;
  private TeamController teamController;
  private boolean gameHasEnded = false;
  private final int AMOUNT_OF_PLAYERS;

  Board board;


  public BoardController(int amountOfPlayers, int amountOfTeams) {
    AMOUNT_OF_PLAYERS = amountOfPlayers;

    // todo sent cards array to firebase
    teamController = new TeamController();
  }

  @Override
  public void update(DocumentSnapshot ds) {}

  @Override
  public void registerObserver(View v) {
    board.registerObserver(v);
  }
}
