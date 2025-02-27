package it.polimi.ingsw.am43.ModelTests.PlayerTests;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.Corner;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PlayerPlacementsTest {

    private Player player;

    @Before
    public void setUp(){

        this.player = new Player("john",PawnColor.RED);

        ArrayList<CardSide> deployed = new ArrayList<>();
        int[][] placements = new int[81][81];

        Corner[] corners = {new Corner(Symbol.NONE),new Corner(Symbol.NONEXISTING),new Corner(Symbol.FUNGI),new Corner(Symbol.NONE)};
        CardSide back1 = new CardSide(corners,"");
        back1.setDeployedID(0);
        back1.setRelativeCoordinates(new int[]{0, 0});
        deployed.add(back1);
        CardSide back2 = new CardSide(corners,"");
        back2.setDeployedID(1);
        back2.setRelativeCoordinates(new int[]{1, -1});
        deployed.add(back2);
        CardSide back3 = new CardSide(corners,"");
        back3.setDeployedID(2);
        back3.setRelativeCoordinates(new int[]{2, 0});
        deployed.add(back3);
        CardSide back4 = new CardSide(corners,"");
        back4.setDeployedID(3);
        back4.setRelativeCoordinates(new int[]{3, 1});
        deployed.add(back4);
        CardSide back5 = new CardSide(corners,"");
        back5.setDeployedID(4);
        back5.setRelativeCoordinates(new int[]{2, 2});
        deployed.add(back5);
        CardSide back6 = new CardSide(corners,"");
        back6.setDeployedID(5);
        back6.setRelativeCoordinates(new int[]{1, 3});
        deployed.add(back6);
        CardSide back7 = new CardSide(corners,"");
        back7.setDeployedID(6);
        back7.setRelativeCoordinates(new int[]{0, 2});
        deployed.add(back7);

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

    }

    @Test
    public void retrieveNeighboursTest(){
        int[] coord = {42,40}; //card 2
        int[] dir = {-1,1}; //corner TR

        CardSide[] result = this.player.retrieveNeighbours(coord,dir);
        int[] indexes = new int[3];

        for (int i=0; i<3; i++)
            if (result[i]!=null)
                indexes[i] = result[i].getDeployedID();

        int[] expected = {4,0,6};

        assertArrayEquals(expected,indexes);
    }

    @Test
    public void getAvailablePlacementsTest(){
        int index = 2;
        boolean[] expected = {false,false,true,false};

        boolean[] result = player.getAvailablePlacements(index);

        assertArrayEquals(expected,result);
    }

    @Test
    public void placeCardSideTest(){
        Corner[] corners = {new Corner(Symbol.NONE),new Corner(Symbol.ANIMAL),new Corner(Symbol.PLANT),new Corner(Symbol.ANIMAL)};
        CardSide newCard = new CardSide(corners,"");

        int index = 2;
        int corner = 2;

        int expectedDeployedID = this.player.getDeployed().size();
        int[] expectedRelCoord = {3,-1};
        int[] expectedResources = {2,0,5,1,0,0,0};

        boolean result = this.player.placeCardSide(newCard,index,corner);

        assertTrue(result);
        assertEquals(newCard,this.player.getDeployed().getLast());
        assertEquals(expectedDeployedID, newCard.getDeployedID());
        assertEquals(7, this.player.getPlacements()[43][39]);
        assertArrayEquals(expectedRelCoord,newCard.getRelativeCoordinates());

        assertArrayEquals(expectedResources, this.player.getRes_qty());
    }
}
