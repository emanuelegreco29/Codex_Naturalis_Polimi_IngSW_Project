package it.polimi.ingsw.am43.ModelTests.PointsRulesTests;

import it.polimi.ingsw.am43.Model.Cards.*;
import it.polimi.ingsw.am43.Model.Deck;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Player;
import it.polimi.ingsw.am43.Model.Points.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class NonPatternPointTests {
    private Player player;
    private Deck resource, gold, objective, starting;
    private PlayableCard resCard, goldCard, startingCard;
    private ObjectiveCard objCard;
    @Before
    public void setUp() {
        player = new Player("Player", PawnColor.RED);
        resource = new Deck("Resource");
        gold = new Deck("Gold");
        objective = new Deck("Objective");
        starting = new Deck("Starting");
        resCard = resource.drawPlayableCard();
        goldCard = gold.drawPlayableCard();
        startingCard = starting.drawPlayableCard();
        objCard = objective.drawObjectiveCard();
    }
    @Test
    public void testSimplePoints(){
        if(resCard.getFrontside().getPointsRule().computePoints(player) == 1){
            assertEquals(1, resCard.getFrontside().getPointsRule().computePoints(player));
        } else {
            assertEquals(0, resCard.getFrontside().getPointsRule().computePoints(player));
        }
    }

    @Test
    public void testForEachResourseEmpty() {
        int[] resources = {0,0,0,0,0,0,0};
        player.setRes_qty(resources);
        PointsRule rule = new forEachResourceSet(1, Collections.singletonList(Symbol.INKWELL));
        assertEquals(0, rule.computePoints(player));
    }

    @Test
    public void testForEachResourceSet(){
        int[] resources = {2,7,5,3,4,7,1};
        player.setRes_qty(resources);

        // Check for gold card
        PointsRule rule = new forEachResourceSet(1, Collections.singletonList(Symbol.INKWELL));
        assertEquals(4, rule.computePoints(player));

        // Check for objective card 3 different symbols
        List<Symbol> symbols = new ArrayList<>();
        symbols.add(Symbol.INKWELL);
        symbols.add(Symbol.QUILL);
        symbols.add(Symbol.MANUSCRIPT);
        rule = new forEachResourceSet(3, symbols);
        assertEquals(3, rule.computePoints(player));

        // Check for objective card 2 equal symbols
        symbols = new ArrayList<>();
        symbols.add(Symbol.INKWELL);
        symbols.add(Symbol.INKWELL);
        rule = new forEachResourceSet(2, symbols);
        assertEquals(4, rule.computePoints(player));

        // Check for objective card 3 equal resources
        symbols = new ArrayList<>();
        symbols.add(Symbol.PLANT);
        symbols.add(Symbol.PLANT);
        symbols.add(Symbol.PLANT);
        rule = new forEachResourceSet(2, symbols);
        assertEquals(2, rule.computePoints(player));
    }

    @Test
    public void testForEachCoveredCorner() {
        int expected = 4;
        int[][] testPlacement = new int[81][81];
        List<Symbol> reqRes = new ArrayList<>();
        ArrayList<CardSide> testDep = new ArrayList<>();
        PointsRule rule = new forEachCoveredCorner(2);
        for (int i=0; i<testPlacement.length; i++){
            for (int j=0; j<testPlacement.length; j++){
                testPlacement[i][j] = -1;
            }
        }

        // Set up the deployed list for the player
        testDep.add(startingCard.getFrontside());
        testDep.add(resource.drawPlayableCard().getFrontside());
        testDep.add(resource.drawPlayableCard().getFrontside());
        testDep.add(new CardSide(new Corner[]{new Corner(Symbol.NONE), new Corner(Symbol.INKWELL), new Corner(Symbol.QUILL), new Corner(Symbol.MANUSCRIPT)},""));
        testDep.get(3).setRelativeCoordinates(new int[]{-1, -1});

        // Set up the placement array for the player
        testPlacement[40][40] = 1;
        testPlacement[41][39] = 2;
        testPlacement[40][38] = 3;
        testPlacement[39][39] = 4;
        player.setPlacements(testPlacement);
        player.setDeployed(testDep);

        assertEquals(expected, rule.computePoints(player));
    }

    /*
    @Test
    public void testForEachDiagonalPattern() {
        int[][] testPlacement = new int[15][15];
        PointsRule rule = new forEachDiagonalPattern(2, Kingdom.ANIMAL, false);
        ArrayList<CardSide> tempDep = new ArrayList<>();
        List<Symbol> fixed = Collections.singletonList(Symbol.PLANT);
        CardSide testSide = new BackSide(new Corner[]{new Corner(Symbol.NONE), new Corner(Symbol.NONE), new Corner(Symbol.NONE), new Corner(Symbol.NONE)}, Kingdom.PLANT, fixed);

        // Set up the placement array for the player
        testPlacement[7][7] = 1;
        testPlacement[8][8] = 2;
        testPlacement[9][9] = 3;
        testPlacement[10][10] = 4;
        testPlacement[11][11] = 5;
        testPlacement[12][12] = 6;

        // Set up the deployed list for the player
        tempDep.add(starting.drawPlayableCard().getFrontside());
        tempDep.add(testSide);
        tempDep.add(testSide);
        tempDep.add(testSide);
        tempDep.add(testSide);
        tempDep.add(testSide);

        player.setPlacements(testPlacement);
        player.setDeployed(tempDep);

        assertEquals(2, rule.computePoints(player));
    }

    @Test
    public void testForEachLPattern(){
        int[][] testPlacement = new int[81][81];
        for (int i=0; i<testPlacement.length; i++){
            for (int j=0; j<testPlacement.length; j++){
                testPlacement[i][j] = -1;
            }
        }

        // Setup the 4 different arrays for the 4 different patterns
        Symbol[] resTL = new Symbol[3];
        resTL[0] = Symbol.INSECT;
        resTL[1] = Symbol.INSECT;
        resTL[2] = Symbol.ANIMAL;
        Symbol[] resTR = new Symbol[3];
        resTR[0] = Symbol.ANIMAL;
        resTR[1] = Symbol.ANIMAL;
        resTR[2] = Symbol.FUNGI;
        Symbol[] resBL = new Symbol[3];
        resBL[0] = Symbol.PLANT;
        resBL[1] = Symbol.PLANT;
        resBL[2] = Symbol.INSECT;
        Symbol[] resBR = new Symbol[3];
        resBR[0] = Symbol.FUNGI;
        resBR[1] = Symbol.FUNGI;
        resBR[2] = Symbol.PLANT;

        // Create the 4 different rules
        PointsRule ruleTL = new forEachLPattern(3, 0, resTL);
        PointsRule ruleTR = new forEachLPattern(3, 1, resTR);
        PointsRule ruleBL = new forEachLPattern(3, 2, resBL);
        PointsRule ruleBR = new forEachLPattern(3, 3, resBR);

        // Creates the 4 sample cards, backside for ease
        BackSide plantSide = new BackSide(new Corner[]{new Corner(Symbol.NONE), new Corner(Symbol.NONE), new Corner(Symbol.NONE), new Corner(Symbol.NONE)}, Kingdom.PLANT, Collections.singletonList(Symbol.PLANT));
        BackSide fungiSide = new BackSide(new Corner[]{new Corner(Symbol.NONE), new Corner(Symbol.NONE), new Corner(Symbol.NONE), new Corner(Symbol.NONE)}, Kingdom.FUNGI, Collections.singletonList(Symbol.FUNGI));
        BackSide insectSide = new BackSide(new Corner[]{new Corner(Symbol.NONE), new Corner(Symbol.NONE), new Corner(Symbol.NONE), new Corner(Symbol.NONE)}, Kingdom.INSECT, Collections.singletonList(Symbol.INSECT));
        BackSide animalSide = new BackSide(new Corner[]{new Corner(Symbol.NONE), new Corner(Symbol.NONE), new Corner(Symbol.NONE), new Corner(Symbol.NONE)}, Kingdom.ANIMAL, Collections.singletonList(Symbol.ANIMAL));

        // Create the board
        testPlacement[40][40] = 0;
        player.placeStartingCard(startingCard.getFrontside());

        // Placement for BR
        player.placeCardSide(plantSide, 0, 1);
        player.placeCardSide(fungiSide, 1, 0);
        player.placeCardSide(insectSide, 2, 1);
        player.placeCardSide(fungiSide, 3, 0);
        assertEquals(3, ruleBR.computePoints(player));

        // Placement for BL
        player.placeCardSide(animalSide, 0, 2);
        player.placeCardSide(plantSide, 4, 2);
        player.placeCardSide(animalSide, 5, 3);
        player.placeCardSide(plantSide, 6, 2);
        player.placeCardSide(insectSide, 7, 2);
        assertEquals(3, ruleBL.computePoints(player));

        // Placement for TR
        player.placeCardSide(fungiSide, 0, 3);
        player.placeCardSide(animalSide, 8, 2);
        player.placeCardSide(insectSide, 9, 3);
        player.placeCardSide(animalSide, 10, 2);
        assertEquals(3, ruleTR.computePoints(player));

        // Placement for TL
        player.placeCardSide(insectSide, 0, 0);
        player.placeCardSide(plantSide, 11, 0);
        player.placeCardSide(insectSide, 12, 1);
        player.placeCardSide(animalSide, 13, 0);
        assertEquals(3, ruleTL.computePoints(player));
    }
     */
}
