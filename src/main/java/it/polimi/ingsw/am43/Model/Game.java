package it.polimi.ingsw.am43.Model;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;

import java.io.*;
import java.util.*;

/**
 * The class that represents a game.
 * It contains the ID of the game, the number of players
 * needed and the array of {@link Player} connected to the game.
 * It also contains the four {@link Deck} containing all the cards,
 * an array of {@link ObjectiveCard} containing the common objectives for this game
 * and an array of {@link PlayableCard} containing the four card on the ground
 * (two resources and two gold).
 * Lastly, it contains the boolean value representing if it is the last turn,
 * the {@link Random} used to pick the first player and the path where to save the game
 * which works on any OS the game is played on.
 */
public class Game implements Serializable{

    @Serial
    private static final long serialVersionUID = -880448027580331582L;

    /**
     * The ID of the game
     */
    private final int id;

    /**
     * The number of the turns played.
     */
    private int turn;

    /**
     * The number of {@link Player}s playing this game.
     */
    private int num_players;

    /**
     * The ID of the first player to play a turn.
     */
    private int first_player;

    /**
     * Array of {@link Player} containing the players that are
     * playing this game.
     */
    private Player[] players;

    /**
     * The four {@link Deck}s containing all the cards.
     */
    private final Deck deck_res;
    private final Deck deck_gold;
    private final Deck deck_objective;
    private final Deck deck_starting;

    /**
     * The array of {@link ObjectiveCard} containing the common objectives.
     */
    private final ObjectiveCard[] common_objective = new ObjectiveCard[2];

    /**
     * The array of {@link PlayableCard} containing the four card on the ground.
     * The first two are resources, the last two are gold.
     */
    private final PlayableCard[] onGround = new PlayableCard[4];

    /**
     * The index of the {@link PlayableCard} on the ground that was last changed.
     */
    private int lastChangedOnGround = 0;

    /**
     * The boolean value representing if it is the last round of turns.
     */
    private boolean lastTurn;

    /**
     * The ID of the last {@link Player} who played.
     */
    private int lastPlayerID;

    /**
a      * The {@link CardSide} that was last placed by a {@link Player}.
     */
    private CardSide lastPlacedSide;

    /**
     * The boolean value representing if the game has been loaded
     */
    private boolean isLoaded = false;

    /**
     * Constructor of the class {@link Game}.
     */
    public Game() {
        // Set initial values
        Random random = new Random();
        this.id = random.nextInt(999999 - 100000) + 100000;
        this.lastTurn = false;
        this.turn = 0;
        this.num_players = 0;
        this.lastPlayerID = -1;
        this.lastPlacedSide = null;

        // Create the decks
        this.deck_res = new Deck("Resource");
        this.deck_gold = new Deck("Gold");
        this.deck_objective = new Deck("Objective");
        this.deck_starting = new Deck("Starting");

        // Place the ground cards (first 2 resource, last 2 gold)
        onGround[0] = deck_res.drawPlayableCard();
        onGround[1] = deck_res.drawPlayableCard();
        onGround[2] = deck_gold.drawPlayableCard();
        onGround[3] = deck_gold.drawPlayableCard();
    }

    /**
     * Initializes the players array of the game
     * and adds the first {@link Player} to it.
     *
     * @param  n         the number of {@link Player}s that will play this game
     * @param  username  the username of the first {@link Player}
     * @param  color     the {@link PawnColor} of the first {@link Player}
     */
    public void initializePlayers(int n, String username, PawnColor color){
        this.players = new Player[n];
        addPlayer(username, color);
    }

    /**
     * A method that retrieves all the {@link Player} connected.
     *
     * @return         Array of {@link Player} connected to the game
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * Setter method for the isLoaded attribute.
     *
     * @param  loaded  the boolean value representing if the game has been loaded
     */
    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    /**
     * Getter method for the isLoaded attribute.
     *
     * @return  the boolean value representing if the game has been loaded
     */
    public boolean getLoaded() {
        return isLoaded;
    }

    /**
     * Retrieves the ID of the last {@link Player} who played a turn.
     *
     * @return the ID of the last {@link Player}
     */
    public int getLastPlayerID() {
        return lastPlayerID;
    }

    /**
     * Sets the ID of the last {@link Player} who played and returns the new ID.
     *
     * @param id the ID of the last {@link Player}
     */
    public void setLastPlayerID(int id) {
        lastPlayerID = id;
    }

    /**
     * Retrieves the last placed {@link CardSide} of the game.
     *
     * @return the last placed {@link CardSide}
     */
    public CardSide getLastPlacedSide() {
        return lastPlacedSide;
    }

    /**
     * Sets the last placed {@link CardSide} of the game to the specified side.
     *
     * @param side the {@link CardSide} to set as the last placed side
     */
    public void setLastPlacedSide(CardSide side) {
        lastPlacedSide = side;
    }

    /**
     * Retrieves the starting {@link CardSide}s for each {@link Player}.
     *
     * @return an array of {@link CardSide}s representing the starting card sides for each {@link Player}
     */
    public CardSide[] getStartingCardSides(){
        CardSide[] starting = new CardSide[num_players];
        for (int i=0; i<num_players;i++){
            starting[i] = players[i].getDeployed().getFirst();
        }
        return starting;
    }

    /**
     * A method that adds a {@link Player} to the list of {@link Player}.
     *
     * @param  username   the username of {@link Player} to be added
     * @param  color    the {@link PawnColor} of the {@link Player}
     */
    public void addPlayer(String username, PawnColor color) {
        Player player = new Player(username, color);
        players[num_players] = player;
        num_players++;
    }

    /**
     * A method that gets the number of {@link Player}.
     *
     * @return         the number of {@link Player}
     */
    public int getNumPlayers() {
        return num_players;
    }

    /**
     * A method that retrieves the ID of the first player playing the game.
     *
     * @return         the ID of the first player
     */
    public int getFirst_player() {
        return first_player;
    }

    /**
     * A method that sets the ID of the first player playing the game.
     *
     * @param first_player the ID of the first player
     */
    public void setFirst_player(int first_player) {
        this.first_player = first_player;
    }

    /**
     * A method that retrieves the common objective for the game.
     *
     * @return         	An array of {@link ObjectiveCard} representing the common objective
     */
    public ObjectiveCard[] getCommonObjectives() {
        return common_objective;
    }

    /**
     * A method that retrieves the id of the game.
     *
     * @return         the id of the game
     */
    public int getId() {
        return id;
    }

    /**
     * A method that retrieves the specified {@link Deck} based on the given type.
     *
     * @param  type   the type of {@link Deck} to retrieve
     * @return          the {@link Deck} corresponding to the given type
     */
    public Deck getDeck(String type) {
        return switch (type) {
            case "Resource" -> deck_res;
            case "Gold" -> deck_gold;
            case "Objective" -> deck_objective;
            default -> deck_starting;
        };
    }

    /**
     * Retrieves the specified {@link Deck} based on the given {@link Integer} value.
     *
     * @param  deck  the {@link Integer} value representing the {@link Deck} type (4 for Resource Deck, 5 for Gold Deck)
     * @return       the {@link Deck} corresponding to the given {@link Integer} value, or null if the deck type is invalid
     */
    public Deck getDeck(int deck){
        if (deck==4) return deck_res;
        if (deck==5) return deck_gold;
        else return null;
    }

    /**
     * A method that indicates if it is the last turn.
     *
     * @return   the boolean value representing if it is the last turn
     */
    public boolean isLastTurn() {
        return lastTurn;
    }

    /**
     * A method that retrieves the value of turn.
     *
     * @return         turn
     */
    public int getTurn() {
        return turn;
    }

    /**
     * A method that sets the value of lastTurn.
     *
     * @param  lastTurn   the new value of lastTurn
     */
    public void setLastTurn(boolean lastTurn) {
        this.lastTurn = lastTurn;
    }

    /**
     * A method that retrieves the array of {@link PlayableCard} on the ground.
     *
     * @return         	the array of {@link PlayableCard} on the ground
     */
    public PlayableCard[] getOnGround() {
        return onGround;
    }

    /**
     * A method that retrieves the scores of all {@link Player}s in the game.
     *
     * @return an array of integers representing the scores of each {@link Player}
     */
    public int[] getScores() {
        int[] scores = new int[num_players];
        for(int i=0; i<num_players; i++) {
            scores[i] = players[i].getScore();
        }
        return scores;
    }

    /**
     * A method that deals starting cards to the {@link Player}s.
     *
     * @param  players  an array of {@link Player}s playing the game
     */
    public void dealStartingCards(Player[] players) {
        for (Player player : players) {
            player.setStartingCard(deck_starting.drawPlayableCard());
        }
    }

    /**
     * A method that deals two {@link ObjectiveCard} to the
     * {@link Player}, who then has to pick one as secret objective.
     *
     * @return         	array of {@link ObjectiveCard} dealt
     */
    public ObjectiveCard[] drawPersonalObjectives() {
        // We draw the cards (pop from the deck) and never insert the cards back in the deck again.
        // In fact, the card which is not chosen by the player will end up at the bottom of the deck,
        // and it's never shuffled. Hence, the card is never used again
        ObjectiveCard[] temp = new ObjectiveCard[2];
        temp[0] = deck_objective.drawObjectiveCard();
        temp[1] = deck_objective.drawObjectiveCard();
        return temp;
    }

    /**
     * A method that sets the personal objectives for a {@link Player}.
     *
     * @param  player  the {@link Player} whose personal objectives are being set
     * @param  obj     the {@link ObjectiveCard} to set as the personal objective
     */
    public void setPersonalObjective(int player, ObjectiveCard obj) {
        players[player].setPersonalObjective(obj);
    }

    /**
     * A method that retrieves the personal objective of a {@link Player}.
     *
     * @param  player  the {@link Player} whose personal objective is being retrieved
     * @return         the personal objective of the {@link Player}
     */
    public ObjectiveCard getPersonalObjective(int player) {
        return players[player].getPersonalObjective();
    }

    /**
     * A method that deals the starting hands
     * to all the {@link Player}s connected to the game.
     *
     * @param  players    array of {@link Player} to deal hands to
     */
    public void dealHands(Player[] players) {
        // For each player, draw 2 resource cards and a gold card and se the user's hand
        for (Player player : players) {
            PlayableCard[] tempHand = new PlayableCard[3];
            tempHand[0] = deck_res.drawPlayableCard();
            tempHand[1] = deck_res.drawPlayableCard();
            tempHand[2] = deck_gold.drawPlayableCard();
            player.setInHand(tempHand);
        }
    }

    /**
     * A method that draws the common objective
     * cards and places them on the ground.
     */
    public void drawCommonObjectives() {
        common_objective[0] = deck_objective.drawObjectiveCard();
        common_objective[1] = deck_objective.drawObjectiveCard();
    }

    /**
     * A method that updates the {@link PlayableCard} on the ground
     * after that a {@link Player} has picked up a card from it.
     *
     * @param  index   the index of the card to update
     */
    public void updateOnGround(int index){
        // If the drawn card was a resource card
        if (index<2){
            // Draw a new card from the deck only if it's not empty
            if(this.deck_res.size()>0){
                this.onGround[index] = this.deck_res.drawPlayableCard();
            } else {
                this.onGround[index] = null;
            }
        // If the drawn card was a gold card
        } else {
            // Draw a new card from the deck only if it's not empty
            if(this.deck_gold.size()>0){
                this.onGround[index] = this.deck_gold.drawPlayableCard();
            } else {
                this.onGround[index] = null;
            }
        }
        this.lastChangedOnGround = index;
    }

    /**
     * Returns the index of the last changed {@link PlayableCard} on the ground.
     *
     * @return the index of the last changed {@link PlayableCard} on the ground
     */
    public int getLastChangedOnGround() {
        return lastChangedOnGround;
    }

    /**
     * A method called to end the game and compute
     * the {@link Player}s' rankings.
     *
     * @return  Array of integers representing the {@link Player}'s rankings and
     * completed objectives.
     */
    public int[][] endGame() {
        // Evaluate objectives for each player
        //points[0] = index, points[1] = score, points[2] = satisfied_objectives
        int[][] points = new int[num_players][3];
        for (int i=0; i<num_players; i++) {
            points[i][0] = i;
            points[i][2] = 0;
            if (players[i].updateScoreObjectiveCard(players[i].getPersonalObjective())>0){points[i][2]++;}
            for (ObjectiveCard obj : common_objective) {
                if(players[i].updateScoreObjectiveCard(obj)>0){points[i][2]++;}
            }
            // Get the score
            points[i][1] = players[i].getScore();
        }
        // Sort the points by score
        for (int i=0; i<num_players; i++) {
            for (int j=i+1; j<num_players; j++) {
                if ((points[i][1]<points[j][1]) || (points[i][1]==points[j][1] && points[i][2]<points[j][2])) {
                    int[] temp = points[i];
                    points[i] = points[j];
                    points[j] = temp;
                }
            }
        }
        return points;
    }

    /**
     * A method that returns the available {@link PawnColor} for a new {@link Player},
     * hence the {@link PawnColor} not yet taken by any {@link Player} in the game.
     *
     * @return         	Array of available {@link PawnColor}
     */
    public int[] getAvailablePawnColors() {
        // Retrieves all used pawn colors
        int n = num_players;
        List<Integer> remaining = new ArrayList<>();
        for (int i=0; i<PawnColor.values().length; i++) {
            boolean found = false;
            for (int j=0; j<n; j++) {
                if (players[j].getPawnColor() == PawnColor.values()[i]) {
                    found = true;
                    break;
                }
            }
            // If the color has not been used, add it to the list to return
            if (!found && PawnColor.values()[i] != PawnColor.BLACK){
                remaining.add(i);
            }
        }
        return remaining.stream().mapToInt(i -> i).toArray();
    }


    /**
     * A method that increases the turn counter by 1.
     */
    public void increaseTurn() {
        this.turn++;
    }
}
