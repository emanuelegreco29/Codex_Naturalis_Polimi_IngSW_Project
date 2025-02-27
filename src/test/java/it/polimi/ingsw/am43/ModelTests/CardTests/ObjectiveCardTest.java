package it.polimi.ingsw.am43.ModelTests.CardTests;

import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Points.PointsRule;
import it.polimi.ingsw.am43.Model.Points.simplePoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectiveCardTest {

    private ObjectiveCard objectiveCard;

    @BeforeEach
    public void setUp() {
        objectiveCard = new ObjectiveCard(1, "path/to/file.png", Kingdom.ANIMAL, new simplePoints(1));
    }


    @Test
    public void testObjectiveCardConstructor() {
        // Arrange
        int expectedId = 1;

        PointsRule expectedRule = new simplePoints(1);

        // Act
        ObjectiveCard objectiveCard = new ObjectiveCard(expectedId, "path/to/file.png",Kingdom.ANIMAL, expectedRule);

        // Assert
        assertNotNull(objectiveCard);
        assertEquals(expectedId, objectiveCard.getId());
        assertEquals(expectedRule, objectiveCard.getRule());
    }

    @Test
    public void getKingdomTest() {
        assertEquals(Kingdom.ANIMAL, objectiveCard.getKingdom());
    }

    @Test
    public void getFrontPathTest() {
        assertEquals("path/to/file.png", objectiveCard.getFrontPath());
    }
}