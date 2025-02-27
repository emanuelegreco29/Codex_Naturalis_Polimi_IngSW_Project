package it.polimi.ingsw.am43.Model.Enum;

/**
 * This enumeration class represents all the symbols
 * that {@link it.polimi.ingsw.am43.Model.Cards.Card}s in the game can have.
 */
public enum Symbol {
    ANIMAL,
    INSECT,
    FUNGI,
    PLANT,
    INKWELL,
    QUILL,
    MANUSCRIPT,
    NONE,
    NONEXISTING;

    /**
     * Converts the {@link Symbol} to an {@link Integer} representation.
     *
     * @return         the {@link Integer} representation of the {@link Symbol}
     */
    public int toInt() {
        return switch (this) {
            case ANIMAL -> 0;
            case INSECT -> 1;
            case FUNGI -> 2;
            case PLANT -> 3;
            case INKWELL -> 4;
            case QUILL -> 5;
            case MANUSCRIPT -> 6;
            case NONE -> 7;
            case NONEXISTING -> -1;
        };
    }

    /**
     * A function that converts an {@link Integer} to a {@link Symbol}.
     *
     * @param  i   the {@link Integer} to be converted to a {@link Symbol}
     * @return     the {@link Symbol} corresponding to the input {@link Integer}
     */
    public static Symbol toSymbol(int i) {
        return switch (i) {
            case 0 -> ANIMAL;
            case 1 -> INSECT;
            case 2 -> FUNGI;
            case 3 -> PLANT;
            case 4 -> INKWELL;
            case 5 -> QUILL;
            case 6 -> MANUSCRIPT;
            case 7 -> NONE;
            case -1 -> NONEXISTING;
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    /**
     * Checks if the current symbol is a resource by comparing its {@link Integer} value to the range [0, 3].
     *
     * @return true if the symbol is a resource, false otherwise.
     */
    public boolean isResource() {
        return this.toInt()>=0 && this.toInt()<=3;
    }
}
