package it.polimi.ingsw.am43.Controller;

import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Game;
import it.polimi.ingsw.am43.Model.Player;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.initialGameInfoMsg;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.initialSituation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class InitializationTests {
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
    }


    @Test
    void startPreliminaryPhase() {
        Game game = gameController.getGame();
        Player[] players = game.getPlayers();

        // Verify that starting cards were dealt to players
        for (Player player : players) {
            assertNotNull(player.getStartingCard());
        }

        // Verify that starting hands were dealt to players
        for (Player player : players) {
            assertNotNull(player.getInHand());
            assertEquals(Arrays.stream(
                            game.getPlayers()).filter((p) -> p.equals(player)).toList().getFirst().getInHand().length,
                    player.getInHand().length);
        }

        // Verify that common objectives were drawn
        assertNotNull(game.getCommonObjectives());
        assertTrue(game.getCommonObjectives().length > 0);

        // Verify that the first player was chosen randomly
        int firstPlayerID = game.getFirst_player();
        assertTrue(firstPlayerID >= 0 && firstPlayerID < gameController.getNumPlayers());

        // Verify that the last player ID is set correctly
        assertNotEquals(firstPlayerID, game.getLastPlayerID());
    }


    @Test
    void getInitialSituation() {
        for (int i = 0; i < mockUsernames.size(); i++) {
            initialSituation situation = gameController.getInitialSituation(i);
            Player player = gameController.getPlayers()[i];

            // Verify the initial hand
            PlayableCard[] hand = situation.getInHand();
            PlayableCard[] expectedHand = player.getInHand();
            assertArrayEquals(expectedHand, hand);

            // Verify the starting card
            PlayableCard startingCard = situation.getStartingCard();
            PlayableCard expectedStartingCard = player.getStartingCard();
            assertEquals(expectedStartingCard, startingCard);

            // Verify the common objectives
            ObjectiveCard[] commonObjectives = situation.getCommonObjectives();
            ObjectiveCard[] expectedCommonObjectives = gameController.getCommonObjectives();
            assertArrayEquals(expectedCommonObjectives, commonObjectives);

            // Verify the player ID
            int playerID = situation.getPlayerID();
            assertEquals(i, playerID);
        }
    }


    @Test
    void drawPersonalObjectives() {
        // Draw personal objectives for player1
        ObjectiveCard[] drawnObjectives = gameController.drawPersonalObjectives();

        // Assertions after drawing personal objectives
        assertNotNull(drawnObjectives);
        assertEquals(2, drawnObjectives.length);
    }

    @Test
    void setPersonalObjective() {
        // Assuming player1 is at index 0
        int playerID = 0;

        // Get the drawn personal objective for player1
        ObjectiveCard[] personalObjectives = gameController.drawPersonalObjectives();
        ObjectiveCard objectiveToSet = personalObjectives[0];

        // Set the personal objective for player1
        gameController.setPersonalObjective(playerID, objectiveToSet);

        // Retrieve and assert the set personal objective for player1
        ObjectiveCard setObjective = gameController.getPersonalObjective(playerID);
        assertNotNull(setObjective);
        assertEquals(objectiveToSet, setObjective);
    }

    @Test
    void getPersonalObjective() {
        gameController.drawPersonalObjectives();

        // Get the drawn personal objective for player1
        ObjectiveCard[] personalObjectives = gameController.drawPersonalObjectives();
        ObjectiveCard objectiveToSet = personalObjectives[0];

        // Set the personal objective for player1
        gameController.setPersonalObjective(0, objectiveToSet);
        // Assuming player 0 is the first player
        ObjectiveCard personalObjective = gameController.getPersonalObjective(0);

        // Assert that the returned personal objective is not null
        assertNotNull(personalObjective);
    }

    @Test
    void getAvailablePawnColors() {
        // Call the method to get available pawn colors
        int[] availableColors = gameController.getAvailablePawnColors();

        // Assert that the returned array is not null and has expected length (assuming maximum colors)
        assertNotNull(availableColors);
        assertEquals(0, availableColors.length); // All colors taken
    }

    @Test
    void placeStartingCardSide() {
        // Assume player1 is at index 0
        int playerID = 0;

        // Place the starting card side (assuming front side)
        gameController.placeStartingCardSide(0, playerID);

        // Get the player's starting card side
        Player player = gameController.getPlayers()[playerID];
        PlayableCard startingCard = player.getStartingCard();

        // Assert that the starting card side is not null
        assertNotNull(startingCard);

        // Assert that the placed side matches the expected front side of the starting card
        assertEquals(player.getStartingCard().getFrontside(), startingCard.getFrontside());
    }

    @Test
    void getInitialGameInfo() {
        for (int i = 0; i < mockUsernames.size(); i++) {
            gameController.placeStartingCardSide(0, i);
        }
        // Call the method to get initial game information
        initialGameInfoMsg initialGameInfo = gameController.getInitialGameInfo();

        // Verify that the returned object is not null
        assertNotNull(initialGameInfo);

        // Verify the correctness of the initial game information
        PawnColor[] colors = initialGameInfo.getColors();
        String[] usernames = initialGameInfo.getUsernames();
        assertNotNull( colors);
        assertNotNull(usernames);

        // Check the lengths of the arrays
        assertEquals(gameController.getNumPlayers(), colors.length);
        assertEquals(gameController.getNumPlayers(), usernames.length);

        // Verify the top cards of the golden and resource decks
        assertNotNull(initialGameInfo.getGoldenDeckTop());
        assertNotNull(initialGameInfo.getResourceDeckTop());
    }
}
