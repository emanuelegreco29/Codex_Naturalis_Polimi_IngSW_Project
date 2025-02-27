package it.polimi.ingsw.am43.Model.Enum;

/**
 * This enumeration class represents all the possible
 * colors of the {@link it.polimi.ingsw.am43.Model.Player}'s pawns.
 */
public enum PawnColor {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    BLACK;


    /**
     * Returns the ANSI escape code for the corresponding TUI color of the pawn.
     *
     * @return the ANSI escape code for the TUI color
     */
    public String toTUIColor() {
        return switch (this) {
            case RED -> "\033[0;31m";
            case BLUE -> "\033[0;34m";
            case GREEN -> "\033[0;32m";
            case YELLOW -> "\033[0;33m";
            case BLACK -> "\033[0;37m";
        };
    }

    public String toGUIColor(){
        return switch (this) {
            case RED -> "Images/Pinions/Red.png";
            case BLUE -> "Images/Pinions/Blue.png";
            case GREEN -> "Images/Pinions/Green.png";
            case YELLOW -> "Images/Pinions/Yellow.png";
            case BLACK -> "Images/Pinions/Black.png";
        };
    }

    /**
     * Converts the current instance of the enum to its corresponding {@link Integer} value.
     *
     * @return the {@link Integer} value of the enum, ranging from 0 to 4
     */
    public int toInt() {
        return switch (this) {
            case RED -> 0;
            case BLUE -> 1;
            case GREEN -> 2;
            case YELLOW -> 3;
            case BLACK -> 4;
        };
    }
}


