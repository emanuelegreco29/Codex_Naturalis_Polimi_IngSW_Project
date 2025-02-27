package it.polimi.ingsw.am43.ModelTests.CardTests;

import it.polimi.ingsw.am43.Model.Cards.Corner;
import it.polimi.ingsw.am43.Model.Cards.GoldFrontSide;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Points.simplePoints;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class GoldFrontSideTest {

    private GoldFrontSide goldFrontSide;
    private simplePoints pointsRule;

    @Before
    public void setUp() {
        // Create a CardSide object with mock data
        Corner[] corners = new Corner[4];
        corners[0] = new Corner(Symbol.MANUSCRIPT);
        corners[1] = new Corner(Symbol.INSECT);
        corners[2] = new Corner(Symbol.ANIMAL);
        corners[3] = new Corner(Symbol.NONEXISTING);
        pointsRule= new simplePoints(1);
        List<Symbol> requiredResources = new ArrayList<>();
        Kingdom k = Kingdom.ANIMAL;
        requiredResources.add(Symbol.INSECT);
        requiredResources.add(Symbol.FUNGI);
        goldFrontSide = new GoldFrontSide(corners, k, requiredResources, pointsRule, "");
        goldFrontSide.setRelativeCoordinates(new int[]{1, 1});
    }

    @Test
    public void testCheckGoldRequisites_EnoughResources() {
        // Set up player resources
        int[] playerResources = {2, 3, 2, 3};
        assertTrue(goldFrontSide.checkGoldRequisites(playerResources));
    }

    @Test
    public void testCheckGoldRequisites_NotEnoughResources() {
        // Set up player resources
        int[] playerResources = {0, 0, 0, 1};
        assertFalse(goldFrontSide.checkGoldRequisites(playerResources));
    }

    @Test
    public void testCheckGoldRequisites_NoRequiredResources() {
        goldFrontSide = new GoldFrontSide(new Corner[4], Kingdom.ANIMAL, new ArrayList<>(), pointsRule, "");
        assertTrue(goldFrontSide.checkGoldRequisites(new int[4]));
    }

    @Test
    public void getKingdomTest() {
        assertEquals(Kingdom.ANIMAL, goldFrontSide.getKingdom());
    }

    @Test
    public void getRequiredResourcesTest() {
        assertEquals(Symbol.INSECT, goldFrontSide.getRequiredResources().get(0));
        assertEquals(Symbol.FUNGI, goldFrontSide.getRequiredResources().get(1));
    }

    @Test
    public void getPointsRuleTest() {
        assertEquals(pointsRule, goldFrontSide.getPointsRule());
    }
}
