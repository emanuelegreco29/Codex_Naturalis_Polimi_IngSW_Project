package it.polimi.ingsw.am43.ModelTests.PlayerTests;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.Corner;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.CardType;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Player;
import it.polimi.ingsw.am43.Model.Points.simplePoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerUpdatesTest {
    private Player player;
    private PlayableCard playableCard1;
    private PlayableCard playableCard4;
    private ObjectiveCard objectiveCard;

    @BeforeEach
    public void setUp() {
        player = new Player("testPlayer", PawnColor.BLUE);
        ArrayList<CardSide> deployed = new ArrayList<>();

        Corner[] corners = {new Corner(Symbol.PLANT),new Corner(Symbol.NONEXISTING),new Corner(Symbol.FUNGI),new Corner(Symbol.NONE)};

        Corner[] corners1 = new Corner[]{new Corner(Symbol.NONE), new Corner(Symbol.NONEXISTING), new Corner(Symbol.FUNGI), new Corner(Symbol.NONE)};
        Corner[] corners2 = new Corner[]{new Corner(Symbol.PLANT), new Corner(Symbol.NONEXISTING), new Corner(Symbol.INSECT), new Corner(Symbol.NONE)};
        Corner[] corners3 = new Corner[]{new Corner(Symbol.NONE), new Corner(Symbol.MANUSCRIPT), new Corner(Symbol.INKWELL), new Corner(Symbol.ANIMAL)};

        CardSide back1 = new CardSide(corners,"");
        back1.setDeployedID(0);
        back1.setRelativeCoordinates(new int[]{0, 0});
        deployed.add(back1);
        CardSide back2 = new CardSide(corners2,"");
        back2.setDeployedID(1);
        back2.setRelativeCoordinates(new int[]{1, -1});
        deployed.add(back2);
        CardSide back3 = new CardSide(corners3,"");
        back3.setDeployedID(2);
        back3.setRelativeCoordinates(new int[]{2, 0});
        deployed.add(back3);
        CardSide back4 = new CardSide(corners2,"");
        back4.setDeployedID(3);
        back4.setRelativeCoordinates(new int[]{3, 1});
        deployed.add(back4);
        CardSide back5 = new CardSide(corners1,"");
        back5.setDeployedID(4);
        back5.setRelativeCoordinates(new int[]{2, 2});
        deployed.add(back5);
        CardSide back6 = new CardSide(corners,"");
        back6.setDeployedID(5);
        back6.setRelativeCoordinates(new int[]{1, 3});
        deployed.add(back6);
        CardSide back7 = new CardSide(corners1,"");
        back7.setDeployedID(6);
        back7.setRelativeCoordinates(new int[]{0, 2});
        deployed.add(back7);

        playableCard1 = new PlayableCard(0,new CardSide(
                corners1,
                ""
        ), new CardSide(corners1,""), CardType.GOLD, Kingdom.ANIMAL);

        PlayableCard playableCard2 = new PlayableCard(1, new CardSide(
                corners3,
                ""
        ), new CardSide(corners1, ""), CardType.RESOURCE, Kingdom.PLANT);

        PlayableCard playableCard3 = new PlayableCard(2, new CardSide(
                corners2,
                ""
        ), new CardSide(corners1, ""), CardType.RESOURCE, Kingdom.INSECT);

        playableCard4 = new PlayableCard(3,new CardSide(
                corners3,
                ""
        ), new CardSide(corners2,""), CardType.GOLD, Kingdom.INSECT);

        objectiveCard = new ObjectiveCard(4, "", Kingdom.INSECT,new simplePoints(3));

        player.setInHand(new PlayableCard[]{playableCard1, playableCard2, playableCard3});
        player.setDeployed(deployed);
    }

    @Test
    public void testUpdateHand() {
        player.updateHand(playableCard4, playableCard1);
        assertEquals(playableCard4, player.getInHand()[0]);
    }

    @Test
    public void testUpdateScoreObjectiveCard() {
        int points = player.updateScoreObjectiveCard(objectiveCard);
        System.out.println(points);
        assertEquals(3, points);
        assertEquals(3, player.getScore());
    }

    @Test
    void testComputeResources() {
        player.computeResources();
        System.out.println(Arrays.toString(player.getRes_qty()));
        assertEquals(1, player.getRes_qty()[0]);
        assertEquals(2,  player.getRes_qty()[1]);
        assertEquals(4,  player.getRes_qty()[2]);
        assertEquals(4,  player.getRes_qty()[3]);
        assertEquals(1,  player.getRes_qty()[4]);
        assertEquals(0,  player.getRes_qty()[5]);
        assertEquals(1,  player.getRes_qty()[6]);
    }

}
