package it.polimi.ingsw.am43.ModelTests.CardTests;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.Corner;
import it.polimi.ingsw.am43.Model.Cards.GoldFrontSide;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.*;

import it.polimi.ingsw.am43.Model.Points.PointsRule;
import it.polimi.ingsw.am43.Model.Points.simplePoints;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class PlayableCardTest {

    private PlayableCard card;
    private final CardSide frontSide;
    private final CardSide backSide;
    private final CardType type = CardType.GOLD;
    private final Kingdom kingdom = Kingdom.ANIMAL;

    public PlayableCardTest() {
        // Create front and back sides with mock data
        this.frontSide = createMockCardSide();
        this.backSide = createMockCardSide();
    }

    @Before
    public void setUp() {
        card = new PlayableCard(1, frontSide, backSide, type, kingdom);
    }

    private CardSide createMockCardSide() {
        Corner[] corners = new Corner[4];
        for (int i = 0; i < 4; i++) {
            corners[i] = new Corner(Symbol.INKWELL); // Assign a non-null symbol to each corner
        }
        List<Symbol> requiredResources = new ArrayList<>();
        requiredResources.add(Symbol.QUILL);
        requiredResources.add(Symbol.INSECT);
        Kingdom k = Kingdom.ANIMAL;
        PointsRule rule = new simplePoints(1);
        CardSide cardSide = new GoldFrontSide(corners, k, requiredResources, rule, "");
        cardSide.setRelativeCoordinates(new int[]{1, 1, -1, 1, -1, -1, 1, -1});
        return cardSide;
    }

    @Test
    public void testConstructor() {
        assertEquals(1, card.getId());
        assertEquals(frontSide, card.getFrontside());
        assertEquals(backSide, card.getBackside());
        assertEquals(type, card.getType());
        assertEquals(kingdom, card.getKingdom());
    }

    @Test
    public void getFrontAndBackPathTest() {
        assertEquals("", card.getFrontPath());
        assertEquals("", card.getBackPath());
    }
}