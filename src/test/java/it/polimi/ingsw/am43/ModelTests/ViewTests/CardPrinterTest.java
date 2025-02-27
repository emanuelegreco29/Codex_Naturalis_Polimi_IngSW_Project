package it.polimi.ingsw.am43.ModelTests.ViewTests;

import it.polimi.ingsw.am43.Model.Deck;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.View.TUI.CardPrinter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;

public class CardPrinterTest {

    private Deck resources;
    private Deck gold;
    private Deck objective;


    @BeforeMethod
    public void setUp() {
        this.resources = new Deck("Resource");
        this.gold = new Deck("Gold");
        this.objective = new Deck("Objective");
    }

    @Test
    public void testLegend() {
        CardPrinter.printLegend();
    }

    @Test
    public void testCardSide() {
        CardPrinter.printPlayableCardSide(gold.drawPlayableCard().getFrontside(), true);
        CardPrinter.printPlayableCardSide(resources.drawPlayableCard().getFrontside(), false);
    }

    @Test
    public void testCardSideSameRow() {
        CardPrinter.printPlayableCardSideSameRow(gold.drawPlayableCard().getFrontside(), true, gold.drawPlayableCard().getFrontside(), true);
        CardPrinter.printPlayableCardSideSameRow(gold.drawPlayableCard().getFrontside(), false, gold.drawPlayableCard().getFrontside(), false);
        CardPrinter.printPlayableCardSideSameRow(resources.drawPlayableCard().getFrontside(), true, resources.drawPlayableCard().getFrontside(), true);
        CardPrinter.printPlayableCardSideSameRow(resources.drawPlayableCard().getFrontside(), false, resources.drawPlayableCard().getFrontside(), false);
    }

    @Test
    public void testObjectiveCard() {
        CardPrinter.printObjectiveCardSideSameRow(objective.drawObjectiveCard(),  objective.drawObjectiveCard());
    }

    @Test
    public void testToTUIColorAndToInt() {
        PawnColor red = PawnColor.RED;
        assertEquals(red.toTUIColor(), "\033[0;31m");
        assertEquals(red.toInt(), 0);
        PawnColor green = PawnColor.GREEN;
        assertEquals(green.toTUIColor(), "\033[0;32m");
        assertEquals(green.toInt(), 2);
        PawnColor blue = PawnColor.BLUE;
        assertEquals(blue.toTUIColor(), "\033[0;34m");
        assertEquals(blue.toInt(), 1);
        PawnColor yellow = PawnColor.YELLOW;
        assertEquals(yellow.toTUIColor(), "\033[0;33m");
        assertEquals(yellow.toInt(), 3);
        PawnColor black = PawnColor.BLACK;
        assertEquals(black.toTUIColor(), "\033[0;37m");
        assertEquals(black.toInt(), 4);
    }
}
