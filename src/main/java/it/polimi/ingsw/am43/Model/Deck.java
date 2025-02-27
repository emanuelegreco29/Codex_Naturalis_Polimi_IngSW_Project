package it.polimi.ingsw.am43.Model;

import it.polimi.ingsw.am43.Model.Cards.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.am43.Model.Enum.CardType;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Points.*;

import java.io.*;
import java.util.*;

/**
 * A class that represents a deck of {@link Card}s.
 * It contains a stack with all the {@link Card}s of a certain type.
 */
public class Deck implements Serializable {

    @Serial
    private static final long serialVersionUID = -7895381186873933726L;

    /**
     * A stack of {@link Card} that composes the deck.
     */
    private final Stack<Card> cards = new Stack<>();

    /**
     * Constructor of the Deck class which, given the
     * deckType creates a deck of {@link Card}s of the
     * specified type.
     *
     * @param deckType  A string indicating the type of the deck
     */
    public Deck(String deckType) {
        switch (deckType) {
            case "Resource" -> {
                InputStream resStream = this.getClass().getResourceAsStream("/it/polimi/ingsw/am43/JSONs/ResourceCards.json");
                readResourceJSON(resStream);
            }
            case "Gold" -> {
                InputStream goldStream = this.getClass().getResourceAsStream("/it/polimi/ingsw/am43/JSONs/GoldCards.json");
                readGoldJSON(goldStream);
            }
            case "Objective" -> {
                InputStream objStream = this.getClass().getResourceAsStream("/it/polimi/ingsw/am43/JSONs/ObjectiveCards.json");
                readObjectiveJSON(objStream);
            }
            case "Starting" -> {
                InputStream startStream = this.getClass().getResourceAsStream("/it/polimi/ingsw/am43/JSONs/StartingCards.json");
                readStartingJSON(startStream);
            }
            default -> throw new IllegalStateException("Unexpected value: " + deckType);
        }
        // Decks get automatically shuffled as soon as they are created
        shuffle();
    }

    /**
     * A method that shuffles the {@link Card}s in the deck.
     */
    private void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * A method that returns the size of the deck.
     *
     * @return        Size of the deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * A method that retrieves the top {@link Card}
     * from the deck without removing it.
     *
     * @return         Top {@link Card} of the deck
     */
    public Card getTopCard() {
        if(cards.isEmpty())
            return null;
        return cards.peek();
    }

    /**
     * A method that converts a string, read
     * from the JSON file, to a list of {@link Symbol}.
     *
     * @param  input	String to be converted
     * @return         	List of {@link Symbol}s
     */
    private List<Symbol> stringToSymbol(String input){
        List<Symbol> list = new ArrayList<>();
        for(int i=0; i<input.length(); i++){
            list.add(Symbol.toSymbol(Character.getNumericValue(input.charAt(i))));
        }
        return list;
    }

    /**
     * A method that draws a {@link PlayableCard} from the deck.
     *
     * @return         	A {@link PlayableCard} drawn from the deck
     */
    public PlayableCard drawPlayableCard() {
        try {
            return (PlayableCard) cards.pop();
        } catch (EmptyStackException e){
            System.out.println("Empty deck");
            return null;
        }
    }

    /**
     * A method that draws an {@link ObjectiveCard} from the deck.
     *
     * @return         	An {@link ObjectiveCard} drawn from the deck
     */
    public ObjectiveCard drawObjectiveCard() {
        return (ObjectiveCard) cards.pop();
    }

    /**
     * A method that reads the ResourceCards JSON file
     * and constructs the {@link PlayableCard}s, one by one, to add them
     * to the deck eventually.
     *
     * @param  jsonFile  The JSON file to read
     */
    private void readResourceJSON(InputStream jsonFile) {

        // Initializes a search tree from the JSON file
        JsonNode rootNode = null;
        try{
            ObjectMapper objMapper = new ObjectMapper();
            rootNode = objMapper.readTree(jsonFile);
        } catch (IOException e) {
            System.err.println("Could not read JSON file: " + e.getMessage());
        }

        // Gets the data from the JSON file
        if(rootNode != null) {
            JsonNode root = rootNode.path("ResourceCards");
            for(JsonNode entry : root) {
                CardSide front, back;
                Corner[] front_corners = new Corner[4];
                Corner[] back_corners = new Corner[4];

                // Construct both front and back corners of the card
                front_corners[0] = new Corner(Symbol.toSymbol(entry.path("corner0").asInt()));
                front_corners[1] = new Corner(Symbol.toSymbol(entry.path("corner1").asInt()));
                front_corners[2] = new Corner(Symbol.toSymbol(entry.path("corner2").asInt()));
                front_corners[3] = new Corner(Symbol.toSymbol(entry.path("corner3").asInt()));
                for(int i=0; i<back_corners.length; i++) {
                    back_corners[i] = new Corner(Symbol.toSymbol(entry.path("back_corners").asInt()));
                }

                // Set kingdom of the card and create its points rule
                Kingdom kingdom = Kingdom.toKingdom(entry.path("perm_back").asInt());
                PointsRule rule = new simplePoints(entry.path("points").asInt());

                // Construct both CardSides of the card
                front = new ResourceFrontSide(front_corners, kingdom, rule, "Images/ResourceCards/Front/"+entry.path("front_png").asText()+".png");
                back = new BackSide(back_corners, kingdom, Collections.singletonList(Symbol.toSymbol(entry.path("perm_back").asInt())), "Images/ResourceCards/Back/"+entry.path("back_png").asText()+".png");

                // Create the card and add it to the deck
                cards.push(new PlayableCard(entry.path("id").asInt(), front, back, CardType.RESOURCE, kingdom ));
            }
        }
    }

    /**
     * A method that reads the GoldCards JSON file
     * and constructs the {@link PlayableCard}s, one by one, to add them
     * to the deck eventually.
     *
     * @param  jsonFile  The JSON file to read
     */
    private void readGoldJSON(InputStream jsonFile){

        // Initializes a search tree from the JSON file and gets the data from the JSON file
        JsonNode rootNode = null;
        try{
            rootNode = new ObjectMapper().readTree(jsonFile);
        } catch (IOException e) {
            System.err.println("Could not read JSON file: " + e.getMessage());
        }

        // Gets the data from the JSON file
        if(rootNode != null) {
            JsonNode root = rootNode.path("GoldCards");
            for (JsonNode entry : root) {
                CardSide front, back;
                PointsRule rule;
                Corner[] front_corners = new Corner[4];
                Corner[] back_corners = new Corner[4];

                // Construct both front and back corners of the card
                front_corners[0] = new Corner(Symbol.toSymbol(entry.path("corner0").asInt()));
                front_corners[1] = new Corner(Symbol.toSymbol(entry.path("corner1").asInt()));
                front_corners[2] = new Corner(Symbol.toSymbol(entry.path("corner2").asInt()));
                front_corners[3] = new Corner(Symbol.toSymbol(entry.path("corner3").asInt()));
                for(int i=0; i<back_corners.length; i++) {
                    back_corners[i] = new Corner(Symbol.toSymbol(entry.path("back_corners").asInt()));
                }

                // Set kingdom of the card
                Kingdom kingdom = Kingdom.toKingdom(entry.path("perm_back").asInt());

                // Create the points rule
                switch (entry.path("type").asText()) {
                    case("object") -> rule = new forEachResourceSet(entry.path("points").asInt(), Collections.singletonList(Symbol.toSymbol(entry.path("required_object").asInt())));
                    case("corners") -> rule = new forEachCoveredCorner(entry.path("points").asInt());
                    case("simple") -> rule = new simplePoints(entry.path("points").asInt());
                    default -> throw new IllegalStateException("Unexpected gold card rule type: " + entry.path("type").asText());
                }

                // Construct both CardSides of the card
                front = new GoldFrontSide(front_corners, kingdom, stringToSymbol(entry.path("required_resources").asText()), rule, "Images/GoldCards/Front/"+entry.path("front_png").asText()+".png");
                back = new BackSide(back_corners, kingdom, Collections.singletonList(Symbol.toSymbol(entry.path("perm_back").asInt())), "Images/GoldCards/Back/"+entry.path("back_png").asText()+".png");

                // Create the card and add it to the deck
                cards.push(new PlayableCard(entry.path("id").asInt(), front, back, CardType.GOLD, kingdom));
            }
        }
    }

    /**
     * A method that reads the ObjectiveCards JSON file
     * and constructs the {@link ObjectiveCard}s, one by one, to add them
     * to the deck eventually.
     *
     * @param  jsonFile  The JSON file to read
     */
    private void readObjectiveJSON(InputStream jsonFile){

        // Initializes a search tree from the JSON file
        JsonNode rootNode = null;
        try{
            rootNode = new ObjectMapper().readTree(jsonFile);
        } catch (IOException e) {
            System.err.println("Could not read JSON file: " + e.getMessage());
        }

        // Gets the data from the JSON file
        if(rootNode != null) {
            JsonNode root = rootNode.path("ObjectiveCards");
            for (JsonNode entry : root) {
                PointsRule rule;
                Kingdom[] resources = new Kingdom[2];

                // Populate array
                resources[0] = Kingdom.toKingdom(entry.path("kingdom").asInt());
                resources[1] = Kingdom.toKingdom(entry.path("l_res").asInt());

                // Create the points rule
                switch (entry.path("type").asText()) {
                    case("objects") -> rule = new forEachResourceSet(entry.path("points").asInt(), stringToSymbol(entry.path("req_res").asText()));
                    case("res") -> rule = new forEachResourceSet(entry.path("points").asInt(), Collections.singletonList(Symbol.toSymbol(entry.path("req_res").asInt())));
                    case("diag_TR") -> rule = new forEachDiagonalPattern(entry.path("points").asInt(), Kingdom.toKingdom(entry.path("kingdom").asInt()),false);
                    case("diag_TL") -> rule = new forEachDiagonalPattern(entry.path("points").asInt(), Kingdom.toKingdom(entry.path("kingdom").asInt()),true);
                    case("L_TR") -> rule = new forEachLPattern(entry.path("points").asInt(), 1, resources);
                    case("L_TL") -> rule = new forEachLPattern(entry.path("points").asInt(), 0, resources);
                    case("L_BR") -> rule = new forEachLPattern(entry.path("points").asInt(), 3, resources);
                    case("L_BL") -> rule = new forEachLPattern(entry.path("points").asInt(), 2, resources);
                    default -> throw new IllegalStateException("Unexpected value: " + entry.path("type").asText());
                }

                // Create the card and add it to the deck
                cards.push(new ObjectiveCard(entry.path("id").asInt(), "Images/ObjectiveCards/Front/"+entry.path("front_png").asText()+".png", Kingdom.toKingdom(entry.path("kingdom").asInt()), rule));
            }
        }
    }

    /**
     * A method that reads the StartingCards JSON file
     * and constructs the {@link PlayableCard}s, one by one, to add them
     * to the deck eventually.
     *
     * @param  jsonFile  The JSON file to read
     */
    private void readStartingJSON(InputStream jsonFile){

        // Initializes a search tree from the JSON file
        JsonNode rootNode = null;
        try{
            rootNode = new ObjectMapper().readTree(jsonFile);
        } catch (IOException e) {
            System.err.println("Could not read JSON file: " + e.getMessage());
        }

        // Gets the data from the JSON file
        if(rootNode != null) {
            JsonNode root = rootNode.path("StartingCards");
            for(JsonNode entry : root) {
                CardSide front, back;
                Corner[] front_corners = new Corner[4];
                Corner[] back_corners = new Corner[4];
                List<Symbol> perm_back = new ArrayList<>();

                // Construct both front and back corners of the card
                front_corners[0] = new Corner(Symbol.toSymbol(entry.path("corner0").asInt()));
                front_corners[1] = new Corner(Symbol.toSymbol(entry.path("corner1").asInt()));
                front_corners[2] = new Corner(Symbol.toSymbol(entry.path("corner2").asInt()));
                front_corners[3] = new Corner(Symbol.toSymbol(entry.path("corner3").asInt()));
                back_corners[0] = new Corner(Symbol.toSymbol(entry.path("back_corner0").asInt()));
                back_corners[1] = new Corner(Symbol.toSymbol(entry.path("back_corner1").asInt()));
                back_corners[2] = new Corner(Symbol.toSymbol(entry.path("back_corner2").asInt()));
                back_corners[3] = new Corner(Symbol.toSymbol(entry.path("back_corner3").asInt()));

                // Set the kingdom
                Kingdom kingdom = Kingdom.NONE;

                // Construct the perm_back
                if(entry.path("perm_back1").asInt() != -1) {
                    perm_back.add(Symbol.toSymbol(entry.path("perm_back1").asInt()));
                }
                if(entry.path("perm_back2").asInt() != -1) {
                    perm_back.add(Symbol.toSymbol(entry.path("perm_back2").asInt()));
                }
                if(entry.path("perm_back3").asInt() != -1) {
                    perm_back.add(Symbol.toSymbol(entry.path("perm_back3").asInt()));
                }

                // Construct both CardSides of the card
                front = new CardSide( front_corners, "Images/StartingCards/Front/"+entry.path("front_png").asText()+".png");
                back = new BackSide( back_corners, kingdom, perm_back, "Images/StartingCards/Back/"+entry.path("back_png").asText()+".png");

                // Create the card and add it to the deck
                cards.push(new PlayableCard(entry.path("id").asInt(), front, back, CardType.STARTING, kingdom));
            }
        }
    }
}