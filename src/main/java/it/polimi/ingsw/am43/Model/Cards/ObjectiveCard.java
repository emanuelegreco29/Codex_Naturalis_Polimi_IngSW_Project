package it.polimi.ingsw.am43.Model.Cards;

import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Player;
import it.polimi.ingsw.am43.Model.Points.PointsRule;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents an objective card, either owned by a {@link Player} as secret
 * objective or as a common objective for all the players of the game.
 * It contains a {@link Kingdom} and a {@link PointsRule}
 */
public class ObjectiveCard extends Card implements Serializable {

    @Serial
    private static final long serialVersionUID = 6460421369968323864L;

    /**
     * The {@link PointsRule} associated to the card.
     */
    private final PointsRule rule;

    /**
     * The {@link Kingdom} of the card.
     */
    private final Kingdom kingdom;

    /**
     * The path of the card's front side PNG file.
     */
    private final String front_png_path;

    /**
     * Constructor for the ObjectiveCard class.
     *
     * @param id                The ID of the card
     * @param front_png_path    The path of the card's front side PNG file
     * @param kingdom           The {@link Kingdom} of the card
     * @param rule              The {@link PointsRule} associated to the card
     */
    public ObjectiveCard(int id, String front_png_path, Kingdom kingdom, PointsRule rule) {
        super(id);
        this.rule = rule;
        this.kingdom = kingdom;
        this.front_png_path = front_png_path;
    }

    /**
     * A function that returns the {@link PointsRule} of the card.
     *
     * @return            {@link PointsRule} associated to the card
     */
    public PointsRule getRule() {
        return rule;
    }

    /**
     * A function that returns the path of the card's front side.
     *
     * @return         	String that contains the path
     */

    public String getFrontPath() {
        return front_png_path;
    }

    /**
     * A function that returns the {@link Kingdom} of the card.
     *
     * @return            {@link Kingdom} of the card
     */
    public Kingdom getKingdom() {
        return kingdom;
    }
}
