package it.polimi.ingsw.am43.Model.Cards;

import it.polimi.ingsw.am43.Model.Enum.Symbol;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a corner of a {@link CardSide}.
 * It contains the {@link Symbol} associated with this corner.
 */
public class Corner implements Serializable {

    @Serial
    private static final long serialVersionUID = -1383110686590528137L;

    /**
     * The {@link Symbol} associated with this corner.
     */
    private final Symbol symbol;

    /**
     * Get the {@link Symbol} associated with this corner.
     *
     * @return         the {@link Symbol} associated with this corner
     */
    public Symbol getSymbol() {
        return symbol;
    }

    /**
     * Constructor for the Corner.
     *
     * @param symbol  the {@link Symbol} associated with this corner
     */
    public Corner(Symbol symbol) {
        this.symbol = symbol;
    }
}
