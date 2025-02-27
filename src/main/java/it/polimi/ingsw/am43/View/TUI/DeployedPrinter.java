package it.polimi.ingsw.am43.View.TUI;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.Corner;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to print the deployed cards in the TUI
 */
public class DeployedPrinter {

    /**
     * ANSI escape codes for colors
     */
    static String RESET = "\033[0m";
    static String RED = "\033[0;31m";
    static String GREEN = "\033[0;32m";
    static String BLUE = "\033[0;34m";
    static String GOLD = "\033[0;33m";
    static String PURPLE = "\033[0;35m";

    /**
     * filled square character
     */
    static char filledSquare = '■';

    //card dimensions
    /**
     * length and height of the card
     */
    static int length = 12; //min 7
    static int height = 4;  //min 4


    /**
     * String representation of a card side
     * @param cardSide the card side to be represented
     * @return a string matrix representing the card side
     */
    public static String[][] toStringCardSide(CardSide cardSide) {
        int deployedID= cardSide.getDeployedID();

        String kingdom = toColorKingdom(cardSide.getKingdom());

        //corners
        Corner[] corners = cardSide.getCorners();
        String[] cornersStrings = new String[4];
        for (int i = 0; i < 4; i++) {
            cornersStrings[i] = buildCorner(corners[i].getSymbol(), i, kingdom);
        }

        //card
        String[][] card = new String[length][height];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                card[i][j] = " ";
            }
        }

        /*  0123456
         *   A═════B 0
         *   ║II   ║ 1
         *   ║II  6║ 2
         *   C═════D 3
         * */

        for (int i = 1; i < length-1; i++) {
            card[i][0] = kingdom + "═";
            card[i][height-1] = kingdom + "═";
        }
        for (int j = 1; j < height-1; j++) {
            card[0][j] = kingdom + "║";
            card[length-1][j] = kingdom + "║";
        }

        card[0][0] = cornersStrings[0];
        card[length-1][0] = cornersStrings[1];
        card[0][height-1] = cornersStrings[2];
        card[length-1][height- 1] = cornersStrings[3];

        //deployedID
        card[length-2][height-2] = kingdom + Integer.toString(deployedID % 10);
        if (deployedID > 9) {
            card[length-3][height-2] = kingdom + Integer.toString(deployedID / 10);
        }

        //resources
        List<Symbol> fixed = cardSide.getFixedResources();
        if (fixed != null) {
            int count = fixed.size();
            String[] reqString = new String[count];
            for (int i = 0; i < count; i++) {
                reqString[i] = toStringSymbol(fixed.get(i));
            }
            int leftOffset = (length-7) / 2;
            int topOffset = (height-4)/2;
            for (int i = 0; i < count; i++) {

                switch (i) {
                    case 0 -> card[leftOffset+2][topOffset+1] = reqString[i];
                    case 1 -> card[leftOffset+2][topOffset+2] = reqString[i];
                    case 2 -> card[leftOffset+1][topOffset+1] = reqString[i];
                    case 3 -> card[leftOffset+1][topOffset+2] = reqString[i];
                }
            }
        }
        return card;
    }


    /**
     * Print the deployed cards
     * @param deployed the list of deployed CardSide
     * @param placements the placement matrix of the player
     */
    public static void printDeployedCard(ArrayList<CardSide> deployed, int[][] placements) {
        int offset = Player.OFFSET;

        int[] maxTopLeft = {100,100};
        int[] maxBottomRight = {0, 0};

        //find max topLeft and the max bottomRight cells not empty in the placements
        for (CardSide card: deployed){
            int x = card.getRelativeCoordinates()[1]+offset;
            int y = card.getRelativeCoordinates()[0]+offset;
            if (x<maxTopLeft[1]) maxTopLeft[1] = x;
            if (y<maxTopLeft[0]) maxTopLeft[0] = y;
            if (x>maxBottomRight[1]) maxBottomRight[1] = x;
            if (y>maxBottomRight[0]) maxBottomRight[0] = y;
        }
        //System.out.println("TopLeft:" + maxTopLeft[0] + "," + maxTopLeft[1]);
        //System.out.println("BottomRight:" + maxBottomRight[0] + "," + maxBottomRight[1]);

        //table
        int dimx = 1+(length-1)*(maxBottomRight[1]-maxTopLeft[1]+1);
        int dimy = 1+(height-1)*(maxBottomRight[0]-maxTopLeft[0]+1);
        String[][] table = new String[dimy][dimx];
        for (int i=0; i<dimy; i++){
            for (int j=0; j<dimx; j++){
                table[i][j] = " ";
            }
        }

        //System.out.println("Table size: y:" + dimy + ", x:" + dimx);

        //fill table
        for(CardSide card: deployed){
            String[][] cardStr = toStringCardSide(card);
            int x = card.getRelativeCoordinates()[1] + offset - maxTopLeft[1];
            int y = card.getRelativeCoordinates()[0] + offset - maxTopLeft[0];
            int printX = (length-1)*x;
            int printY = (height-1)*y;
            //uncomment to reverse y axes in the deployed print
            //printY = (height-1)*((dimy-1)/(height-1) -1-y);


            //System.out.println("Card " + card.getDeployedID() + " at " + y + "," + x +" -> "+ printY + "," +printX)  ;

            for (int i=0; i<length; i++){
                for (int j=0; j<height; j++){
                    //cardStr uses [x][y] convention
                    table[printY +j][printX +i] = cardStr[i][j];
                }
            }
        }

        //print table
        for (int j=0; j<dimy; j++){
            for (int i=0; i<dimx; i++){
                System.out.print(table[j][i]);
            }
            System.out.println();
        }
        System.out.println(RESET);

    }


    /**
     * Build a corner of the card
     * @param s the symbol of the corner
     * @param i the index of the corner
     * @param kingdom the color of the kingdom
     * @return a string representing the corner
     */
    private static String buildCorner(Symbol s, int i, String kingdom){
        if (toStringSymbol(s).equals("X")){
            return switch (i) {
                case 0 -> kingdom+ "╔";
                case 1 -> kingdom+ "╗";
                case 2 -> kingdom+ "╚";
                case 3 -> kingdom+ "╝";
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };
        } else if (toStringSymbol(s).equals(" ")){
            return kingdom+ filledSquare;
        }
        else {
            return toStringSymbol(s);
        }
    }

    /**
     * Convert a kingdom to a color
     * @param k the kingdom
     * @return the color
     */
    private static String toColorKingdom(Kingdom k){
        return switch (k) {
            case NONE -> GOLD;
            case ANIMAL -> BLUE;
            case INSECT -> PURPLE;
            case FUNGI -> RED;
            case PLANT -> GREEN;
        };
    }

    /**
     * Convert a symbol to its string representation
     * @param s the symbol
     * @return the string
     */
    private static String toStringSymbol(Symbol s) {
        return switch (s) {
            case ANIMAL -> BLUE + "A";
            case INSECT -> PURPLE + "I";
            case FUNGI -> RED + "F";
            case PLANT -> GREEN + "P";
            case INKWELL -> GOLD + "I";
            case QUILL -> GOLD + "Q";
            case MANUSCRIPT -> GOLD + "M";
            case NONE -> " ";
            case NONEXISTING -> "X";
        };
    }





}
