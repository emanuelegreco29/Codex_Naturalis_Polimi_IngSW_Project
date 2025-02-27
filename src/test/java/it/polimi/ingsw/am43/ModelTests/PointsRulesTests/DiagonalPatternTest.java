package it.polimi.ingsw.am43.ModelTests.PointsRulesTests;

import it.polimi.ingsw.am43.Model.Cards.*;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Player;
import it.polimi.ingsw.am43.Model.Points.forEachDiagonalPattern;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DiagonalPatternTest {

    private Player player;
    private ObjectiveCard obj;

    @Before
    public void setUp(){
        this.player = new Player("john",PawnColor.RED);

        ArrayList<CardSide> deployed = new ArrayList<>();
        int[][] placements = new int[81][81];

        Corner[] corners = {new Corner(Symbol.NONE),new Corner(Symbol.NONEXISTING),new Corner(Symbol.FUNGI),new Corner(Symbol.NONE)};
        CardSide starting = new CardSide(corners, "");
        starting.setDeployedID(0);
        starting.setRelativeCoordinates(new int[]{0, 0});
        deployed.add(starting);
        CardSide res1 = new ResourceFrontSide(corners, Kingdom.ANIMAL,null, "");
        res1.setDeployedID(1);
        res1.setRelativeCoordinates(new int[]{1, -1});
        deployed.add(res1);
        CardSide res2 = new ResourceFrontSide(corners, Kingdom.ANIMAL,null, "");
        res2.setDeployedID(2);
        res2.setRelativeCoordinates(new int[]{2, 0});
        deployed.add(res2);
        CardSide res3 = new ResourceFrontSide(corners, Kingdom.PLANT,null, "");
        res3.setDeployedID(3);
        res3.setRelativeCoordinates(new int[]{3, 1});
        deployed.add(res3);
        CardSide res4 = new ResourceFrontSide(corners, Kingdom.PLANT,null, "");
        res4.setDeployedID(4);
        res4.setRelativeCoordinates(new int[]{2, 2});
        deployed.add(res4);
        CardSide res5 = new ResourceFrontSide(corners, Kingdom.PLANT,null, "");
        res5.setDeployedID(5);
        res5.setRelativeCoordinates(new int[]{1, 3});
        deployed.add(res5);
        CardSide res6 = new ResourceFrontSide(corners, Kingdom.ANIMAL,null, "");
        res6.setDeployedID(6);
        res6.setRelativeCoordinates(new int[]{0, 2});
        deployed.add(res6);

        for (int i=0;i<81;i++){
            for (int j=0; j<81;j++){
                placements[i][j] = -1;
            }
        }

        placements[40][40] = 0;
        placements[40][42] = 6;
        placements[41][39] = 1;
        placements[41][43] = 5;
        placements[42][40] = 2;
        placements[42][42] = 4;
        placements[43][41] = 3;

        this.player.setDeployed(deployed);
        this.player.setPlacements(placements);
        this.player.computeResources();

        this.obj = new ObjectiveCard(0,"", Kingdom.PLANT, new forEachDiagonalPattern(3,Kingdom.PLANT,false));

    }

    @Test
    public void diagonalPatternTest(){

        int expectedScore = 3;

        int SCORE = this.obj.getRule().computePoints(this.player);

        assertEquals(expectedScore, SCORE);

        CardSide newBack = new BackSide(new Corner[]{new Corner(Symbol.NONE),new Corner(Symbol.NONE),new Corner(Symbol.NONE),new Corner(Symbol.NONE)}, Kingdom.ANIMAL, new ArrayList<>(), "");
        this.player.placeCardSide(newBack,3,2);

        assertEquals(expectedScore, SCORE);
    }
}
