package it.polimi.ingsw.am43.ModelTests;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.Corner;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.CardType;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Game;
import it.polimi.ingsw.am43.Model.Points.simplePoints;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    Game game;

    @Before
    public void setUp() {
        game = new Game();
    }

    @Test
    public void testGameConstructor(){
        Game testGame = new Game();

        // Primary assignments tests
        assertEquals(0, testGame.getTurn());
        assertFalse(testGame.isLastTurn());
        assertTrue(testGame.getId()>100000 && testGame.getId()<999999);

        // OnGround tests
        assertEquals(4, testGame.getOnGround().length);
        assertEquals(CardType.RESOURCE, testGame.getOnGround()[0].getType());
        assertEquals(CardType.RESOURCE, testGame.getOnGround()[1].getType());
        assertEquals(CardType.GOLD, testGame.getOnGround()[2].getType());
        assertEquals(CardType.GOLD, testGame.getOnGround()[3].getType());
        assertEquals(38, testGame.getDeck("Resource").size());
        assertEquals(38, testGame.getDeck("Gold").size());
    }

    @Test
    public void testInitializePlayers(){
        int n=4;
        String[] players = {"p1", "p2", "p3", "p4"};
        PawnColor[] colors = {PawnColor.RED, PawnColor.BLUE, PawnColor.GREEN, PawnColor.YELLOW};

        game.initializePlayers(n, players[0], colors[0]);

        assertEquals(game.getPlayers().length, n);
        assertEquals(players[0], game.getPlayers()[0].getUsername());
        assertEquals(colors[0], game.getPlayers()[0].getPawnColor());
    }

    @Test
    public void testAddPlayer(){
        String[] players = {"p1", "p2", "p3", "p4"};
        PawnColor[] colors = {PawnColor.BLUE, PawnColor.GREEN, PawnColor.YELLOW, PawnColor.RED};

        game.initializePlayers(4, players[0], colors[0]);
        game.addPlayer(players[1], colors[1]);
        game.addPlayer(players[2], colors[2]);
        game.addPlayer(players[3], colors[3]);
        assertEquals(4, game.getPlayers().length);
        assertEquals(players[0], game.getPlayers()[0].getUsername());
        assertEquals(colors[0], game.getPlayers()[0].getPawnColor());
        assertEquals(players[1], game.getPlayers()[1].getUsername());
        assertEquals(colors[1], game.getPlayers()[1].getPawnColor());
        assertEquals(players[2], game.getPlayers()[2].getUsername());
        assertEquals(colors[2], game.getPlayers()[2].getPawnColor());
        assertEquals(players[3], game.getPlayers()[3].getUsername());
        assertEquals(colors[3], game.getPlayers()[3].getPawnColor());
    }

    @Test
    public void testDealStartingCards(){
        String[] players = {"p1", "p2", "p3", "p4"};
        PawnColor[] colors = {PawnColor.BLUE, PawnColor.GREEN, PawnColor.YELLOW, PawnColor.RED};
        game.initializePlayers(4, players[0], colors[0]);
        game.addPlayer(players[1], colors[1]);
        game.addPlayer(players[2], colors[2]);
        game.addPlayer(players[3], colors[3]);

        game.dealStartingCards(game.getPlayers());
        assertEquals(2, game.getDeck("Starting").size());
        assertNotEquals(game.getDeck("Starting"), game.getPlayers()[0].getStartingCard());
        assertNotEquals(game.getDeck("Starting"), game.getPlayers()[1].getStartingCard());
        assertNotEquals(game.getDeck("Starting"), game.getPlayers()[2].getStartingCard());
        assertNotEquals(game.getDeck("Starting"), game.getPlayers()[3].getStartingCard());
    }

    @Test
    public void testDrawPersonalObjectives(){
        ObjectiveCard[] tmp = new ObjectiveCard[2];
        tmp[0] = game.getDeck("Objective").drawObjectiveCard();
        tmp[1] = game.getDeck("Objective").drawObjectiveCard();

        assertNotEquals(tmp, game.drawPersonalObjectives());
    }

    @Test
    public void testDealHands(){
        String[] players = {"p1", "p2", "p3", "p4"};
        PawnColor[] colors = {PawnColor.BLUE, PawnColor.GREEN, PawnColor.YELLOW, PawnColor.RED};

        game.initializePlayers(4, players[0], colors[0]);
        game.addPlayer(players[1], colors[1]);
        game.addPlayer(players[2], colors[2]);
        game.addPlayer(players[3], colors[3]);

        game.dealHands(game.getPlayers());
        for(int i=0; i<4; i++){
            assertEquals(3, game.getPlayers()[i].getInHand().length);
            assertNotEquals(game.getDeck("Resource"), game.getPlayers()[i].getInHand()[0]);
            assertNotEquals(game.getDeck("Resource"), game.getPlayers()[i].getInHand()[1]);
            assertNotEquals(game.getDeck("Gold"), game.getPlayers()[i].getInHand()[2]);
        }
    }

    @Test
    public void testDrawCommonObjectives(){
        game.drawCommonObjectives();
        ObjectiveCard[] tmp = game.getCommonObjectives();
        assertNotEquals(game.getDeck("Objective"), tmp[0]);
        assertNotEquals(game.getDeck("Objective"), tmp[1]);
    }

    @Test
    public void testOnGround(){
        PlayableCard tmpRes1 = game.getOnGround()[0];
        PlayableCard tmpRes2 = game.getOnGround()[1];
        PlayableCard tmpGold1 = game.getOnGround()[2];
        PlayableCard tmpGold2 = game.getOnGround()[3];

        game.updateOnGround(0);
        assertNotEquals(tmpRes1, game.getOnGround()[0]);
        assertEquals(tmpRes2, game.getOnGround()[1]);
        assertEquals(tmpGold1, game.getOnGround()[2]);
        assertEquals(tmpGold2, game.getOnGround()[3]);

        game.updateOnGround(1);
        assertNotEquals(tmpRes1, game.getOnGround()[0]);
        assertNotEquals(tmpRes2, game.getOnGround()[1]);
        assertEquals(tmpGold1, game.getOnGround()[2]);
        assertEquals(tmpGold2, game.getOnGround()[3]);

        game.updateOnGround(2);
        assertNotEquals(tmpRes1, game.getOnGround()[0]);
        assertNotEquals(tmpRes2, game.getOnGround()[1]);
        assertNotEquals(tmpGold1, game.getOnGround()[2]);
        assertEquals(tmpGold2, game.getOnGround()[3]);

        game.updateOnGround(3);
        assertNotEquals(tmpRes1, game.getOnGround()[0]);
        assertNotEquals(tmpRes2, game.getOnGround()[1]);
        assertNotEquals(tmpGold1, game.getOnGround()[2]);
        assertNotEquals(tmpGold2, game.getOnGround()[3]);
    }

    @Test
    public void testSetPersonalObjective() {
        game.initializePlayers(1, "Player1", PawnColor.BLUE);
        ObjectiveCard objectiveMock = new ObjectiveCard(45, "", Kingdom.INSECT, new simplePoints(4));
        game.setPersonalObjective(0, objectiveMock);

        assertEquals(objectiveMock, game.getPersonalObjective(0));
    }

    @Test
    public void testUpdateOnGround() {
        game.updateOnGround(0);

        PlayableCard[] onGround = game.getOnGround();
        assertNotNull(onGround[0]);
    }

    @Test
    public void testEndGame() {
        game.initializePlayers(2, "Player1", PawnColor.BLUE);
        game.addPlayer("Player2", PawnColor.RED);

        game.drawCommonObjectives();
        game.getPlayers()[0].setPersonalObjective(game.drawPersonalObjectives()[0]);
        game.getPlayers()[1].setPersonalObjective(game.drawPersonalObjectives()[0]);
        game.getScores();

        int[][] points = game.endGame();

        assertEquals(2, points.length);
    }

    @Test
    public void testGetAvailablePawnColors() {
        game.initializePlayers(2, "Player1", PawnColor.BLUE);
        game.addPlayer("Player2", PawnColor.RED);

        int[] availableColors = game.getAvailablePawnColors();

        assertTrue(availableColors.length > 0);
        for (int color : availableColors) {
            assertNotEquals(PawnColor.BLUE.ordinal(), color);
            assertNotEquals(PawnColor.RED.ordinal(), color);
        }
    }

    @Test
    public void testIncreaseTurn() {
        int initialTurn = game.getTurn();
        game.increaseTurn();
        assertEquals(initialTurn + 1, game.getTurn());
    }

    @Test
    public void testGetScores() {
        game.initializePlayers(1, "Player1", PawnColor.BLUE);
        game.getPlayers()[0].updateScoreObjectiveCard(new ObjectiveCard(45, "", Kingdom.INSECT, new simplePoints(4)));

        int[] scores = game.getScores();
        assertEquals(1, scores.length);
        assertEquals(4, scores[0]);
    }

    @Test
    public void testSetAndGetLoaded() {
        game.setLoaded(true);
        assertTrue(game.getLoaded());
        game.setLoaded(false);
        assertFalse(game.getLoaded());
    }

    @Test
    public void testSetAndGetLastPlayerID() {
        game.setLastPlayerID(0);
        assertEquals(0, game.getLastPlayerID());
    }

    @Test
    public void testSetAndGetLastPlacedSide() {
        CardSide side = new CardSide(new Corner[4], "");
        game.setLastPlacedSide(side);
        assertEquals(side, game.getLastPlacedSide());
    }

    @Test
    public void testGetNumPlayers() {
        game.initializePlayers(2, "Player1", PawnColor.BLUE);
        assertEquals(1, game.getNumPlayers());
    }

    @Test
    public void testSetAndGetFirstPlayer() {
        game.setFirst_player(1);
        assertEquals(1, game.getFirst_player());
    }

    @Test
    public void testGetDeck() {
        assertNotNull(game.getDeck("Resource"));
        assertNotNull(game.getDeck("Gold"));
        assertNotNull(game.getDeck("Objective"));
        assertNotNull(game.getDeck("Starting"));
    }

    @Test
    public void testSetAndGetLastTurn() {
        game.setLastTurn(true);
        assertTrue(game.isLastTurn());
        game.setLastTurn(false);
        assertFalse(game.isLastTurn());
    }
}
