package it.polimi.ingsw.am43.ModelTests.CardTests;

import it.polimi.ingsw.am43.Model.Cards.*;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.Corner;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class CardSideTest {

    private CardSide cardSide;
    private BackSide backSide;

    @Before
    public void setUp() {
        // Create a CardSide object with mock data
        Corner[] corners = new Corner[4];
        corners[0] = new Corner(Symbol.MANUSCRIPT);
        corners[1] = new Corner(Symbol.INSECT);
        corners[2] = new Corner(Symbol.ANIMAL);
        corners[3] = new Corner(Symbol.NONEXISTING);
        cardSide = new CardSide(corners, "");
        backSide = new BackSide(new Corner[4], Kingdom.ANIMAL, new ArrayList<>(), "");
        cardSide.setRelativeCoordinates(new int[]{1, 1});
    }

    @Test
    public void testGetCornersStatus() {
        // Expected result: {1, 1, 1, 0}
        int[] expectedPresence = {1, 1, 1, 0};
        int[] actualPresence = cardSide.getCornersStatus();
        assertArrayEquals(expectedPresence, actualPresence);
    }

    @Test
    public void testCheckGoldRequisites_NotGoldFront() {
        // Create a CardSide object with no required resources
        cardSide = new CardSide(new Corner[4],"");
        // Set up player resources
        int[] playerResources = {0, 0, 0, 0};
        assertTrue(cardSide.checkGoldRequisites(playerResources));
    }

    @Test
    public void getKingdomTest() {
        assertEquals(Kingdom.ANIMAL, backSide.getKingdom());
    }

    @Test
    public void getRequiredResourcesTest() {
        assertTrue(cardSide.getRequiredResources().isEmpty());
    }

    @Test
    public void getPngPathTest() {
        assertEquals("", cardSide.getPngPath());
    }
}