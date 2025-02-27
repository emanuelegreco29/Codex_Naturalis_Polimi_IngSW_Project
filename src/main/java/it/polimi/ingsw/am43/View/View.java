package it.polimi.ingsw.am43.View;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.*;

import java.io.IOException;

public interface View {
    /**
     * A method that is run by a separate {@link Thread}, it takes
     * all the incoming messages and puts them in a queue. If the
     * incoming message is a chat message, immediately parse it
     * without adding it to the queue, to have a real-time chat.
     */
    void getTraffic();

    /**
     * A method that runs periodically on the main {@link Thread},
     * it takes the messages in the queue and processes them.
     *
     * @throws IOException if an I/O error occurs
     */
    void execute() throws IOException;

    /**
     * A method that processes the incoming messages.
     * It analyzes the type of the message and calls the appropriate
     * method.
     *
     * @param message the incoming message
     * @throws IOException if an I/O error occurs
     */
    void viewHandler(Message message) throws IOException;

    /**
     * A method called when the game comes to an end. It prints out
     * the winner and the scoreboard.
     *
     * @param message the message containing the needed information
     */
    void endGame(endGameMsg message);

    /**
     * A method that updates the locally saved hand, based on the placed
     * {@link CardSide} and the drawn {@link PlayableCard}.
     *
     * @param message the message containing the needed information
     */
    void updateLocalHand(newInHandMsg message) ;

    /**
     * A method that updates the local client with all the
     * game information, after each turn. It is used both
     * after a turn or when a game is reloaded (it prints
     * the last turn played to all players).
     * The information shown are:
     * - int of the player who should play next
     * - array of the scores
     * - the last on ground card
     * - the index of the last on ground card
     * - placed of the last player who played
     *
     * @param message the message containing all useful information
     */
    void displayGameInfo(gameInfoMsg message);

    /**
     * A method that prints out the placeable cards in player's hand and asks
     * the player which one it wants to place. After that, it asks
     * for the coordinates of the deployed card on which it wants to place it.
     * The information is later sent to the server.
     *
     * @param message the message containing the placeable cards in player's hand
     * @throws IOException if an I/O error occurs
     */
    void displayPlaceableInHand(placeableCardsMsg message) throws IOException;

    /**
     * A method run when a player is rejoining a loaded game.
     * It prints the usernames chosen for this game and asks
     * the player for their choice.
     *
     * @param msg the message containing the usernames
     * @throws IOException if an I/O error occurs
     */
    void rejoin(rejoinPlayerMsg msg) throws IOException;

    /**
     * A method run if the player, when rejoining, picks a
     * username already picked in the meantime by someone else.
     *
     * @param msg the message containing the usernames
     * @throws IOException if an I/O error occurs
     */
    void alreadyPicked(cannotRejoinMsg msg) throws IOException;

    /**
     * A method that restores all the helpful information
     * when a game is loaded and restarted. All these information
     * where present when the player got kicked or when the
     * match crashed, thus are mandatory to proceed the match.
     *
     * @param msg the message containing all necessary data
     */
    void setRequiredData(requiredDataMsg msg);

    /**
     * A method run when a player has inserted a duplicate username or
     * color chosen from another player.
     *
     * @param msg the message containing the usernames and colors used
     * @throws IOException if an I/O error occurs
     */
    void nickOrColorUsed(nickOrColorAlreadyUsedMsg msg) throws IOException;

    /**
     * A method that checks periodically if the first
     * player is still setting up the game.
     *
     * @param msg the message containing the usernames and colors
     */
    void firstStillJoining(checkFirstMsg msg);

    /**
     * A method that saved the initial data structure, consisting of
     * the starting card, hands and common objectives.
     * Lastly, it asks the server for the potential objective cards
     * to choose from for the secret objective.
     *
     * @param message the message containing all necessary data
     * @throws IOException if an I/O error occurs
     */
    void updateLocalInitialSituation(initialSituation message) throws IOException;

    /**
     * A method run when the player has to choose its secret
     * objective. It shows the two personal objective cards
     * drawn, asks which one the player wants to choose,
     * send the message to the server and, lastly, it calls
     * the next method.
     *
     * @param msg the message containing the personal objective cards
     * @throws IOException if an I/O error occurs
     */
    void choosePersonalObjectives(personalObjectivesMsg msg) throws IOException;

    /**
     * A method that updates the local client with all the
     * initial game information. Only used when a new  game is started.
     *
     * @param msg the message containing the initial game info
     */
    void updateInitialGameInfo(initialGameInfoMsg msg);

    /**
     * A method that updates the local situation of a player
     * when he/she places a card. It also updates the placements
     * of that player.
     *
     * @param playerID the ID of the player who placed the card
     * @param placedCardSide the card that was placed
     */
    void updatePlayerSituation(int playerID, CardSide placedCardSide);

    /**
     * A method that prints out the available corner placements to the player,
     * who then has to decide one. After insertion, the information is forwarded to the server.
     * If the player inserts "-1", he's sent back to choosing a card to place.
     *
     * @param msg the message containing the corners on which placement is possible
     * @throws IOException if an I/O error occurs
     */
    void displayAvailablePlacements(availablePlacementsMsg msg) throws IOException;
}
