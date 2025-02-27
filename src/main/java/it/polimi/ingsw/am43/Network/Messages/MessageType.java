package it.polimi.ingsw.am43.Network.Messages;

/**
 * Enum class that defines the different types of messages.
 */
@SuppressWarnings("SpellCheckingInspection")
public enum MessageType {
    //to server messages
    HEARTBEAT,
    CHAT,
    STARTINGCARD,
    PERSONAL_OBJECTIVE_CHOICE,
    COUPLECARDS,
    PLAYTURN,
    PLAY,
    PLAYER_REJOINS,

    //to client messages
    GENERAL,
    FIRSTPLAYER,
    FIRSTPLAYERJOINS,
    GENERALPLAYER,
    GENERALPLAYERJOINS,
    PERSONALOBJECTIVES,
    GAMEINFO,
    PLEACABLECARDS,
    NEWINHAND,
    AVAILABLEPLACEMENTS,
    INITIAL_SITUATION,
    INITIALGAMEINFO,
    DRAW_PERSONAL_OBJECTIVES,
    NICKNAME_ALREADY_USED,
    FIRST_STILL_JOINING,
    CHECK_FIRST,
    LOAD_GAME,
    REJOIN_PLAYER,
    CANNOT_REJOIN,
    REQUIRED_DATA,
    GENERAL_MSG,
    PRIVATE_MSG,
    ENDGAME;



    /**
     * A method that returns a string representation of the current enum constant.
     *
     * @return a string representation of the message type
     */
    public String toString() {
        return switch (this) {
            //to server messages
            case HEARTBEAT -> "HEARTBEAT";
            case CHAT -> "CHAT";
            case STARTINGCARD -> "STARTINGCARD";
            case PERSONAL_OBJECTIVE_CHOICE -> "PERSONAL_OBJECTIVE_CHOICE";
            case COUPLECARDS -> "COUPLECARDS";
            case PLAYTURN -> "PLAYTURN";
            case PLAY -> "PLAY";
            case FIRSTPLAYERJOINS -> "FIRSTPLAYERJOINS";
            case GENERALPLAYERJOINS -> "GENERALPLAYERJOINS";
            case LOAD_GAME -> "LOAD_GAME";
            case PLAYER_REJOINS -> "PLAYER_REJOINS";

            //to client messages
            case GENERAL -> "GENERAL";
            case PERSONALOBJECTIVES -> "PERSONALOBJECTIVES";
            case GAMEINFO -> "GAMEINFO";
            case PLEACABLECARDS -> "PLEACABLECARDS";
            case NEWINHAND -> "NEWINHAND";
            case AVAILABLEPLACEMENTS -> "AVAILABLEPLACEMENTS";
            case INITIAL_SITUATION -> "INITIAL_SITUATION";
            case DRAW_PERSONAL_OBJECTIVES -> "DRAW_PERSONAL_OBJECTIVES";
            case FIRSTPLAYER -> "FIRSTPLAYER";
            case GENERALPLAYER -> "GENERALPLAYER";
            case INITIALGAMEINFO -> "INITIALGAMEINFO";
            case NICKNAME_ALREADY_USED -> "NICKNAME_ALREADY_USED";
            case FIRST_STILL_JOINING -> "FIRST_STILL_JOINING";
            case CHECK_FIRST -> "CHECK_FIRST";
            case REJOIN_PLAYER -> "REJOIN_PLAYER";
            case CANNOT_REJOIN -> "CANNOT_REJOIN";
            case REQUIRED_DATA -> "REQUIRED_DATA";
            case GENERAL_MSG -> "GENERAL_MSG";
            case PRIVATE_MSG -> "PRIVATE_MSG";
            case ENDGAME -> "ENDGAME";

        };
    }
}
