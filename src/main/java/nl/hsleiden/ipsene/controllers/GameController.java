package nl.hsleiden.ipsene.controllers;

import com.google.cloud.firestore.DocumentSnapshot;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import nl.hsleiden.ipsene.exceptions.GameNotFoundException;
import nl.hsleiden.ipsene.exceptions.ServerConnectionException;
import nl.hsleiden.ipsene.firebase.FirebaseService;
import nl.hsleiden.ipsene.interfaces.Controller;
import nl.hsleiden.ipsene.interfaces.View;
import nl.hsleiden.ipsene.models.Game;
import nl.hsleiden.ipsene.models.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameController implements Controller {

  private static final Logger logger = LoggerFactory.getLogger(GameController.class.getName());

  private final Game game;
  private final FirebaseService firebaseService;

  public GameController(FirebaseService firebaseService) {
    this.game = new Game();
    this.firebaseService = firebaseService;
  }

  @Override
  public void update(DocumentSnapshot document) {
    logger.info("Received update from firebase");
    game.update(document);
  }

  @Override
  public void registerObserver(View v) {
    game.registerObserver(v);
  }

  public String getToken() {
    return game.getToken();
  }

  /**
   * Sets the availability of a player slot.
   *
   * @param id The ID of the slot to set.
   * @param value Whether to set this slot to available or not.
   */
  public void setPlayerAvailable(int id, boolean value) {
    game.getPlayer(id - 1).setAvailable(value);
    push();
  }

  /**
   * Gets if the given player is available (can be joined).
   *
   * @param id The player ID to check the availability for.
   * @return A boolean indicating if the player is available.
   */
  public boolean getPlayerAvailable(int id) {
    Player player = game.getPlayer(id - 1);
    return player.isAvailable();
  }

  /**
   * Checks if the user has already chosen a player. Always check with this method before getting a
   * selected player.
   *
   * @return Boolean which is true when the user has selected a player.
   */
  public boolean hasSelectedPlayer() {
    return game.getOwnPlayer() != null;
  }

  /**
   * Gets the ID of the player the user has selected.
   *
   * @return The ID of the selected player.
   */
  public Integer getSelectedPlayer() {
    return game.getOwnPlayer();
  }

  /**
   * Sets the player selected by the user.
   *
   * @param id The ID to link to the user.
   */
  public void setSelectedPlayer(Integer id) {
    game.setOwnPlayer(id);
  }

  /**
   * Joins an already existing game with a token
   *
   * @param token The token that the user has provided
   * @throws GameNotFoundException Gets thrown when no game with the given token was found
   * @throws ServerConnectionException Gets thrown when no connection with firebase could be made
   */
  public void join(String token) throws GameNotFoundException, ServerConnectionException {
    try {
      DocumentSnapshot document = firebaseService.get(token);
      if (document == null) {
        throw new GameNotFoundException("No game with token " + token + " was found");
      } else {
        game.update(document);
        firebaseService.listen(game.getToken(), this);
      }
    } catch (ExecutionException | InterruptedException e) {
      logger.error(e.getMessage(), e);
      throw new ServerConnectionException();
    }
  }

  /**
   * Hosts/starts a new empty game
   *
   * @return Returns the token of the new game
   * @throws ServerConnectionException Gets thrown when no connection with firebase could be made
   */
  public void host() throws ServerConnectionException {
    try {
      firebaseService.set(game.getToken(), game.serialize());
      firebaseService.listen(game.getToken(), this);
    } catch (ExecutionException | InterruptedException e) {
      logger.error(e.getMessage(), e);
      throw new ServerConnectionException();
    }
  }

  /** Pushes new data to firebase. Use this method after changing the model. */
  private void push() {
    try {
      firebaseService.set(game.getToken(), game.serialize());
      firebaseService.listen(game.getToken(), this);
    } catch (ExecutionException | InterruptedException e) {
      logger.error(e.getMessage(), e);
    }
  }

  public void quit() {
    Platform.exit();
  }
}
