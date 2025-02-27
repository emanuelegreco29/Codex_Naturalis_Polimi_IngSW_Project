package it.polimi.ingsw.am43.View.TUI;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.Corner;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Points.*;

import java.util.Arrays;
import java.util.List;

/**
 * The class that prints the cards in the TUI
 */
public class CardPrinter {

    /**
     * ANSI escape codes for colors
     */
    static String RESET = "\033[0m";
    static String RED = "\033[0;31m";
    static String GREEN = "\033[0;32m";
    static String BLUE = "\033[0;34m";
    static String GOLD = "\033[0;33m";
    static String PURPLE = "\033[0;35m";
    static String GREY = "\033[0;37m";

    /**
     * The filled square character
     */
    static char filledSquare = '■';

    /**
     * The length of the card
     */
    static String cardHorizontalSpaceLength = "                           ";

    /**
     * The half-length of the card
     */
    static String cardHalfHorizontalSpaceLength = "             ";

    /**
     * Prints the legend of the game
     */
    public static void printLegend(){

        System.out.println("╔═══════════════════════╗");
        System.out.println("║                       ║");
        System.out.println("║ " + GREEN + "    P" + " - PLANT         " + RESET+"║");
        System.out.println("║ " + BLUE + "    A" + " - ANIMAL        " + RESET+"║");
        System.out.println("║ " + RED + "    F" + " - FUNGI         " + RESET+"║");
        System.out.println("║ " + PURPLE + "    I" + " - INSECT        " + RESET+"║");
        System.out.println("║                       ║");
        System.out.println("║ " + GOLD + "    Q" + " - quill         " + RESET+"║");
        System.out.println("║ " + GOLD + "    I" + " - inkwell       " + RESET+"║");
        System.out.println("║ " + GOLD + "    M" + " - manuscript    " + RESET+"║");
        System.out.println("║                       ║");
        System.out.println("╚═══════════════════════╝");
    }

    /**
     * Prints the card side of a card
     * @param card the card to be printed
     * @param BlackAndWhite if true, the card will be printed in black and white
     */
    public static void printPlayableCardSide(CardSide card, boolean BlackAndWhite){
        String[] cardString = buildPlayableCardSide(card, BlackAndWhite);
        for (String s: cardString){
            System.out.println(s);
        }
    }

    /**
     * Prints two card side in the same row
     * @param card1 the card to be printed on the left
     * @param card2 the card to be printed on the right
     * @param BlackAndWhite1 if true, the card1 will be printed in black and white
     * @param BlackAndWhite2 if true, the card2 will be printed in black and white
     */
    public static void printPlayableCardSideSameRow(CardSide card1, boolean BlackAndWhite1, CardSide card2, boolean BlackAndWhite2){
        String[] cardString1 = buildPlayableCardSide(card1, BlackAndWhite1);
        String[] cardString2 = buildPlayableCardSide(card2, BlackAndWhite2);
        for (int i=0; i<cardString1.length; i++){
            System.out.println(cardString1[i]+"   "+cardString2[i]);
        }
    }

    /**
     * Prints two objective card side in the same row
     * @param card1 the card to be printed on the left
     * @param card2 the card to be printed on the right
     */
    public static void printObjectiveCardSideSameRow(ObjectiveCard card1, ObjectiveCard card2){
        String[] cardString1 = buildObjectiveCard(card1);
        String[] cardString2 = buildObjectiveCard(card2);
        for (int i=0; i<cardString1.length; i++){
            System.out.println(cardString1[i]+"   "+cardString2[i]);
        }
    }

    /**
     * Prints the objective card side of a card
     * @param card the card to be printed
     */
    public static void printObjectiveCardSide(ObjectiveCard card){
        String[] cardString1 = buildObjectiveCard(card);
        for (String s : cardString1) {
            System.out.println(s);
        }
    }

    /**
     * Returns the string array representation of the rule of a card
     * @param pointsRule the rule to be printed
     * @return the string array representation of the rule
     */
    public static String[] printRule(PointsRule pointsRule){
        int points;
        if (pointsRule != null) {
            points = pointsRule.getPoints();
        } else {
            points = 0;
        }
        String[] rule = {"   ", "   ", "   "};
        if (pointsRule instanceof forEachCoveredCorner) rule[1] = RESET + points + "|" + filledSquare;
        else if (pointsRule instanceof forEachResourceSet)
            rule[1] = RESET + points + "|" + toStringSymbol(((forEachResourceSet) pointsRule).getResourceSet().getFirst(), false);
        else if (pointsRule instanceof simplePoints) rule[1] = RESET + " " + (points == 0 ? " " : points) + " ";
        else if (pointsRule instanceof forEachDiagonalPattern) {
            String kingdom = toColorKingdom(((forEachDiagonalPattern) pointsRule).getKingdom());
            if (((forEachDiagonalPattern) pointsRule).getIsTopLeft()) {
                rule[0] = kingdom+filledSquare + "  ";
                rule[1] = " "+ kingdom+filledSquare + " ";
                rule[2] = RESET + points + " " + kingdom + filledSquare;
            }
            else {
                rule[0] = RESET + points + " " + kingdom + filledSquare;
                rule[1] = " "+ kingdom+filledSquare + " ";
                rule[2] = kingdom+filledSquare + "  ";
            }

        }
        else if (pointsRule instanceof forEachLPattern) {
            String[] res = Arrays.stream(((forEachLPattern) pointsRule).getRes()).map(CardPrinter::toColorKingdom).toArray(String[]::new);
            switch (((forEachLPattern) pointsRule).getDirection()){

                case 0 -> {
                    rule[0] = " "             + res[1]+filledSquare + " ";
                    rule[1] = " "             +" "                  + res[0]+filledSquare ;
                    rule[2] = RESET + points  + " "                 + res[0]+filledSquare;
                }
                case 1 -> {
                    rule[0] = " "                 + res[1]+ filledSquare  +" ";
                    rule[1] = res[0]+filledSquare + " "                   +" ";
                    rule[2] = res[0]+filledSquare + " "                   +RESET + points ;
                }
                case 2 -> {
                    rule[0] = RESET + points + " "                   + res[0] + filledSquare ;
                    rule[1] = " "            + " "                   + res[0] + filledSquare ;
                    rule[2] = " "            + res[1]+filledSquare   + " ";
                }
                case 3 -> {
                    rule[0] = res[0]+filledSquare + " "                   + RESET + points;
                    rule[1] = res[0]+filledSquare + " "                   +" ";
                    rule[2] = " "                 + res[1] + filledSquare + " ";
                }


            }


        }

        return rule;
    }

    /**
     * Returns the string array representation of the objective card
     * @param card the card to be printed
     * @return the string array representation of the objective card
     */
    public static String[] buildObjectiveCard(ObjectiveCard card){
        PointsRule pointsRule = card.getRule();
        String[] rule = printRule(pointsRule);

        String kingdomColor = GREY;

        String[] tlCorner = new String[3];
        tlCorner[0] = "╔══════";
        tlCorner[1] = "║      ";
        tlCorner[2] = "║      ";
        String[] trCorner = new String[3];
        trCorner[0] = "══════╗";
        trCorner[1] = "      ║";
        trCorner[2] = "      ║";
        String[] blCorner = new String[3];
        blCorner[0] = "║      ";
        blCorner[1] = "║      ";
        blCorner[2] = "╚══════";
        String[] brCorner = new String[3];
        brCorner[0] = "      ║";
        brCorner[1] = "      ║";
        brCorner[2] = "══════╝";


        String[] result = new String[6];
        result[0] = (kingdomColor+tlCorner[0]+"═════════════"+trCorner[0]);
        result[1] = (kingdomColor+tlCorner[1]+"     "+RESET + rule[0]+"     "+kingdomColor+trCorner[1]);
        result[2] = (kingdomColor+tlCorner[2]+"     "+RESET + rule[1]+"     "+kingdomColor+trCorner[2]);
        result[3] = (kingdomColor+blCorner[0]+"     "+RESET + rule[2]+"     "+kingdomColor+brCorner[0]);
        result[4] = (kingdomColor+blCorner[1]+"             "+brCorner[1]);
        result[5] = (kingdomColor+blCorner[2]+"═════════════"+brCorner[2]+RESET);
        return result;


    }

    /**
     * Returns the string array representation of the playable card
     * @param card the card to be printed
     * @param BlackAndWhite if true, the card will be printed in black and white
     * @return the string array representation of the playable card
     */
    public static String[] buildPlayableCardSide(CardSide card, boolean BlackAndWhite) {


        String kingdom;
        if (BlackAndWhite) kingdom = GREY;
        else {
            switch (card.getKingdom()) {
                case ANIMAL -> kingdom = BLUE;
                case INSECT -> kingdom = PURPLE;
                case FUNGI -> kingdom = RED;
                case PLANT -> kingdom = GREEN;
                default -> kingdom = GOLD;
            }
        }

        Corner[] corners = card.getCorners();

        String[] tlCorner = new String[3];
        tlCorner = buildCorner(corners[0].getSymbol(), kingdom, BlackAndWhite);

        String[] trCorner = new String[3];
        trCorner = buildCorner(corners[1].getSymbol(), kingdom, BlackAndWhite);

        String[] blCorner = new String[3];
        blCorner = buildCorner(corners[2].getSymbol(), kingdom, BlackAndWhite);

        String[] brCorner = new String[3];
        brCorner = buildCorner(corners[3].getSymbol(), kingdom, BlackAndWhite);

        if (corners[0].getSymbol().equals(Symbol.NONEXISTING)) {
            tlCorner[0] = "╔══════";
            tlCorner[1] = "║      ";
            tlCorner[2] = "║      ";
        }

        if (corners[1].getSymbol().equals(Symbol.NONEXISTING)) {
            trCorner[0] = "══════╗";
            trCorner[1] = "      ║";
            trCorner[2] = "      ║";
        }

        if (corners[2].getSymbol().equals(Symbol.NONEXISTING)) {
            blCorner[0] = "║      ";
            blCorner[1] = "║      ";
            blCorner[2] = "╚══════";
        }

        if (corners[3].getSymbol().equals(Symbol.NONEXISTING)) {
            brCorner[0] = "      ║";
            brCorner[1] = "      ║";
            brCorner[2] = "══════╝";
        }

        List<Symbol> requisites = card.getRequiredResources();
        StringBuilder req = new StringBuilder();
        int count = 0;
        for (Symbol r : requisites) {
            if (r != null && r != Symbol.NONE && r != Symbol.NONEXISTING)
                req.append(toStringSymbol(r, BlackAndWhite));

            count++;
        }
        int right = (5 - count) / 2;
        int left = 5 - count - right;
        req.append(" ".repeat(Math.max(0, right)));
        for (int i = 0; i < left; i++) req.insert(0, " ");

        PointsRule pointsRule = card.getPointsRule();

        String[] rule = printRule(pointsRule);

        String fixedResources = "";
        List<Symbol> fixed = card.getFixedResources();
        if (fixed != null){
            switch(fixed.size()){
                case 0 -> fixedResources += "  ";
                case 1 -> fixedResources += "  ";
                case 2 -> fixedResources += " ";
                case 3 -> fixedResources += " ";
            }
            for (Symbol s : fixed) {
                fixedResources += toStringSymbol(s, BlackAndWhite);
            }
            switch(fixed.size()){
                case 0 -> fixedResources += "  ";
                case 1 -> fixedResources += " ";
                case 2 -> fixedResources += " ";
                case 3 -> fixedResources += "";
            }
        }
        else {
            fixedResources = "    ";
        }




        String[] result = new String[8];
        result[0] = (kingdom+tlCorner[0]+"═════════════"+trCorner[0]);
        result[1] = (kingdom+tlCorner[1]+"     "+rule[0]+kingdom+"     "+trCorner[1]);
        result[2] = (kingdom+tlCorner[2]+"     "+rule[1]+kingdom+"     "+trCorner[2]);
        result[3] = (kingdom+"║           "+ rule[2] + kingdom +"           ║");
        result[4] = (kingdom+"║          "+ fixedResources + kingdom +"           ║");
        result[5] = (kingdom+blCorner[0]+"             "+brCorner[0]);
        result[6] = (kingdom+blCorner[1]+"    "+req+kingdom+"    "+brCorner[1]);
        result[7] = (kingdom+blCorner[2]+"═════════════"+brCorner[2]+RESET);

        return result;

    }

    /**
     * Returns the string array representation of the corner
     * @param s the symbol of the corner
     * @param k the kingdom of the corner
     * @param blackAndWhite if true, the corner will be printed in black and white
     * @return the string array representation of the corner
     */
    private static String[] buildCorner(Symbol s, String k, boolean blackAndWhite){
        return new String[]{"╔═════╗", "║  "+toStringSymbol(s, blackAndWhite)+k+"  ║", "╚═════╝"};
    }

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
     * Returns the string representation of the symbol
     * @param s the symbol to be printed
     * @param blackAndWhite if true, the symbol will be printed in black and white
     * @return the string representation of the symbol
     */
    private static String toStringSymbol(Symbol s, boolean blackAndWhite) {

        if (blackAndWhite) {
            return switch (s) {
                case ANIMAL -> GREY + "A";
                case INSECT -> GREY + "I";
                case FUNGI -> GREY + "F";
                case PLANT -> GREY + "P";
                case INKWELL -> GREY + "I";
                case QUILL -> GREY + "Q";
                case MANUSCRIPT -> GREY + "M";
                case NONE -> " ";
                case NONEXISTING -> "X";

            };
        } else {
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
}
