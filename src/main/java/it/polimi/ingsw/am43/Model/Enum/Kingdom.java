package it.polimi.ingsw.am43.Model.Enum;

/**
 * This enumeration class represents the different Kingdoms of the cards.
 */
public enum Kingdom {
    NONE,
    ANIMAL,
    INSECT,
    PLANT,
    FUNGI;

    /**
     * Converts an {@link Integer} to a {@link Kingdom}.
     *
     * @param  i    the {@link Integer} to be converted
     * @return      the corresponding {@link Kingdom}
     */
    public static Kingdom toKingdom(int i) {
        return switch (i) {
            case 0 -> ANIMAL;
            case 1 -> INSECT;
            case 2 -> FUNGI;
            case 3 -> PLANT;
            case -1 -> null;
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }
}
