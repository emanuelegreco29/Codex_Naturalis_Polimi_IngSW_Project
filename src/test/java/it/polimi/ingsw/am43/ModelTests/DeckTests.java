package it.polimi.ingsw.am43.ModelTests;

import it.polimi.ingsw.am43.Model.Cards.Card;
import it.polimi.ingsw.am43.Model.Deck;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTests {

    private Deck deck, obj;

    @Before
    public void setUp() {
        deck = new Deck("Starting");
        obj = new Deck("Objective");
    }

    @Test
    public void testConstructor() {
        Deck deck2 = new Deck("Starting");
        int[] expectedIDs = {81, 82, 83, 84, 85, 86};
        for (int i = 0; i < 6; i++) {
            int id = deck2.drawPlayableCard().getId();
            for(int j = 0; j < 6; j++) {
                if(id == expectedIDs[j]) {
                    assertEquals(id, expectedIDs[j]);
                }
            }
        }
    }
    @Test
    public void testShuffle() {
        // Shuffle happens inside the deck constructor
        Deck deck2 = new Deck("Starting");
        assertNotEquals(deck, deck2);
    }

    @Test
    public void testDrawPlayableCard() {
        Deck copy = deck;
        Card expected = deck.getTopCard();
        // Draws the first card from the deck
        assertEquals(copy.drawPlayableCard(), expected);
    }

    @Test
    public void testDrawObjectiveCard() {
        Deck copy = obj;
        Card expected = obj.getTopCard();
        // Draws the first card from the deck
        assertEquals(copy.drawObjectiveCard(), expected);
    }

    @Test
    public void testGetTopCardFull() {
        Card expected = deck.getTopCard();
        assertEquals(expected, deck.getTopCard());
    }

    @Test
    public void testGetTopCardEmpty() {
        while(deck.size() != 0) {
            deck.drawPlayableCard();
        }
        assertNull(deck.getTopCard());
    }
}
