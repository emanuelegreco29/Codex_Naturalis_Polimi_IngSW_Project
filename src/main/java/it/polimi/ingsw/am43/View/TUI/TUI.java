package it.polimi.ingsw.am43.View.TUI;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Chat;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Player;
import it.polimi.ingsw.am43.Network.ClientInterface;
import it.polimi.ingsw.am43.Network.Messages.controllerMessages.*;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.*;
import it.polimi.ingsw.am43.Network.RMI.RMIClient;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;
import it.polimi.ingsw.am43.Network.Socket.SocketClient;
import it.polimi.ingsw.am43.View.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@SuppressWarnings("SpellCheckingInspection")
public class TUI implements View {

    /**
     * Interface used by the client to connect.
     */
    private final ClientInterface client;

    /**
     * Input stream.
     */
    private final BufferedReader in;

    /**
     * Color codes for printing different messages.
     */
    String YELLOW = "\033[0;33m";
    String RESET = "\033[0m";
    String RED = "\033[0;31m";

    /**
     * The cards that the player has in hand.
     */
    private PlayableCard[] inHand;

    /**
     * The starting card of the player.
     */
    private PlayableCard startingCard;

    /**
     * The objective cards common to all players.
     */
    private ObjectiveCard[] commonObjectives;

    /**
     * The secret objective of the player.
     */
    private ObjectiveCard personalObjective;

    /**
     * Username picked by this client.
     */
    private String usr;

    /**
     * Chat object of the player.
     */
    private final Chat chat;

    /**
     * The list of deployed cards.
     */
    private ArrayList<CardSide>[] deployed;

    /**
     * The placements matrix of the player.
     */
    private int[][][] placements;

    /**
     * The scores of all the players.
     */
    private int[] scores;

    /**
     * The ID of the player.
     */
    private int playerID;

    /**
     * Array of all players' usernames.
     */
    private String[] usernames;

    /**
     * Array of all players' colors.
     */
    private PawnColor[] usersColors;

    /**
     * Array with all the cards on the ground.
     */
    private PlayableCard[] onGround;

    /**
     * The backside of the card on top of the gold deck.
     */
    private CardSide goldenDeckTop;

    /**
     * The backside of the card on top of the resource deck.
     */
    private CardSide resourceDeckTop;

    /**
     * A boolean stating if the resource deck is empty.
     */
    private boolean resEmpty = false;

    /**
     * A boolean stating if the gold deck is empty.
     */
    private boolean goldEmpty = false;

    /**
     * The thread used to wait for user's input.
     */
    private Thread inputThread;

    /**
     * String used to store the input which would be lost
     * when the turn changes.
     */
    private String hangingInput;

    /**
     * Array of booleans indicating which cards can be placed.
     */
    private Boolean[] tmpPlaceableCards;

    //0 -> inHand choice
    //1 -> deployed choice
    //2 -> corner choice
    /**
     * The choices taken by the player during its turn.
     */
    private final int[] turnChoices = new int[3];

    /**
     * The number of players in the game.
     */
    private int numPlayers;

    /**
     * The queue containing all the incoming messages.
     */
    private static final BlockingDeque<Message> msgQueue = new LinkedBlockingDeque<>();

    /**
     * Constructor of the class.
     *
     * @param inputIP the IP of the server
     * @param inputPORT the port of the server
     * @param isSocket boolean indicating if the connection is a socket
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     * @throws NotBoundException if the RMI registry cannot be bound
     */
    public TUI(String inputIP, int inputPORT, boolean isSocket) throws IOException, ClassNotFoundException, NotBoundException {
        this.in = new BufferedReader(new InputStreamReader(System.in));
        chat = new Chat();
        buildInputThread();
        if (isSocket) {
            this.client = new SocketClient(inputIP, inputPORT);
        } else {
            this.client = new RMIClient(inputIP, inputPORT);
        }
        cls();
        printArt();
        new Thread(this::getTraffic).start();
        execute();
    }

    /**
     * A method that is run by a separate {@link Thread}, it takes
     * all the incoming messages and puts them in a queue. If the
     * incoming message is a chat message, immediately parse it
     * without adding it to the queue, to have a real-time chat.
     */
    @Override
    public void getTraffic(){
        while(true){
            Message msg = client.readNewMessage();
            if(msg != null) {
                if(msg instanceof publicChatMessageMsg){
                    addPublicMessage((publicChatMessageMsg) msg);
                } else if (msg instanceof privateChatMessageMsg){
                    addPrivateMessage((privateChatMessageMsg) msg);
                } else {
                    try {
                        if(msgQueue.remainingCapacity() == 0) {
                            msgQueue.pollFirst();
                        }
                        msgQueue.add(msg);
                    } catch (Exception e){
                        System.out.println(RED + "[ERROR] Could not process Socket message!\nMaybe someone disconnected!" + RESET);
                        this.inputThread.interrupt();
                        Scanner scanner = new Scanner(System.in);
                        scanner.nextLine();
                        scanner.close();
                        System.exit(0);
                    }
                }
            }
        }
    }

    /**
     * A method that runs periodically on the main {@link Thread},
     * it takes the messages in the queue and processes them.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void execute() throws IOException {
        while (true){
            try {
                viewHandler(msgQueue.take());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * A method that processes the incoming messages.
     * It analyzes the type of the message and calls the appropriate
     * method.
     *
     * @param message the incoming message
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void viewHandler(Message message) throws IOException {
        switch (message.getType()){
            case MessageType.HEARTBEAT -> {}
            case MessageType.FIRSTPLAYER -> join(message, true);
            case MessageType.GENERALPLAYER -> join(message, false);
            case MessageType.INITIAL_SITUATION -> updateLocalInitialSituation((initialSituation) message);
            case MessageType.PERSONALOBJECTIVES -> choosePersonalObjectives((personalObjectivesMsg) message);
            case MessageType.INITIALGAMEINFO -> updateInitialGameInfo((initialGameInfoMsg) message);
            case MessageType.GAMEINFO -> displayGameInfo((gameInfoMsg) message);
            case MessageType.PLEACABLECARDS -> displayPlaceableInHand((placeableCardsMsg) message);
            case MessageType.AVAILABLEPLACEMENTS -> displayAvailablePlacements((availablePlacementsMsg) message);
            case MessageType.NEWINHAND -> updateLocalHand((newInHandMsg) message);
            case MessageType.CHECK_FIRST -> firstStillJoining((checkFirstMsg) message);
            case MessageType.NICKNAME_ALREADY_USED -> nickOrColorUsed((nickOrColorAlreadyUsedMsg) message);
            case MessageType.REJOIN_PLAYER -> rejoin((rejoinPlayerMsg) message);
            case MessageType.ENDGAME -> endGame((endGameMsg) message);
            case MessageType.CANNOT_REJOIN -> alreadyPicked((cannotRejoinMsg) message);
            case MessageType.REQUIRED_DATA -> setRequiredData((requiredDataMsg) message);
            default -> throw new IllegalStateException("Unexpected value: " + message);
        }
    }

    /**
     * A method that prints out an ASCII art on login.
     */
    private void printArt(){
        System.out.println(YELLOW +
                "     ,gggg,                                                                                   \n" + YELLOW +
                "   ,88\"\"\"Y8b,                   8I                                                            \n" + YELLOW +
                "  d8\"     `Y8                   8I                                                            \n" + YELLOW +
                " d8'   8b  d8                   8I                                                            \n" + YELLOW +
                ",8I    \"Y88P'                   8I                                                            \n" + YELLOW +
                "I8'            ,ggggg,    ,gggg,8I   ,ggg,      ,gg,   ,gg                                    \n" + YELLOW +
                "d8            dP\"  \"Y8gggdP\"  \"Y8I  i8\" \"8i    d8\"\"8b,dP\"                                     \n" + YELLOW +
                "Y8,          i8'    ,8I i8'    ,8I  I8, ,8I   dP   ,88\"                                       \n" + YELLOW +
                "`Yba,,_____,,d8,   ,d8',d8,   ,d8b, `YbadP' ,dP  ,dP\"Y8,                                      \n" + YELLOW +
                "  `\"Y8888888P\"Y8888P\"  P\"Y8888P\"`Y8888P\"Y8888\"  dP\"   \"Y8                                     \n" + YELLOW +
                "                                                                                              \n" +
                "                                                                                              \n" +
                " ,ggg, ,ggggggg,                                                                              \n" +
                "dP\"\"Y8,8P\"\"\"\"\"Y8b               I8                                      ,dPYb,                \n" + YELLOW +
                "Yb, `8dP'     `88               I8                                      IP'`Yb                \n" + YELLOW +
                " `\"  88'       88            88888888                                   I8  8I  gg            \n" + YELLOW +
                "     88        88               I8                                      I8  8'  \"\"            \n" + YELLOW +
                "     88        88    ,gggg,gg   I8   gg      gg   ,gggggg,    ,gggg,gg  I8 dP   gg     ,g,    \n" + YELLOW +
                "     88        88   dP\"  \"Y8I   I8   I8      8I   dP\"\"\"\"8I   dP\"  \"Y8I  I8dP    88    ,8'8,   \n" + YELLOW +
                "     88        88  i8'    ,8I  ,I8,  I8,    ,8I  ,8'    8I  i8'    ,8I  I8P     88   ,8'  Yb  \n" + YELLOW +
                "     88        Y8,,d8,   ,d8b,,d88b,,d8b,  ,d8b,,dP     Y8,,d8,   ,d8b,,d8b,_ _,88,_,8'_   8) \n" + YELLOW +
                "     88        `Y8P\"Y8888P\"`Y88P\"\"Y88P'\"Y88P\"`Y88P      `Y8P\"Y8888P\"`Y88P'\"Y888P\"\"Y8P' \"YY8P8P\n\n\n" + RESET );

    }

    /**
     * A method called when a player is joining a newly created
     * game. It runs differently for the first player and for
     * following players.
     *
     * @param message the message containing the username and color picked by the first player
     * @param first if the player joining is the first
     * @throws IOException if an I/O error occurs
     */
    private void join(Message message, boolean first) throws IOException {
        // If the player joining is the first one
        // asks for a username, for a PawnColor
        // and how many players should be in the game
        if (first){
            // Username choice phase
            System.out.println("Please insert your username:");
            String username = in.readLine();
            while(username.isEmpty() || username.contains(" ") || username.contains("\n") || username.contains("\r")){
                System.out.println("Invalid input, please insert a string:");
                username = in.readLine();
            }

            // PawnColor choice phase
            PawnColor[] colours = PawnColor.values();
            for (int i=0; i<colours.length-1; i++){
                System.out.println(i+ ") " + colours[i].toTUIColor() + colours[i].toString() + RESET);
            }
            System.out.println("Please insert the number of the color you want to choose:");
            String color = in.readLine();
            while (!color.matches("[0-3]")){
                System.out.println("Invalid input, please insert a number between 0 and 3:");
                color = in.readLine();
            }

            // Number of players choice phase
            System.out.println("Please insert the number of players for this match [2-4]:");
            String players = in.readLine();
            while (!players.matches("[2-4]")){
                System.out.println("Invalid input, please insert a number between 2 and 4:");
                players = in.readLine();
            }
            usr = username;
            client.writeNewMessage(new SCMsgFirstPlayerJoins(username, colours[Integer.parseInt(color)].toInt(), Integer.parseInt(players)));
            System.out.println("Game created!\nWaiting for other players to join...");

        // If the player joining is not the first one
        // he/she either has to wait for the first
        // to finish the game setup, or just pick
        // and username and a PawnColor
        } else {
            generalPlayerMsg msg = (generalPlayerMsg) message;
            int[] remaining = msg.getRemaining();
            String[] invalid_nicknames = msg.getNicknames();

            if(msg.getNicknames()==null /*&& !quit*/){
                System.out.println("First player is setting up the game. Please wait...");
                try {
                    Thread.sleep(5000);
                    client.writeNewMessage(new SCMsgFirstStillJoining());
                    return;
                } catch (InterruptedException e) {
                    System.err.println(RED + "Error: " + e.getMessage() + RESET);
                }
            }

            // Username choice phase
            System.out.println("Please insert your username:");
            String username = in.readLine();
            while(username.isEmpty() || username.contains(" ") || username.contains("\n") || username.contains("\r") ||
                    Arrays.asList(invalid_nicknames).contains(username)){
                if(Arrays.asList(invalid_nicknames).contains(username)){
                    System.out.println("Username already taken! Please insert another one:");
                }else{
                    System.out.println("Invalid input, please insert a valid string:");
                }
                username = in.readLine();
            }

            // PawnColor choice phase
            // Only print available colors to the player
            for (int i=0; i<remaining.length; i++){
                System.out.println(i+ ") " + toColor(remaining[i]).toTUIColor() + toColor(remaining[i]).toString() + RESET);
            }
            System.out.println("Please insert the number of the color you want to choose:");
            String color = in.readLine();
            while (!color.matches("[0-" + (remaining.length-1) + "]")){
                    System.out.println("Invalid input, please insert a number between 0 and " + (remaining.length-1) + ":");
                    color = in.readLine();
            }
            usr = username;
            client.writeNewMessage(new SCMsgGeneralPlayerJoins(username, remaining[Integer.parseInt(color)]));
        }
    }

    /**
     * A method that checks periodically if the first
     * player is still setting up the game.
     *
     * @param message the message containing the usernames and colors
     */
    @Override
    public void firstStillJoining(checkFirstMsg message){
        if(message.getNicknames()!=null && message.getNicknames().length>0){
            cls();
            System.out.println("Setup finished!");
            try {
                join(new generalPlayerMsg(message.getRemaining(), message.getNicknames()), false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("First player is setting up the game. Please wait...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            client.writeNewMessage(new SCMsgFirstStillJoining());
        }
    }

    /**
     * A method run when a player has inserted a duplicate username or
     * color chosen from another player.
     *
     * @param message the message containing the usernames and colors used
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void nickOrColorUsed(nickOrColorAlreadyUsedMsg message) throws IOException{
        cls();
        int[] remaining = message.getColors();
        String[] invalid_nicknames = message.getNicknames();
        String username;

        System.out.println("The nickname you inserted or the color you chose is already used!\nYou must login again!");
        System.out.println("Please insert a valid username:");
        username = in.readLine();
        while(username.isEmpty() || username.contains(" ") || username.contains("\n") || username.contains("\r") ||
                Arrays.asList(invalid_nicknames).contains(username)){
            if(Arrays.asList(invalid_nicknames).contains(username)){
                System.out.println("Username already taken! Please insert another one:");
            }else{
                System.out.println("Invalid input, please insert a valid string:");
            }
            username = in.readLine();
        }
        for (int i=0; i<remaining.length; i++){
            System.out.println(i+ ") " + toColor(remaining[i]).toTUIColor() + toColor(remaining[i]).toString() + RESET);
        }
        System.out.println("Please insert the number of the color you want to choose:");
        String color = in.readLine();
        while (!color.matches("[0-" + (remaining.length-1) + "]")){
            System.out.println("Invalid input, please insert a number between 0 and " + (remaining.length-1) + ":");
            color = in.readLine();
        }
        usr = username;
        client.writeNewMessage(new SCMsgGeneralPlayerJoins(username, remaining[Integer.parseInt(color)]));
        System.out.println("Game joined!\nWaiting for other players to join...");
    }

    /**
     * A method run when a player is rejoining a loaded game.
     * It prints the usernames chosen for this game and asks
     * the player for their choice.
     *
     * @param msg the message containing the usernames
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void rejoin(rejoinPlayerMsg msg) throws IOException {
        System.out.println("Welcome back! Please pick the username you chose in the last game:");
        for(int i=0; i<msg.getPlayers().length; i++){
            System.out.println(i + ") " + msg.getPlayers()[i]);
        }
        String pick = in.readLine();
        while(!pick.matches("[0-" + (msg.getPlayers().length-1) + "]")){
            System.out.println("Invalid input, please insert a number between 0 and " + (msg.getPlayers().length-1) + ":");
            pick = in.readLine();
        }

        usr = msg.getPlayers()[Integer.parseInt(pick)];
        this.playerID = Integer.parseInt(pick);
        client.writeNewMessage(new SCMsgPlayerRejoins(pick));
    }

    /**
     * A method run if the player, when rejoining, picks a
     * username already picked in the meantime by someone else.
     *
     * @param msg the message containing the usernames
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void alreadyPicked(cannotRejoinMsg msg) throws IOException {
        System.out.println("Someone already picked this username!");
        System.out.println("Please pick another username:");
        for(int i=0; i<msg.getPlayers().length; i++){
            if(!msg.getInvalid().contains(i)){
                System.out.println(i + ") " + msg.getPlayers()[i]);
            }
        }
        String pick = in.readLine();
        while(!pick.matches("[0-" + (msg.getPlayers().length-1) + "]")){
            System.out.println("Invalid input, please insert a number between 0 and " + (msg.getPlayers().length-1) + ":");
            pick = in.readLine();
        }
        usr = msg.getPlayers()[Integer.parseInt(pick)];
        this.playerID = Integer.parseInt(pick);
        client.writeNewMessage(new SCMsgPlayerRejoins(pick));
    }

    /**
     * A method that restores all the helpful information
     * when a game is loaded and restarted. All these information
     * where present when the player got kicked or when the
     * match crashed, thus are mandatory to proceed the match.
     *
     * @param msg the message containing all necessary data
     */
    @Override
    public void setRequiredData(requiredDataMsg msg) {
        this.inHand = msg.getInHand();
        this.startingCard = msg.getStartingCard();
        this.onGround = msg.getOnGround();
        this.commonObjectives = msg.getCommonObjectives();
        this.personalObjective = msg.getPersonalObjective();
        this.scores = msg.getScores();
        this.placements = msg.getPlacements();
        this.deployed = msg.getDeployed();
        this.usersColors = msg.getColors();
        this.usernames = msg.getUsernames();
        this.numPlayers = msg.getNumPlayers();
        this.hangingInput = "rejoined";
    }

    /**
     * A method that saved the initial data structure, consisting of
     * the starting card, hands and common objectives.
     * Lastly, it asks the server for the potential objective cards
     * to choose from for the secret objective.
     *
     * @param msg the message containing all necessary data
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void updateLocalInitialSituation(initialSituation msg) throws IOException {
        this.startingCard = msg.getStartingCard();
        this.inHand = msg.getInHand();
        this.commonObjectives = msg.getCommonObjectives();
        this.playerID = msg.getPlayerID();
        displayInitialSituation();
    }

    /**
     * A method that prints out the initial game situation
     * to the player.
     *
     * @throws IOException if an I/O error occurs
     */
    private void displayInitialSituation() throws IOException {
        System.out.println("Common objectives:");
        CardPrinter.printObjectiveCardSideSameRow(commonObjectives[0], commonObjectives[1]);
        System.out.println("In hand:");
        for (int i=0; i<inHand.length; i++){
            PlayableCard c = inHand[i];
            System.out.println("Card number: " + i);
            CardPrinter.printPlayableCardSideSameRow(c.getFrontside(), false, c.getBackside(), false);
        }
        chooseStartingCardSide();
    }

    /**
     * A method run when the player has to choose its secret
     * objective. It shows the two personal objective cards
     * drawn, asks which one the player wants to choose,
     * send the message to the server and, lastly, it calls
     * the next method.
     *
     * @param message the message containing the personal objective cards
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void choosePersonalObjectives(personalObjectivesMsg message) throws IOException {
        ObjectiveCard[] personalObjectives = message.getPersonalObjectives();
        System.out.println("Objective cards:");
        System.out.println(CardPrinter.cardHalfHorizontalSpaceLength+ "0)"+ CardPrinter.cardHorizontalSpaceLength+ "1)");
        CardPrinter.printObjectiveCardSideSameRow(personalObjectives[0], personalObjectives[1]);
        System.out.println("Insert the number of the objective you want to choose as secret objective:");
        String choice = in.readLine();
        while (!choice.matches("[0-1]")){
            System.out.println("Invalid input, please insert 0 or 1:");
            choice = in.readLine();
        }
        this.personalObjective = personalObjectives[Integer.parseInt(choice)];
        client.writeNewMessage(new SCMsgPersonalObjective(personalObjective));
        System.out.println("Awesome, now just wait for other players to pick their cards!");
    }

    /**
     * A method run when the player has to pick on which side
     * to place the starting card.
     *
     * @throws IOException if an I/O error occurs
     */
    private void chooseStartingCardSide() throws IOException {
        System.out.println("Starting card:");
        CardPrinter.printPlayableCardSideSameRow(startingCard.getFrontside(), false, startingCard.getBackside(), false);

        //ask for input
        System.out.println("Choose a side to place: 0 for front, 1 for back");
        String side = in.readLine();
        while (!side.matches("[0-1]")){
            System.out.println("Invalid input, please insert 0 or 1:");
            side = in.readLine();
        }
        client.writeNewMessage(new SCMsgDrawPersonalObjectives(Integer.parseInt(side)));
    }

    /**
     * A method that updates the local client with all the
     * initial game information. Only used when a new  game is started.
     *
     * @param msg the message containing the initial game info
     */
    @Override
    public void updateInitialGameInfo(initialGameInfoMsg msg) {
        CardSide[] starting = msg.getStartingSides();
        int first = msg.getFirst();
        this.usernames = msg.getUsernames();
        this.usersColors = msg.getColors();
        this.numPlayers = starting.length;
        this.deployed = new ArrayList[numPlayers];

        this.onGround = msg.getOnGround();
        this.goldenDeckTop = msg.getGoldenDeckTop();
        this.resourceDeckTop = msg.getResourceDeckTop();

        this.placements = new int[numPlayers][81][81];

        for (int i=0; i<numPlayers; i++){
            this.deployed[i] = new ArrayList<>();
            updatePlayerSituation(i,starting[i]);
        }
        cls();
        System.out.println("Welcome " + usersColors[playerID].toTUIColor()+usr + RESET + "! You are player n. " + (playerID+1));

        if (first == this.playerID){
            System.out.println("Now it is your turn!");
            client.writeNewMessage(new SCMsgPlay());
        } else {
            System.out.println("Now it is " + printUsername(first)+ "'s turn.\nPlease wait for your turn! (type /help for a helpful list of commands)");
            buildInputThread();
            this.inputThread.start();
        }
    }

    /**
     * A method that updates the local client with all the
     * game information, after each turn. It is used both
     * after a turn or when a game is reloaded (it prints
     * the last turn played to all players).
     * The information shown are:
     * - int of the player who should play next
     * - array of the scores
     * - the last on ground card
     * - the index of the last on ground card
     * - placed of the last player who played
     *
     * @param message the message containing all useful information
     */
    @Override
    public void displayGameInfo(gameInfoMsg message) {
        int index = message.getLastOnGroundIndex();
        this.scores = message.getScores();
        this.goldenDeckTop = message.getGoldenDeckTop();
        this.resourceDeckTop = message.getResourceDeckTop();
        if (index!=-1) {
            this.onGround[message.getLastOnGroundIndex()] = message.getLastOnGround();
        }
        // Stop the thread waiting for potential user's commands to execute while it was not its turn
        if (this.inputThread != null && hangingInput != null && !hangingInput.equals("rejoined")) {
            this.inputThread.interrupt();
            hangingInput = null;
        } else {
            this.inputThread.interrupt();
            if(hangingInput != null && hangingInput.equals("rejoined")){
                hangingInput = "";
            } else {
                hangingInput = null;
            }
        }

        int lastPlayer = message.getPlayerID();

        CardSide placedCardSide = message.getPlacedCardSide();
        updatePlayerSituation(lastPlayer, placedCardSide);

        if(lastPlayer == this.playerID){
            cls();
            System.out.println("You placed: ");
            CardPrinter.printPlayableCardSide(placedCardSide, false);
            System.out.println("And your current deployed situation is: ");
        }
        else {
            System.out.println("Player " + printUsername(lastPlayer) + " placed: ");
            CardPrinter.printPlayableCardSide(placedCardSide, false);
            System.out.println("And its current deployed situation is: ");
        }
        showDeployed(lastPlayer);
        System.out.println("General situation: ");
        showScoreboard();

        if(index!=-1) {
            System.out.println("On ground: ");
            showOnGround();

            if ((lastPlayer + 1) % numPlayers == this.playerID) {
                System.out.println("Now it is your turn!");
                client.writeNewMessage(new SCMsgPlay());
            } else {
                System.out.println("Now it is " + printUsername((lastPlayer + 1) % numPlayers) + "'s turn. You can type /help for a helpful list of commands!");
                buildInputThread();
                this.inputThread.start();
            }
        }
    }

    /**
     * A method that updates the local situation of a player
     * when he/she places a card. It also updates the placements
     * of that player.
     *
     * @param playerID the ID of the player who placed the card
     * @param placedCardSide the card that was placed
     */
    @Override
    public void updatePlayerSituation(int playerID, CardSide placedCardSide){
        this.deployed[playerID].add(placedCardSide);
        int x = placedCardSide.getRelativeCoordinates()[0] + Player.OFFSET;
        int y = placedCardSide.getRelativeCoordinates()[1] + Player.OFFSET;
        this.placements[playerID][x][y] = placedCardSide.getDeployedID();
    }

    /**
     * A method that prints the username of player at index "i"
     *
     * @param i index of the player
     * @return the player's username
     */
    private String printUsername(int i){
        return usersColors[i].toTUIColor() + usernames[i] + RESET;
    }

    /**
     * A method that prints out the placeable cards in player's hand and asks
     * the player which one it wants to place. After that, it asks
     * for the coordinates of the deployed card on which it wants to place it.
     * The information is later sent to the server.
     *
     * @param message the message containing the placeable cards in player's hand
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void displayPlaceableInHand(placeableCardsMsg message) throws IOException {
        System.out.println("Your current situation is: ");
        showDeployed(playerID);
        Boolean[] placeableCards = message.getPlaceableCards();
        System.out.println("Choose a card to place: ");
        System.out.println("    FRONT SIDE" + CardPrinter.cardHorizontalSpaceLength + "BACK SIDE");
        for (int i=0; i<2*(inHand.length); i++){
            PlayableCard c = inHand[i/2];
            boolean bw1 = !placeableCards[i];
            boolean bw2 = !placeableCards[i+1];
            String label= "";
            if(placeableCards[i]) {
                label+=(CardPrinter.cardHalfHorizontalSpaceLength + i + ")");
            } else{
                label+=(CardPrinter.cardHalfHorizontalSpaceLength + "  ");
            }
            i++;

            if(placeableCards[i]){
                label+= CardPrinter.cardHorizontalSpaceLength + (i + ")");
            }
            System.out.println(label);
            CardPrinter.printPlayableCardSideSameRow(c.getFrontside(), bw1, c.getBackside(), bw2);

        }
        System.out.println("Insert the number of the card you want to place:");
        String choice = handleInput();
        while (!choice.matches("[0-5]") || !placeableCards[Integer.parseInt(choice)]){
            System.out.println("Invalid input, please insert one of the displayed numbers corresponding to one of the colored cards:");
            choice = handleInput();
        }

        System.out.println("Select a deployed card in order to place the previously selected card next to it:");
        showDeployed(playerID);

        //ask for a number between 0 and the number of deployed cards
        String deployedChoice = handleInput();
        while (!deployedChoice.matches("[0-9]+") || (deployedChoice.matches("[0-9]+") && !(Integer.parseInt(deployedChoice) < deployed[playerID].size() && Integer.parseInt(deployedChoice) >= 0))){
            System.out.println("Invalid input, please insert a number between 0 and " + (deployed[playerID].size()-1) + ":");
            deployedChoice = handleInput();
        }

        //save the current situation for the future playTurn or to choose a new card
        this.tmpPlaceableCards = placeableCards;
        this.turnChoices[0] = Integer.parseInt(choice);
        this.turnChoices[1] = Integer.parseInt(deployedChoice);

        //send message
        client.writeNewMessage(new SCMsgCardCouple(Integer.parseInt(deployedChoice)));
    }

    /**
     * A method that prints out the available corner placements to the player,
     * who then has to decide one. After insertion, the information is forwarded to the server.
     * If the player inserts "-1", he's sent back to choosing a card to place.
     *
     * @param message the message containing the corners on which placement is possible
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void displayAvailablePlacements(availablePlacementsMsg message) throws IOException {
        boolean[] availablePlacements = message.getAvailablePlacements();
        this.resEmpty = message.isResEmpty();
        this.goldEmpty = message.isGoldEmpty();
        System.out.println("Choose one of this available placement options: ");
        for (int i=0; i<availablePlacements.length; i++){
            if(availablePlacements[i]){
                switch (i){
                    case 0:
                        System.out.println(i+") TL");
                        break;
                    case 1:
                        System.out.println(i+") TR");
                        break;
                    case 2:
                        System.out.println(i+") BL");
                        break;
                    case 3:
                        System.out.println(i+") BR");
                        break;
                }
            }
        }
        System.out.println("Insert the number of the corner you want to choose: (insert -1 to repeat the choice of a card to place)");
        String choice = handleInput();
        while (!(choice.matches("[0-3]") && availablePlacements[Integer.parseInt(choice)]) && !(choice.matches("-1"))){
            System.out.println("Invalid input, please insert one of the displayed numbers");
            choice = handleInput();
        }
        if (Integer.parseInt(choice) == -1){
            //choose a new card
            turnChoices[0] = -1;
            turnChoices[1] = -1;
            displayPlaceableInHand(new placeableCardsMsg(tmpPlaceableCards));
        } else {
            this.turnChoices[2] = Integer.parseInt(choice);
            displayOnGround();
        }
    }

    /**
     * A method that is called when the player has to choose the card
     * to pick from the ground or, alternatively, draw from a deck (if not empty).
     * Asks the player what to draw and forwards the information to server.
     *
     * @throws IOException if an I/O error occurs
     */
    private void displayOnGround() throws IOException {
        List<Integer> pickable = new ArrayList<>();
        for(int i=0; i<onGround.length; i++){
            if(onGround[i] == null) continue;
            pickable.add(i);
            System.out.println("Card number: " + CardPrinter.cardHalfHorizontalSpaceLength+ i);
            CardPrinter.printPlayableCardSideSameRow(onGround[i].getFrontside(), false, onGround[i].getBackside(), false);
        }
        if(goldenDeckTop != null && resourceDeckTop != null){
            System.out.println("4) Resource deck"+ CardPrinter.cardHalfHorizontalSpaceLength + "5) Gold deck");
            CardPrinter.printPlayableCardSideSameRow(resourceDeckTop, false, goldenDeckTop, false);
        } else if (goldenDeckTop != null){
            System.out.println("5) Gold deck");
            CardPrinter.printPlayableCardSide(goldenDeckTop, false);
        } else if (resourceDeckTop != null){
            System.out.println("4) Resource deck");
            CardPrinter.printPlayableCardSide(resourceDeckTop, false);
        }

        System.out.println("Insert the number of the card you want to draw.");
        String choice = handleInput();
        while (!choice.matches("[0-5]") || (choice.matches("[0-3]") && !pickable.contains(Integer.parseInt(choice))) ||
                (choice.matches("4") && resEmpty) || (choice.matches("5") && goldEmpty)){
            if (choice.matches("4") && resEmpty){
                System.out.println("Resource deck is empty, please insert a valid card to pick!");
            } else if (choice.matches("5") && goldEmpty){
                System.out.println("Gold deck is empty, please insert a valid card to pick!");
            } else {
                System.out.println("Invalid input, please insert a valid card to pick!");
            }
            choice = handleInput();
        }
        client.writeNewMessage(new SCMsgPlayTurn(turnChoices[0], turnChoices[1], turnChoices[2], Integer.parseInt(choice)));
    }

    /**
     * A method called when the game comes to an end. It prints out
     * the winner and the scoreboard.
     *
     * @param message the message containing the needed information
     */
    public void endGame(endGameMsg message){
        int[][] finalScores = message.getRanking();
        System.out.println("The game is over! The final scores are: ");
        for (int i=0; i<finalScores.length; i++){
            System.out.println(i+") " + printUsername(finalScores[i][0]) + ":  completed " + finalScores[i][2] + " objectives and collected a total of " + finalScores[i][1] + " points");
        }
        //get the first string of the set
        System.out.println("The winner is: " + printUsername(finalScores[0][0]));
        System.out.println("Press enter to exit.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
        System.exit(0);
    }

    /**
     * A method that updates the locally saved hand, based on the placed
     * {@link CardSide} and the drawn {@link PlayableCard}.
     *
     * @param message the message containing the needed information
     */
    @Override
    public void updateLocalHand(newInHandMsg message){
        PlayableCard drawn = message.getDrawn();
        int played = turnChoices[0]/2; //get the index of the Card from the index of the CardSide
        inHand[played] = drawn;
    }

    /**
     * A method that executes the terminal command "cls" to
     * clear the terminal window.
     */
    private static void cls(){
        try{
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error executing cls command: " + e.getMessage());
        }
    }

    /**
     * A method that, given an index, returns the corresponding
     * {@link PawnColor}.
     *
     * @param color the index of the color
     * @return the {@link PawnColor} at index "color"
     */
    private PawnColor toColor(int color){
        return PawnColor.values()[color];
    }

    /**
     * Captures commands from the input stream (if there are any)
     * and passes them to the commandInput method for processing.
     * This method is run by a separate {@link Thread} while a player
     * is waiting for its turn, so that the client can still continously
     * read incoming messages and handle them, as well as executing user inserted
     * commands.
     *
     * @throws IOException if an I/O error occurs while reading from the input stream.
     */
    private void captureCommands() throws IOException {
        String input = "";
        if(in.ready()){
            input = in.readLine();
        }
        if(!Thread.currentThread().isInterrupted()) {
            commandInput(input);
        } else {
            if(input.startsWith("/")) {
                commandInput(input);
                hangingInput = "";
            }else{
                hangingInput = input;
            }
        }
    }

    /**
     * Handles the user's input DURING its turn: if the input inserted is a command,
     * the method executes the command and then asks either for another
     * one or for the original value that the player was supposed to
     * insert to play the turn. Otherwise, the method simply returns
     * the inserted string so that the method calling handleInput can
     * process the input on its own.
     *
     * @return         	the user input if no command is to run.
     * @throws IOException	if an I/O error occurs while reading from the input stream.
     */
    private String handleInput() throws IOException {
        String input;
        input = getHangingInput();
        if(inputThread.isInterrupted()) {
            while (input == null) {
                input = getHangingInput();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            input = "";
        }
        if (input.isEmpty()) {
            input = in.readLine();
        }
        if (input.startsWith("/")) {
            commandInput(input);
            System.out.println("Insert a new command or the previously required input");
            return handleInput();
        }
        return input;
    }

    /**
     * Handles the user's input DURING the waiting phase between its
     * turn and the turn(s) of other player(s): if the input inserted is a command,
     * the method executes the command. This method is run by the
     * captureCommands() method inside a dedicated {@link Thread}.
     */
    private void commandInput(String input){

        if (inputThread.isInterrupted() && !input.startsWith("/")) {
            hangingInput = input;
            return;
        }

        if (input.equals("/exit")){
            System.exit(0);
        }
        else if (input.equals("/help")){
            System.out.println("""
                    Commands:\s
                    /exit -> exit the game
                    /help -> show this message
                    /cls -> clear the screen
                    /chat -> see the chat
                    /send xxx -> send a message (xxx) to the chat
                    /send -p XXX xxx -> send a private message (xxx) to the player XXX (insert an username)
                    /legend -> show the legend
                    /scoreboard -> show the scoreboard
                    /deployed -> show your deployed cards
                    /deployed -p XXX -> show the deployed cards of the player XXX (insert an username)
                    /onGround -> show the cards on the ground
                    /hand -> show the cards in hand
                    /commonObjectives -> show the common objectives
                    /personalObjective -> show the personal objective
                    """
            );
        }
        else if (input.equals("/cls")){
            cls();
        }
        else if (input.equals("/chat")){
            this.chat.viewChat();
        }
        else if(input.startsWith("/send")){
            String[] parts = input.split("-p");
            // Public message
            if(parts.length==1 && parts[0].length()>6) {
                String msg = parts[0].substring(6);
                this.chat.addMessage(printUsername(Arrays.asList(usernames).indexOf(usr)), "all", msg);
                client.writeNewMessage(new publicChatMessageMsg(usr, msg));
                System.out.println("Message sent!");
                // Private message
            } else if (parts.length>1 && !parts[1].isEmpty()) {
                String[] division = parts[1].split(" ", 3);
                String receiver = division[1];
                String msg = division[2];
                if(Arrays.asList(usernames).contains(receiver) && !receiver.equals(usr) && !receiver.isEmpty()) {
                    this.chat.addPrivateMessage(printUsername(Arrays.asList(usernames).indexOf(usr)), printUsername(Arrays.asList(usernames).indexOf(receiver)), msg);
                    client.writeNewMessage(new privateChatMessageMsg(usr, receiver, msg));
                    System.out.println("Message sent!");
                } else {
                    if(receiver.equals(usr)) {
                        System.out.println("You can't send a private message to yourself!");
                    } else if(receiver.isEmpty()) {
                        System.out.println("The username inserted is empty, please retry!");
                    }
                    else {
                        System.out.println("The username inserted does not exist, please retry!");
                    }
                }
            }
        }
        else if (input.equals("/legend")){
            printLegend();
        }
        else if (input.equals("/scoreboard")){
            if(scores!=null){
                showScoreboard();
            } else {
                System.out.println("You can't use this command now");
            }
        }
        else if (input.startsWith("/deployed")){
            if (deployed!=null) {
                //parse the input
                String[] parts = input.split("-p ");
                if (parts.length > 2) {
                    System.out.println("Invalid input, please insert a valid string");
                } else if (parts.length == 1) {
                    showDeployed(playerID);
                } else {
                    String player = parts[1];
                    if (!Arrays.asList(usernames).contains(player)) {
                        System.out.println("The username inserted does not exist, please retry!");
                    } else {
                        int playerID = Arrays.asList(usernames).indexOf(player);
                        showDeployed(playerID);
                    }
                }
            }else{
                System.out.println("You can't use this command now!");
            }

        }
        else if (input.equals("/onGround")){
            showOnGround();
        }
        else if (input.equals("/hand")){
            if(inHand!= null) {
                for (int i = 0; i < inHand.length; i++) {
                    PlayableCard c = inHand[i];
                    System.out.println("Card number: " + i);
                    CardPrinter.printPlayableCardSideSameRow(c.getFrontside(), false, c.getBackside(), false);
                }
            } else {
                System.out.println("You can't use this command now!");
            }
        }
        else if (input.equals("/commonObjectives")){
            if(commonObjectives!=null){
                CardPrinter.printObjectiveCardSideSameRow(commonObjectives[0], commonObjectives[1]);
            } else {
                System.out.println("You can't use this command now!");
            }
        }
        else if (input.equals("/personalObjective")){
            if(personalObjective!=null){
                CardPrinter.printObjectiveCardSide(personalObjective);
            } else {
                System.out.println("You can't use this command now!");
            }
        }
    }

    /**
     * A method that prints out a useful legend.
     */
    public void printLegend(){
        CardPrinter.printLegend();
    }

    /**
     * A method that adds a received public message to the chat
     * and notifies the player.
     *
     * @param msg the Socket message containing the content of the message
     */
    private void addPublicMessage(publicChatMessageMsg msg){
        int sender = Arrays.asList(usernames).indexOf(msg.getSender());
        this.chat.addMessage(printUsername(sender), "all", msg.getMessage());
        System.out.println("New public message in chat!");
    }

    /**
     * A method that adds a received private message to the chat
     * and notifies the player.
     *
     * @param msg the Socket message containing the sender and the content of the message
     */
    private void addPrivateMessage(privateChatMessageMsg msg){
        int sender = Arrays.asList(usernames).indexOf(msg.getSender());
        int receiver = Arrays.asList(usernames).indexOf(msg.getReceiver());
        this.chat.addPrivateMessage(printUsername(sender), printUsername(receiver), msg.getMessage());
        System.out.println("New private message from " + printUsername(sender) + " in chat!");
    }

    /**
     * A method that prints out the scores of all the players.
     */
    public void showScoreboard(){
        System.out.println("Scores: ");
        for (int i=0; i<scores.length; i++){
            System.out.println("Player " + printUsername(i)+ ": " + scores[i]);
        }
    }

    /**
     * A method that prints out the cards on the ground.
     */
    public void showOnGround(){
        for (PlayableCard c : onGround){
            if(c!=null){
                CardPrinter.printPlayableCardSideSameRow(c.getFrontside(), false, c.getBackside(), false);
            }
        }
    }

    /**
     * A method that prints out the deployed cards of a player.
     *
     * @param player the index of the player
     */
    public void showDeployed(int player){
        DeployedPrinter.printDeployedCard(deployed[player], placements[player]);
    }

    /**
     * A method that retrieves the input that was hanging in the buffer.
     *
     * @return the input
     */
    private String getHangingInput() {
        if (hangingInput == null) {
            return null;
        } else {
            String input = hangingInput;
            hangingInput = "";
            return input;
        }
    }

    /**
     * A method that creates the thread that will execute the
     * commands that the player might want to launch while waiting
     * for its turn.
     */
    private void buildInputThread() {
        // Thread to execute commands that the player might want to launch (if any)
        inputThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    captureCommands();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
