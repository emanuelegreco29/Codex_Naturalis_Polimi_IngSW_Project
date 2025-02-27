package it.polimi.ingsw.am43.Model.Cards;

import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Player;
import it.polimi.ingsw.am43.Model.Points.PointsRule;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * This class represents the side of a {@link Card}.
 * It contains the ID the card obtains once deployed, the
 * array of {@link Corner}s of the side and the {@link PointsRule}.
 * It also contains the relative coordinates that the card-side
 * has, with respect to the starting card.
 */
public class CardSide implements Serializable {

    @Serial
    private static final long serialVersionUID = -4726145402960013686L;

    /**
     * The path of the card side.
     */
    private final String pngPath;

    /**
     * The ID of the CardSide once deployed.
     */
    private int deployedID;

    /**
     * The {@link Corner}s of the CardSide.
     */
    private final Corner[] corners;

    /**
     * The relative coordinates on the board, with respect to the starting card.
     */
    private int[] relativeCoordinates;

    /**
     * Constructor for the CardSide
     *
     * @param corners      The {@link Corner}s of the CardSide
     */
    public CardSide(Corner[] corners, String pngPath) {
        this.relativeCoordinates = new int[]{0, 0};
        this.corners = corners;
        this.pngPath= pngPath;
    }

    /**
     * Returns the path to the PNG image file representing this CardSide.
     *
     * @return the path to the PNG image file
     */
    public String getPngPath() {
        return pngPath;
    }

    /**
     * A function that returns the deployedID of the card.
     *
     * @return         	deployedID of the card
     */
    public int getDeployedID() {
        return deployedID;
    }

    /**
     * A function that sets the deployedID of the card.
     */
    public void setDeployedID(int deployedID) {
        this.deployedID = deployedID;
    }

    /**
     * A function that retrieves the list of {@link Symbol}s
     * which represent the fixed resources.
     * Here the method returns null since this class is extended further
     *
     * @return         list of {@link Symbol}s representing the fixed resources
     */
    public List<Symbol> getFixedResources() {
        return null;
    }

    /**
     * A function that returns the {@link PointsRule} of the card side.
     * Here the method returns null since this class is extended further
     *
     * @return            {@link PointsRule} associated to the card side
     */
    public PointsRule getPointsRule() {
        return null;
    }

    /**
     * A function that returns all the {@link Corner}s of the card side.
     *
     * @return         	Array of {@link Corner}s
     */
    public Corner[] getCorners() {
        return corners;
    }

    /**
     * A function that retrieves the relative coordinates of the CardSide.
     *
     * @return         Relative coordinates
     */
    public int[] getRelativeCoordinates() {
        return relativeCoordinates;
    }

    /**
     * A function that sets the relative coordinates of the CardSide, once placed.
     *
     * @param  relativeCoordinates    the array of relative coordinates to set
     */
    public void setRelativeCoordinates(int[] relativeCoordinates) {
        this.relativeCoordinates = relativeCoordinates;
    }

    /**
     * A function that retrieves the status of each {@link Corner} of the CardSide.
     *
     * @return          an array indicating the presence of each {@link Corner} (1 if present, 0 if not)
     */
    public int[] getCornersStatus() {
        int[] cornersPresence = new int[4];
        for (int i = 0; i < corners.length; i++) {
            cornersPresence[i] = (corners[i].getSymbol() != Symbol.NONEXISTING) ? 1 : 0;
        }
        return cornersPresence;
    }

    /**
     * A function that checks if the {@link Player} has enough resources to place the card.
     *
     * @param  playerRes array of player's resources
     * @return          true if enough resources, false otherwise
     */
    public boolean checkGoldRequisites(int[] playerRes) {
        return true;
    }

    /**
     * A function that returns the {@link Kingdom} of the card.
     *
     * @return            {@link Kingdom} of the card
     */
    public Kingdom getKingdom() {
        return Kingdom.NONE;
    }

    /**
     * A function that returns the list of {@link Symbol}s
     * which represent the required resources.
     *
     * @return         list of {@link Symbol}s of required resources
     */
    public List<Symbol> getRequiredResources() {
        return new ArrayList<>();
    }
}
