package it.polimi.ingsw.am43.Model;

import it.polimi.ingsw.am43.Model.Cards.*;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Enum.Symbol;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * This class represents a player in the game.
 * It contains the player's username, its {@link PawnColor}, its starting card and
 * the hand.
 * It also contains the list of deployed {@link CardSide}s and the player's score, as well as
 * the secret objective to fulfill.
 * Lastly, the class contains an array of integers to store the resources obtained by the player.
 */
@SuppressWarnings("SpellCheckingInspection")
public class Player implements Serializable {

    @Serial
    private static final long serialVersionUID = 4739584014082362305L;

    /**
     * Player's username.
     */
    private final String username;

    /**
     * Player's {@link PawnColor}.
     */
    private final PawnColor pawn_color;

    /**
     * The starting card of the player, dealt at the beginning of the game.
     */
    private PlayableCard startingCard;

    /**
     * An array of {@link PlayableCard}s that represents the player's hand, dealt
     * at the beginning of the game.
     */
    private PlayableCard[] in_hand;

    /**
     * A list of {@link CardSide}s that represents the player's deployed cards.
     * The player deploys cards, but for the sake of the mere functionality, only
     * the placed (and displayed) {@link CardSide}s are stored, since are the ones
     * used for the computation of resources and score.
     */
    private ArrayList<CardSide> deployed;

    /**
     * A matrix of {@link CardSide}s that represents the player's placements.
     * The matrix is actually of integers, with every element being -1 if empty and
     * an integer otherwise. The integer is the deployedID of a {@link CardSide}.
     * By doing so, the deployedIDs always increase, and it is possible to effortlessly
     * understand which card is on top of the other, the order of placement and much more.
     */
    private int[][] placements;

    /**
     * The player's secret objective.
     */
    private ObjectiveCard personal_objective;

    /**
     * Player's score.
     */
    private int score;

    /**
     * The array that stores the number of resources obtained by the player.
     * The resources are ordered as follows: Animal, Insect, Fungi, Plant, Inkwell, Quill, Manuscript
     */
    private int[] res_qty = {0,0,0,0,0,0,0};

    /**
     * This value is used to compute the relative coordinates with ease.
     * The starting card is assumed to be placed at the center of the matrix,
     * in position (40,40).
     */
    public static final int OFFSET = 40;

    /**
     * Constructor for the Player class.
     *
     * @param username        Player's username
     * @param color           Player's {@link PawnColor}
     */
    public Player(String username, PawnColor color) {
        this.username = username;
        this.pawn_color = color;

        // Initialize the matrix of placements with all empty cells (represented with a -1)
        this.placements = new int[81][81];
        for (int i=0; i<placements.length; i++)
            for (int j=0; j<placements.length; j++)
                placements[i][j] = -1;

        this.score = 0;
        this.in_hand = new PlayableCard[3];
        this.deployed = new ArrayList<>();
        this.personal_objective = null;
    }

    /**
     * A method that retrieves the list of deployed {@link CardSide}s.
     *
     * @return         List of deployed {@link CardSide}s
     */
    public ArrayList<CardSide> getDeployed() {
        return deployed;
    }

    /**
     * A method that retrieves the player's placements.
     *
     * @return         Array of cards' placements
     */
    public int[][] getPlacements() {
        return placements;
    }

    /**
     * A method that retrieves the list of {@link PlayableCard}s that
     * the player has in hand, ready to be deployed.
     *
     * @return    An array of deployable {@link PlayableCard}s in player's hand
     */
    public PlayableCard[] getInHand() {
        return in_hand;
    }

    /**
     * A method that retrieves the player's username.
     *
     * @return         Player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * A method that retrieves the player's {@link PawnColor}.
     *
     * @return         Player's {@link PawnColor}
     */
    public PawnColor getPawnColor() {
        return pawn_color;
    }

    /**
     * A method that retrieves the player's starting card.
     *
     * @return         Player's starting card
     */
    public PlayableCard getStartingCard() {
        return startingCard;
    }

    /**
     * A method that retrieves the player's score.
     *
     * @return         Player's score
     */
    public int getScore() {
        return score;
    }

    /**
     * A method that retrieves the player's resources.
     *
     * @return         Player's resources
     */
    public int[] getResources(){
        return res_qty;
    }

    /**
     * A method that retrieves the player's secret {@link ObjectiveCard}.
     *
     * @return         Personal {@link ObjectiveCard}
     */
    public ObjectiveCard getPersonalObjective() {
        return personal_objective;
    }

    /**
     * A method that sets the secret objective for the player.
     *
     * @param  personal_objective  the {@link ObjectiveCard} to set as the secret objective
     */
    public void setPersonalObjective(ObjectiveCard personal_objective) {
        this.personal_objective = personal_objective;
    }

    /**
     * A method that sets the player's deployed list.
     *
     * @param  deployed  List of player's deployed {@link CardSide}s
     */
    public void setDeployed(ArrayList<CardSide> deployed) {
        this.deployed = deployed;
    }

    /**
     * A method that sets the player's placements array.
     *
     * @param  placements  Arrays of placements to set
     */
    public void setPlacements(int[][] placements) {
        this.placements = placements;
    }

    /**
     * A method that sets the player's hand.
     *
     * @param  in_hand  the hand to set as player's hand
     */
    public void setInHand(PlayableCard[] in_hand) {
        this.in_hand = in_hand;
    }

    /**
     * A method that sets the player's starting card.
     *
     * @param  startingCard  the card to set as player's starting card
     */
    public void setStartingCard(PlayableCard startingCard) {
        this.startingCard = startingCard;
    }

    /**
     * A method that retrieves the player's resources' quantities.
     *
     * @return             Array of resources' quantities
     */
    public int[] getRes_qty() {
        return res_qty;
    }

    /**
     * A method that sets the player's resources' quantities.
     *
     * @param  res_qty  the array to set as player's resources' quantities
     */
    public void setRes_qty(int[] res_qty) {
        this.res_qty = res_qty;
    }

    /**
     * A method that identifies which {@link Corner}s on a placed {@link CardSide}
     * are available for a card's placement.
     * Given the index of a placed card, it returns a boolean array to identify what corners are
     * available for the placement (indexes: 0 for TL, 1 for TR, 2 for BR, 3 for BL).
     *
     * @param  index	index of a placed {@link CardSide}
     * @return          array of booleans to identify what {@link Corner}s are available for the placement
     */
    public boolean[] getAvailablePlacements(int index){
        boolean[] result = {false,false,false,false};

        int[] coord = new int[2];
        for (int i=0; i<2;i++) coord[i] = this.deployed.get(index).getRelativeCoordinates()[i] + OFFSET;

        for (int i=0; i<4; i++){
            int[] v = toVector(i);

            CardSide[] neighbours = retrieveNeighbours(coord, v);
            int[] nIndexes = getInvertedCorners(i);

            // Check for free space, available corner on this card and corners on other cards nearby
            if (this.placements[coord[0]+v[0]][coord[1]+v[1]]==-1)
                if (this.deployed.get(index).getCornersStatus()[i] == 1)
                    if (neighbours[0] == null || neighbours[0].getCornersStatus()[nIndexes[0]] == 1)
                        if (neighbours[1] == null || neighbours[1].getCornersStatus()[nIndexes[1]] == 1)
                            if (neighbours[2] == null || neighbours[2].getCornersStatus()[nIndexes[2]] == 1)
                                result[i] = true;
        }

        return result;
    }


    private int[] getInvertedCorners(int corner){
        int[] invertedIndexes = new int[3];
        //se corner dispari ruoto in senso antiorario (--)
        if (corner%2==1) {
            for (int i = 0; i < invertedIndexes.length; i++) {
                invertedIndexes[i] = corner - (i + 1);
                if (invertedIndexes[i] < 0) invertedIndexes[i] = invertedIndexes[i] + 4;
            }
        } //altrimenti orario (++)
        else {
            for (int i = 0; i < invertedIndexes.length; i++) {
                invertedIndexes[i] = corner + (i + 1);
                if (invertedIndexes[i] > 3) invertedIndexes[i] = invertedIndexes[i] - 4;
            }
        }

        return invertedIndexes;
    }

    /**
     * A method that converts an index into a vector.
     * 0 TL
     * 1 TR
     * 2 BL
     * 3 BR
     *
     * @param  index	the index to convert to a vector
     * @return         	an array representing the converted vector
     */
    private int[] toVector(int index){ //3
        int[] vector = new int[2];

        vector[0] = index/2; //1
        vector[1] = index%2; //1

        vector[0] = (vector[0]*2)-1; //1
        vector[1] = (vector[1]*2)-1; //1

        return vector;
    }

    /**
     * A method that given the index of a {@link CardSide} and a direction (corner),
     * retrieves the "other 3 vertices of the square".
     * IN ORDER: moving along x, along y and diagonally
     *
     * @param  coord	array of coordinates
     * @param  dir	    array of directions
     * @return         	an array of neighbours {@link CardSide}s
     */
    public CardSide[] retrieveNeighbours(int[] coord, int[] dir){
        CardSide[] neighbours = new CardSide[3];
        int cardIndex;

        //move along dirX
        cardIndex = placements[coord[0]][coord[1]+2*dir[1]];
        if (cardIndex!=-1) neighbours[0] = deployed.get(cardIndex);

        //move along dirY
        cardIndex = placements[coord[0]+2*dir[0]][coord[1]];
        if (cardIndex!=-1) neighbours[1] = deployed.get(cardIndex);


        //move along both
        cardIndex = placements[coord[0]+2*dir[0]][coord[1]+2*dir[1]];
        if (cardIndex!=-1) neighbours[2] = deployed.get(cardIndex);


        return neighbours;
    }

    /**
     * A method that adds the {@link CardSide} to player's deployed and placements,
     * adds the deployedID, checks on which cards it's being placed
     * and calls for updates.
     *
     * @param  toBePlaced  the {@link CardSide} to be placed
     * @param  index       the index where the {@link CardSide} is to be placed (-1 for the starting)
     * @param  corner      the corner where the {@link CardSide} is to be placed (for the starting put whatever)
     * @return             true if the placement was successful, false otherwise
     */
    public boolean placeCardSide(CardSide toBePlaced, int index, int corner){

        //STARTING CARD (index = -1)
        if (index==-1){
            toBePlaced.setDeployedID(0);
            toBePlaced.setRelativeCoordinates(new int[]{0,0});
            this.deployed.add(toBePlaced);
            this.placements[OFFSET][OFFSET] = 0;
            //this.updateResources(toBePlaced,new ArrayList<CardSide>(),new ArrayList<Integer>());
            for (Corner c : toBePlaced.getCorners()) {
                int ind = c.getSymbol().toInt();
                if (ind>=0&&ind<=6) this.res_qty[ind]++;
            }
            if(toBePlaced.getFixedResources()!=null){
                for(Symbol s : toBePlaced.getFixedResources()) {
                    if(s!=null) this.res_qty[s.toInt()]++;
                }
            }
            return true;
        }

        //check legal and requisites (for gold cards)
        if (!getAvailablePlacements(index)[corner]){
            System.out.println("ILLEGAL PLACEMENT");
            return false;
        }


        if (!toBePlaced.checkGoldRequisites(res_qty)){
            System.out.println("UNSATISFIED REQUISITES");
            return false;
        }


        //set the deployedID
        try {
            toBePlaced.setDeployedID(this.deployed.getLast().getDeployedID()+1);
        } catch (NoSuchElementException e){
            toBePlaced.setDeployedID(0);
        }

        //add to deployed
        deployed.add(toBePlaced);

        //add to placements
        int absX, absY;
        absX = deployed.get(index).getRelativeCoordinates()[0]+OFFSET;
        absY = deployed.get(index).getRelativeCoordinates()[1]+OFFSET;

        int[] coord = {absX, absY};
        int[] v = toVector(corner);
        this.placements[absX+v[0]][absY+v[1]] = toBePlaced.getDeployedID();

        //set relativeCoordinates
        toBePlaced.setRelativeCoordinates(new int[]{absX + v[0] - OFFSET, absY + v[1] - OFFSET});

        //updates
        ArrayList<CardSide> covered_cards = new ArrayList<>();
        ArrayList<Integer> covered_corner_indexes = new ArrayList<>();

        covered_cards.add(this.deployed.get(index));
        covered_corner_indexes.add(corner);

        CardSide[] neighbours = retrieveNeighbours(coord, v);
        int[] nIndexes = getInvertedCorners(corner);

        for (int i=0;i<neighbours.length;i++){
            if (neighbours[i]!=null) {
                covered_cards.add(neighbours[i]);
                covered_corner_indexes.add(nIndexes[i]);
            }
        }

        this.updateResources(toBePlaced,covered_cards,covered_corner_indexes);
        this.updateScore(toBePlaced);

        return true;

    }

    /**
     * A method that updates the player resources quantity and
     * the object quantity according to the deployed {@link CardSide}s.
     *
     * @param  placed_card            {@link CardSide} placed
     * @param  covered_cards          List of {@link CardSide}s covered
     * @param  covered_corner_indexes List of indexes of covered {@link Corner}s
     */
    private void updateResources(CardSide placed_card, ArrayList<CardSide> covered_cards, ArrayList<Integer> covered_corner_indexes) {
        for (int i=0; i<covered_cards.size(); i++) {
            int index = covered_cards.get(i).getCorners()[covered_corner_indexes.get(i)].getSymbol().toInt();
            if (index>=0&&index<=6) this.res_qty[index]--;
        }

        for (Corner c : placed_card.getCorners()) {
            int index = c.getSymbol().toInt();
            if (index>=0&&index<=6) this.res_qty[index]++;
        }
        if(placed_card.getFixedResources()!=null && !placed_card.getFixedResources().isEmpty()){
            this.res_qty[placed_card.getFixedResources().getFirst().toInt()]++;
        }
    }

    /**
     * A method that computes the resources of the player
     * based on the deployed {@link CardSide}s and the resources
     * associated with their visible {@link Corner}s.
     * It is called only after the placement phase.
     */
    public void computeResources() {

        Arrays.fill(res_qty, 0);

        for (CardSide card : deployed) {
            //sum the fixed resources
            if (card.getFixedResources()!=null)
                for (int i : card.getFixedResources().stream().mapToInt(Symbol::toInt).toArray())
                    res_qty[i]++;


            int card_x = card.getRelativeCoordinates()[0] + OFFSET;
            int card_y = card.getRelativeCoordinates()[1] + OFFSET;

            //if   the corner does not exist or is not occupied or is occupied by some previously placed card ==> sum this card's resources
            if (  !(    !(card_x==0 || card_y==80) && (placements[card_x-1][card_y+1] > card.getDeployedID())  )   ) {
                //get the symbol of corner 0 - TOP LEFT
                if (card.getCorners()[0].getSymbol().toInt()!=-1 && card.getCorners()[0].getSymbol().toInt()!=7) {
                    res_qty[card.getCorners()[0].getSymbol().toInt()]++;
                }
            }
            if (  !(    !(card_x==80 || card_y==80) && (placements[card_x+1][card_y+1] > card.getDeployedID())  )   ) {
                //get the symbol of corner 1 - TOP RIGHT
                if (card.getCorners()[1].getSymbol().toInt()!=-1 && card.getCorners()[1].getSymbol().toInt()!=7) {
                    res_qty[card.getCorners()[1].getSymbol().toInt()]++;
                }
            }
            if (  !(    !(card_x==0 || card_y==0) && (placements[card_x-1][card_y-1] > card.getDeployedID())  )   ) {
                //get the symbol of corner 2 - BOTTOM LEFT
                if (card.getCorners()[2].getSymbol().toInt()!=-1 && card.getCorners()[2].getSymbol().toInt()!=7) {
                    res_qty[card.getCorners()[2].getSymbol().toInt()]++;
                }
            }
            if (  !(    !(card_x==80 || card_y==0) && (placements[card_x+1][card_y-1] > card.getDeployedID())  )   ) {
                //get the symbol of corner 3 - BOTTOM RIGHT
                if (card.getCorners()[3].getSymbol().toInt()!=-1 && card.getCorners()[3].getSymbol().toInt()!=7) {
                    res_qty[card.getCorners()[3].getSymbol().toInt()]++;
                }
            }
        }
    }

    /**
     * A method that updates the player's hand by
     * replacing a deployed {@link PlayableCard} with a drawn {@link PlayableCard}.
     * The method eliminates both the placed and non-placed {@link CardSide}s
     * of the deployed {@link PlayableCard}, then adds the new one to the hand.
     *
     * @param  drawn_card    the {@link PlayableCard} drawn to be placed in the hand
     * @param  deployed_card  the {@link PlayableCard} in the hand to be replaced
     */
    public void updateHand(PlayableCard drawn_card, PlayableCard deployed_card) {
        for (int i=0; i<in_hand.length; i++) {
            if (in_hand[i].equals(deployed_card) ){
                in_hand[i] = drawn_card;
                break;
            }
        }
    }

    /**
     * A method that updates the player's score after a
     * {@link CardSide} has been deployed.
     *
     * @param  deployed_card    {@link CardSide} deployed
     */
    public void updateScore(CardSide deployed_card){
        if (deployed_card.getPointsRule()!=null) this.score += deployed_card.getPointsRule().computePoints(this);
    }

    /**
     * A method that updates the player's score if an objective
     * has been fulfilled.
     * This method is called only at the end of the game.
     *
     * @param objectiveCard      The {@link ObjectiveCard} containing the objective to fulfill.
     * @return                   The points awarded to the player
     */
    public int updateScoreObjectiveCard(ObjectiveCard objectiveCard){
        int points = objectiveCard.getRule().computePoints(this);
        this.score += points;
        return points;
    }
}


