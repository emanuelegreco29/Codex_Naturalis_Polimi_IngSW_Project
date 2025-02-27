package it.polimi.ingsw.am43.ModelTests.PointsRulesTests;

import it.polimi.ingsw.am43.Model.Cards.Corner;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.ResourceFrontSide;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Player;
import it.polimi.ingsw.am43.Model.Points.PointsRule;
import it.polimi.ingsw.am43.Model.Points.forEachLPattern;
import it.polimi.ingsw.am43.View.TUI.CardPrinter;
import it.polimi.ingsw.am43.View.TUI.DeployedPrinter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class LPatternTest {

    private Player player;

    @Before
    public void setUp(){
        this.player = new Player("j", PawnColor.RED);

        Corner[] corners = {new Corner(Symbol.NONE), new Corner(Symbol.NONE), new Corner(Symbol.NONE), new Corner(Symbol.NONE)};

        this.player.placeCardSide(new ResourceFrontSide(corners, Kingdom.PLANT,null, ""),-1,0);

        this.player.placeCardSide(new ResourceFrontSide(corners, Kingdom.PLANT,null, ""),0,2);
        this.player.placeCardSide(new ResourceFrontSide(corners, Kingdom.ANIMAL,null, ""),0,3);

        this.player.placeCardSide(new ResourceFrontSide(corners, Kingdom.ANIMAL,null, ""),1,3);
        this.player.placeCardSide(new ResourceFrontSide(corners, Kingdom.ANIMAL,null, ""),3,3);
        this.player.placeCardSide(new ResourceFrontSide(corners, Kingdom.ANIMAL,null, ""),4,2);
        this.player.placeCardSide(new ResourceFrontSide(corners, Kingdom.FUNGI,null, ""),5,3);

        DeployedPrinter.printDeployedCard(this.player.getDeployed(), this.player.getPlacements());

    }

    @Test
    public void LTest1(){

        PointsRule rule = new forEachLPattern(3, 0, new Kingdom[]{Kingdom.ANIMAL,Kingdom.PLANT});
        ObjectiveCard obj = new ObjectiveCard(0, "", Kingdom.NONE, rule);
        CardPrinter.printObjectiveCardSide(obj);
        int expectedScore = 6;
        int SCORE = obj.getRule().computePoints(player);

        assertEquals(expectedScore,SCORE);

    }
}
