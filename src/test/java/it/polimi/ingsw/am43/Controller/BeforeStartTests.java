package it.polimi.ingsw.am43.Controller;

import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeforeStartTests {
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
    }

    @Test
    void initializePlayersAndAddPlayer() {
        // Retrieve the players from the game
        Player[] players = gameController.getPlayers();

        // Verify the number of players
        assertEquals(mockUsernames.size(), players.length);

        // Verify each player's username and color
        for (int i = 0; i < players.length; i++) {
            assertEquals(mockUsernames.get(i), players[i].getUsername());
            assertEquals(mockColors.get(i), players[i].getPawnColor());
        }
    }


    @Test
    void getPlayerID() {
        // Test retrieving the player IDs by their usernames
        for (int i = 0; i < mockUsernames.size(); i++) {
            int playerID = gameController.getPlayerID(mockUsernames.get(i));
            assertEquals(i, playerID);
        }

        // Test retrieving a non-existing username
        int nonExistingPlayerID = gameController.getPlayerID("nonExistingPlayer");
        assertEquals(-1, nonExistingPlayerID);
    }

    @Test
    public void addPlayers(){
        GameController c = new GameController();
        c.initializePlayers(mockUsernames.size(), mockUsernames.getFirst(), mockColors.getFirst());
        for (int i = 1; i < mockUsernames.size() - 1; i++) {
            c.addPlayer(mockUsernames.get(i), mockColors.get(i));
        }
        int before_count = c.getNumPlayers();

        c.addPlayer(mockUsernames.get(3), mockColors.get(3));
        Player[] after_players = c.getPlayers();
        int after_count = c.getNumPlayers();

        assertEquals(before_count +1, after_count);
        assertEquals(mockUsernames.get(3), after_players[after_count-1].getUsername());
        assertEquals(mockColors.get(3), after_players[after_count-1].getPawnColor());
        assertEquals(c.getPlayerID(mockUsernames.get(3)), after_count-1);
    }

    @Test
    public void rejoinTest(){
        List<Integer> rejoined = new ArrayList<>();
        List<String> rejoinNames = new ArrayList<>();
        gameController.rejoinPlayer(0);
        rejoined.add(0);
        rejoinNames.add("player0");
        gameController.rejoinPlayer(1);
        rejoined.add(1);
        rejoinNames.add("player1");
        gameController.rejoinPlayer(2);
        rejoined.add(2);
        rejoinNames.add("player2");

        assertEquals(gameController.getRejoinedIndexes(), rejoined);
        assertEquals(gameController.getRejoinedPlayers(), rejoinNames);
    }


    @Test
    void getPlayerNames() {
        gameController.startPreliminaryPhase();
        // Call the method to get player names
        String[] playerNames = gameController.getPlayerNames();

        // Assert that the returned array is not null
        assertNotNull(playerNames);

        // Assert the length of the array matches the number of players added
        assertEquals(4, playerNames.length);
    }

    @Test
    void getLoaded() {
        gameController.startPreliminaryPhase();
        // Call the method to check if the game is loaded
        boolean isLoaded = gameController.getLoaded();

        // Assert that the game is not loaded initially
        assertFalse(isLoaded);
    }

    @Test
    void getGameID() {
        gameController.startPreliminaryPhase();
        // Call the method to get the game ID
        int gameID = gameController.getGameID();

        // Assert that the game ID is greater than or equal to 0 (assuming non-negative game IDs)
        assertTrue(gameID >= 0);
    }

    @Test
    void getUsersColors() {
        gameController.startPreliminaryPhase();
        // Call the method to get users' colors
        PawnColor[] usersColors = gameController.getUsersColors();

        // Assert that the returned array is not null
        assertNotNull(usersColors);

        // Assert the length of the array matches the number of players added
        assertEquals(4, usersColors.length);
    }
}
