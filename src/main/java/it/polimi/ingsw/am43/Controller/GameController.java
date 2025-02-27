package it.polimi.ingsw.am43.Controller;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Game;
import it.polimi.ingsw.am43.Model.Player;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.gameInfoMsg;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.initialGameInfoMsg;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.initialSituation;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("SpellCheckingInspection")
public class GameController implements Serializable {

    @Serial
    private static final long serialVersionUID = -1428779363374911493L;

    /**
     * The game that the controller manages.
     */
    private Game game;

    /**
     * A list containing the usernames of the rejoined players.
     */
    private final List<String> rejoinedPlayers;

    /**
     * A list containing the indexes of the rejoined players.
     */
    private final List<Integer> rejoinedIndexes;

    /**
     * The path where to save the game.
     */
    private final String homePath = System.getProperty("user.home");

    /**
     * The ID of the last player who played.
     */
    private int lastPlayingPlayerID;

    /**
     * Constructor of the class.
     */
    public GameController() {
        this.game = new Game();
        this.rejoinedPlayers = new ArrayList<>();
        this.rejoinedIndexes = new ArrayList<>();
    }

    /**
     * A method that returns the game that the controller manages.
     *
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * A method that returns the list of the rejoined players.
     *
     * @return the list of rejoined players
     */
    public List<String> getRejoinedPlayers() {
        return rejoinedPlayers;
    }

    /**
     * A method that adds a player to the game.
     *
     * @param username player's username
     * @param color player's color
     */
    public void addPlayer(String username, PawnColor color) {
        game.addPlayer(username, color);
    }

    /**
     * A method that initializes the players array of the game.
     *
     * @param n the number of players
     * @param username the username of the first player
     * @param color the color of the first player
     */
    public void initializePlayers(int n, String username, PawnColor color) {
        game.initializePlayers(n, username, color);
    }

    /**
     * A method that reconnects a player to a loaded game.
     *
     * @param pick the index of the player's username
     */
    public void rejoinPlayer(int pick) {
        rejoinedPlayers.add(game.getPlayers()[pick].getUsername());
        rejoinedIndexes.add(pick);
    }

    /**
     * A method that returns the players of the game.
     *
     * @return the players
     */
    public Player[] getPlayers(){
        return game.getPlayers();
    }

    /**
     * A method that returns the indexes of the rejoined players.
     *
     * @return the indexes of rejoined players
     */
    public List<Integer> getRejoinedIndexes() {
        return rejoinedIndexes;
    }

    /**
     * A method that returns the index of the player with the given username.
     *
     * @param username the username of the player
     * @return the index of the player
     */
    public int getPlayerID(String username) {
        for (int i=0; i<game.getPlayers().length; i++) {
            if (username.equals(game.getPlayers()[i].getUsername())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * A network method that returns the on ground of the game.
     *
     * @return the on ground cards
     */
    public PlayableCard[] getOnGround() {
        return game.getOnGround();
    }

    /**
     * A method that returns the common objectives of the game.
     *
     * @return the common objectives
     */
    public ObjectiveCard[] getCommonObjectives() {
        return game.getCommonObjectives();
    }

    /**
     * A method that returns the scores of the game.
     *
     * @return  the scores
     */
    public int[] getScores() {
        return game.getScores();
    }

    /**
     * A method that returns the number of players of the game.
     *
     * @return the number of players
     */
    public int getNumPlayers() {
        return game.getNumPlayers();
    }

    /**
     * A method that returns the ID of the first
     * player playing in the game.
     *
     * @return the ID
     */
    public int getFirstPlayer() {
        return game.getFirst_player();
    }

    /**
     * A method that returns all the placements matrix of
     * all the players of the game.
     *
     * @return the placements
     */
    public int[][][] getAllPlacements() {
        int [][][] placements = new int[getNumPlayers()][81][81];
        for(int i=0; i<getNumPlayers(); i++){
            placements[i] = game.getPlayers()[i].getPlacements();
        }
        return placements;
    }

    /**
     * A method that returns the deployed of all
     * the players of the game.
     *
     * @return the deployed of all players
     */
    public ArrayList<CardSide>[] getAllDeployed() {
        ArrayList<CardSide>[] deployed = new ArrayList[getNumPlayers()];
        for(int i=0; i<getNumPlayers(); i++){
            deployed[i] = game.getPlayers()[i].getDeployed();
        }
        return deployed;
    }

    /**
     * A method that returns the colors of all
     * the players of the game.
     *
     * @return the colors of all players
     */
    public PawnColor[] getUsersColors() {
        return Arrays.stream(game.getPlayers()).map(Player::getPawnColor).toArray(PawnColor[]::new);
    }

    /**
     * A method that starts the preliminary phase of the game.
     */
    public void startPreliminaryPhase() {
        // Actions that do not involve any user active choice
        // Deal starting cards to players
        game.dealStartingCards(game.getPlayers());
        // Deal starting hands
        game.dealHands(game.getPlayers());
        // Draw common objectives
        game.drawCommonObjectives();
        // Choose randomly the first player
        lastPlayingPlayerID = new Random().nextInt(getNumPlayers());
        game.setFirst_player((lastPlayingPlayerID+1)%getNumPlayers());
        game.setLastPlayerID(lastPlayingPlayerID);
    }

    /**
     * A method that returns the initial situation of the player.
     *
     * @param playerID the ID of the player
     * @return a message containing the initial situation
     */
    public initialSituation getInitialSituation(int playerID) {
        return new initialSituation(game.getPlayers()[playerID].getInHand(), game.getPlayers()[playerID].getStartingCard(),
                game.getCommonObjectives(), playerID);
    }

    /**
     * A method that returns the game info of the game.
     *
     * @return a message containing the game info
     */
    public gameInfoMsg getGameInfo() {
        //if the player is the last to play in that turn and the match should finish: set index=-1
        CardSide goldDeckBack, resDeckBack;
        int index = game.getLastChangedOnGround();
        int lastPlayerID = game.getLastPlayerID();
        PlayableCard lastOnGround = game.getOnGround()[index];
        if (isLastTurn()&& lastPlayerID == lastPlayingPlayerID) index=-1;
        if(game.getDeck(5).getTopCard() == null){
            goldDeckBack = null;
        } else {
            goldDeckBack = ( (PlayableCard) game.getDeck(5).getTopCard()).getBackside();
        }
        if(game.getDeck(4).getTopCard() == null){
            resDeckBack = null;
        } else {
            resDeckBack = ( (PlayableCard) game.getDeck(4).getTopCard()).getBackside();
        }

        return new gameInfoMsg(lastPlayerID, game.getScores(), lastOnGround, index, game.getLastPlacedSide(), goldDeckBack, resDeckBack);
    }

    /**
     * A method that returns the initial information of the game.
     *
     * @return a message containing the initial information
     */
    public initialGameInfoMsg getInitialGameInfo() {
        PawnColor[] colors = Arrays.stream(game.getPlayers()).map(Player::getPawnColor).toArray(PawnColor[]::new);
        String[] usernames = Arrays.stream(game.getPlayers()).map(Player::getUsername).toArray(String[]::new);
        CardSide goldenDeckTop = ( (PlayableCard) game.getDeck(5).getTopCard()).getBackside();
        CardSide resourceDeckTop = ( (PlayableCard) game.getDeck(4).getTopCard()).getBackside();
        return new initialGameInfoMsg(game.getStartingCardSides(), game.getOnGround(), game.getFirst_player(), usernames, colors, goldenDeckTop, resourceDeckTop);
    }

    /**
     * A method that places the starting card side of the player.
     *
     * @param side the side to be placed
     * @param playerID the ID of the player
     */
    public void placeStartingCardSide(int side, int playerID) {
        Player curr = game.getPlayers()[playerID];
        if (side==0) curr.placeCardSide(curr.getStartingCard().getFrontside(),-1,0);
        else curr.placeCardSide(curr.getStartingCard().getBackside(), -1, 0);
    }

    /**
     * A method that plays the turn of the player.
     *
     * @param playerID the ID of the player
     * @param idToBePlaced the ID of the card to be placed
     * @param on the ID of the card on which the card will be placed
     * @param corner the corner on which the card will be placed
     * @param drawIndex the index of the drawn card
     * @return the drawn card
     */
    public PlayableCard playTurn(int playerID, int idToBePlaced, int on, int corner, int drawIndex) {

        Player current = this.game.getPlayers()[playerID];
        PlayableCard toBePlaced = current.getInHand()[idToBePlaced/2];
        CardSide toPlace = idToBePlaced%2==0 ? toBePlaced.getFrontside() : toBePlaced.getBackside();

        current.placeCardSide(toPlace, on, corner);

        if (current.getScore()>=20) this.game.setLastTurn(true);
        PlayableCard drawn;

        if (drawIndex<4){
            drawn = this.game.getOnGround()[drawIndex];
            this.game.updateOnGround(drawIndex);
        } else {
            if(this.game.getDeck(drawIndex).size()!=0){
                drawn = this.game.getDeck(drawIndex).drawPlayableCard();
            } else {
                drawn = null;
            }
            if (this.game.getDeck(4).size()==0 && this.game.getDeck(5).size()==0) {
                this.game.setLastTurn(true);
            }
        }
        current.updateHand(drawn, toBePlaced);
        game.setLastPlayerID(playerID);
        game.setLastPlacedSide(toPlace);
        game.increaseTurn();
        return drawn;
    }

    /**
     * A method that ends the game and returns the scores of the players.
     *
     * @return the scores
     */
    public int[][] endGame() {
        return game.endGame();
    }

    /**
     * A method that returns the personal objective of the players to draw it.
     *
     * @return the personal objective card
     */
    public ObjectiveCard[] drawPersonalObjectives() {
        return game.drawPersonalObjectives();
    }

    /**
     * A method that sets the personal objective of the player.
     *
     * @param player the ID of the player
     * @param obj the personal objective card
     */
    public void setPersonalObjective(int player, ObjectiveCard obj) {
        game.setPersonalObjective(player, obj);
    }

    /**
     * A method that returns the personal objective of the player.
     *
     * @param playerID the ID of the player
     * @return the personal objective of the player
     */
    public ObjectiveCard getPersonalObjective(int playerID) {
        return game.getPersonalObjective(playerID);
    }

    /**
     * A method that checks if it is the last turn.
     *
     * @return true if it is the last turn
     */
    public boolean isLastTurn() {
        return game.isLastTurn();
    }

    /**
     * A method that returns the available {@link PawnColor} for a new player,
     * hence the {@link PawnColor} not yet taken by any {@link Player} in the game.
     *
     * @return         	Array of available {@link PawnColor}
     */
    public int[] getAvailablePawnColors() {
        return game.getAvailablePawnColors();
    }


    /**
     * A function that returns the available placements for a card to be placed.
     *
     * @param  playerID      the ID of the player who wants to place the card
     * @param  on            the deployed index of the card to be placed on
     *
     * @return               an array of booleans indicating the available corners for the placement
     */
    public boolean[] getAvailablePlacements(int playerID, int on) {
        Player current = this.game.getPlayers()[playerID];
        //if the card to be placed is gold, check if the player has the requisites to place it
        return current.getAvailablePlacements(on);
    }

    /**
     * A method that checks if the resource deck is empty.
     *
     * @return true if the resource deck is empty, false otherwise
     */
    public boolean isResEmpty() {
        return this.game.getDeck(4).size() == 0;
    }

    /**
     * A method that checks if the gold deck is empty.
     *
     * @return true if the gold deck is empty, false otherwise
     */
    public boolean isGoldEmpty() {
        return this.game.getDeck(5).size() == 0;
    }

    /**
     * A function that returns the available sides for a card to be placed.
     *
     * @param  playerID      the ID of the player who wants to place the card
     *
     * @return               an array of booleans indicating the available sides for the placement
     */
    public Boolean[] getInHandAvailableSides(int playerID) {
        Player current = this.game.getPlayers()[playerID];
        int[] playerRes = current.getResources();

        //nb. not necessary to check the back side requisites as it returns always true
        return Arrays.stream(current.getInHand())
                .flatMap(card -> Stream.of(card.getFrontside().checkGoldRequisites(playerRes), true))
                .toArray(Boolean[]::new);
    }

    /**
     *  A method that returns the names of the players.
     *
     * @return the names
     */
    public String[] getPlayerNames() {
        String[] names;
        if (game.getPlayers() != null) {
            names = new String[game.getPlayers().length];
            for (int i = 0; i < game.getPlayers().length; i++) {
                if (game.getPlayers()[i] != null) {
                    names[i] = game.getPlayers()[i].getUsername();
                }
            }
        } else {
            return null;
        }
        return names;
    }

    /**
     * A method that saves the current game.
     *
     * @throws IOException if an I/O error occurs
     */
    public void saveGame() throws IOException {
        File saveFile = new File(homePath + File.separator + "CodexNaturalis" + File.separator + game.getId());
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));
        oos.writeObject(this.game);
        oos.flush();
        oos.close();
    }

    /**
     * A method that loads a game from a save file.
     *
     * @param gameID the ID of the game to load
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class cannot be found
     */
    public void loadGame(String gameID) throws IOException, ClassNotFoundException {
        File saveFile = new File(homePath + File.separator + "CodexNaturalis" + File.separator + gameID);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile));
        this.game = (Game) ois.readObject();
        ois.close();
        this.game.setLoaded(true);
        rejoinedPlayers.clear();
    }

    /**
     * A method that returns if the game is loaded or not.
     *
     * @return the boolean
     */
    public boolean getLoaded() {
        return game.getLoaded();
    }

    /**
     * A method that returns the ID of the game.
     *
     * @return the ID
     */
    public int getGameID() {
        return game.getId();
    }
}