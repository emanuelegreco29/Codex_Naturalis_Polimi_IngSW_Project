package it.polimi.ingsw.am43.Controller;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Game;
import it.polimi.ingsw.am43.Model.Player;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.gameInfoMsg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class InGameTests {
    GameController gameController;
    ArrayList<String> mockUsernames;
    ArrayList<PawnColor> mockColors;

    @BeforeEach
    void setUp() {
        // Initialize the GameController
        gameController = new GameController();

        // Initialize mock data
        mockUsernames = new ArrayList<>();
        mockColors = new ArrayList<>();

        // Create mock usernames
        mockUsernames.add("player0");
        mockUsernames.add("player1");
        mockUsernames.add("player2");
        mockUsernames.add("player3");

        // Create mock colors
        mockColors.add(PawnColor.YELLOW);
        mockColors.add(PawnColor.RED);
        mockColors.add(PawnColor.BLUE);
        mockColors.add(PawnColor.GREEN);

        // Initialize the players in the game
        gameController.initializePlayers(mockUsernames.size(), mockUsernames.getFirst(), mockColors.getFirst());
        for (int i = 1; i < mockUsernames.size(); i++) {
            gameController.addPlayer(mockUsernames.get(i), mockColors.get(i));
        }
        gameController.startPreliminaryPhase();
        for (int i = 0; i < mockUsernames.size(); i++) {
            // Get the drawn personal objective for player1
            ObjectiveCard[] personalObjectives = gameController.drawPersonalObjectives();
            ObjectiveCard objectiveToSet = personalObjectives[0];

            // Set the personal objective for player1
            gameController.setPersonalObjective(i, objectiveToSet);

            gameController.placeStartingCardSide(0, i);
        }
    }


    @Test
    void getGameInfo() {
        gameInfoMsg gameInfo = gameController.getGameInfo();
        Game game = gameController.getGame();

        // Verify the last player ID
        int playerID = gameInfo.getPlayerID();
        assertEquals(game.getLastPlayerID(), playerID);

        // Verify the scores
        int[] scores = gameInfo.getScores();
        assertArrayEquals(game.getScores(), scores);

        // Verify the last card on ground
        PlayableCard lastOnGround = gameInfo.getLastOnGround();
        int lastChangedIndex = game.getLastChangedOnGround();
        if (lastChangedIndex >= 0) {
            assertEquals(game.getOnGround()[lastChangedIndex], lastOnGround);
        } else {
            assertNull(lastOnGround);
        }

        // Verify the last placed side
        CardSide lastPlacedSide = gameInfo.getPlacedCardSide();
        assertEquals(game.getLastPlacedSide(), lastPlacedSide);

        // Verify the gold deck back
        CardSide goldDeckBack = gameInfo.getGoldenDeckTop();
        if (game.getDeck(5).getTopCard() == null) {
            assertNull(goldDeckBack);
        } else {
            assertEquals(((PlayableCard) game.getDeck(5).getTopCard()).getBackside(), goldDeckBack);
        }

        // Verify the resource deck back
        CardSide resDeckBack = gameInfo.getResourceDeckTop();
        if (game.getDeck(4).getTopCard() == null) {
            assertNull(resDeckBack);
        } else {
            assertEquals(((PlayableCard) game.getDeck(4).getTopCard()).getBackside(), resDeckBack);
        }
    }

    @Test
    void getAvailablePlacements() {
        // Assuming player 0 is the first player and index 0 of deployed cards
        boolean[] availablePlacements = gameController.getAvailablePlacements(0, 0);

        // Assert that the returned array is not null and has expected length
        assertNotNull(availablePlacements);
        assertEquals(4, availablePlacements.length); // Assuming 81 possible placements
    }

    @Test
    void getInHandAvailableSides() {
        // Assuming player 0 is the first player
        Boolean[] availableSides = gameController.getInHandAvailableSides(0);

        // Assert that the returned array is not null and has expected length
        assertNotNull(availableSides);
        assertEquals(gameController.getGame().getPlayers()[0].getInHand().length * 2, availableSides.length);
    }

    @Test
    void getAllDeployed() {
        // Call the method to get all deployed cards
        ArrayList<CardSide>[] allDeployed = gameController.getAllDeployed();

        // Assert that the returned array is not null
        assertNotNull(allDeployed);

        // Assert the length of the array matches the number of players added
        assertEquals(4, allDeployed.length);
    }


    @Test
    void playTurn() {
        // Assuming player1 is at index 0 and it's player1's turn
        int playerID = 0;

        // Get initial state before playing turn
        PlayableCard[] initialOnGround = gameController.getOnGround();
        Player player = gameController.getPlayers()[playerID];
        int initialPlayerScore = player.getScore();

        // Choose a card from player's hand to play (assuming the first card)
        PlayableCard cardToPlay = player.getInHand()[0];
        int cardIndex = 0;
        CardSide sideToPlace = cardToPlay.getFrontside(); // Assuming placing front side
        int onIndex = 0; // Assuming initial placement
        int corner = 0; // Assuming initial corner
        int drawIndex = 0; // Assuming draw from onGround array

        // Play the turn
        PlayableCard drawnCard = gameController.playTurn(playerID, cardIndex, onIndex, corner, drawIndex);

        // Assertions after playing turn
        // Check that the drawn card is returned correctly
        assertNotNull(drawnCard);

        // Check that the card was placed correctly on the board
        assertEquals(gameController.getGame().getLastPlacedSide().getDeployedID(),
                gameController.getPlayers()[playerID].getDeployed().getLast().getDeployedID());

        // Check that player's score has been updated correctly
        assertTrue(player.getScore() >= initialPlayerScore);
    }
    @Test
    public void testBridgeMethods(){
        Game game = gameController.getGame();
        assertEquals(game.getOnGround(), gameController.getOnGround());
        assertEquals(game.getCommonObjectives(), gameController.getCommonObjectives());
        for (int i=0; i< game.getScores().length; i++){
            assertEquals(game.getScores()[i], gameController.getScores()[i]);
        }
        assertEquals(game.getFirst_player(), gameController.getFirstPlayer());
    }

    @Test
    public void placementTest(){
        int[][][] placements = new int[4][][];
        for (int i=0; i<4; i++){
            placements[i] = gameController.getGame().getPlayers()[i].getPlacements();
        }
        for(int i=0; i<4; i++){
            for(int j=0; j<placements[i].length; j++){
                for(int k=0; k<placements[i][j].length; k++){
                    assertEquals(placements[i][j][k], gameController.getAllPlacements()[i][j][k]);
                }
            }
        }
    }

    @Test
    void isResEmpty() {
        assertFalse(gameController.isResEmpty());
    }

    @Test
    void isGoldEmpty() {
        assertFalse(gameController.isGoldEmpty());
    }

    @Test
    void isLastTurn() {
    }

    @Test
    void endGame() {
    }

    @Test
    void saveGame() {
    }
}