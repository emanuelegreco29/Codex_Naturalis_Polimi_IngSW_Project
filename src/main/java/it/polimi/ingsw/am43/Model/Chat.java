package it.polimi.ingsw.am43.Model;

import java.util.ArrayList;

/**
 * This class represents the chat of the game.
 * It contains the list of messages sent in the chat.
 * Each player has its own chat object.
 */
public class Chat {

    /**
     * The list of messages sent in the chat.
     */
    private final ArrayList<String> messages;

    /**
     * The constructor of the class.
     */
    public Chat() {
        this.messages = new ArrayList<>();
    }

    /**
     * A method that retrieves the list of messages in the chat.
     *
     * @return the list of messages
     */
    public ArrayList<String> getMessages() {
        return messages;
    }

    /**
     * Adds a new message to the chat.
     *
     * @param  sender   the sender of the message
     * @param  receiver the receiver of the message
     * @param  message  the content of the message
     */
    public void addMessage(String sender, String receiver, String message) {
        String toAdd = "[PUBLIC] " + sender + " -> " + receiver + ": " + message;
        messages.add(toAdd);
    }

    /**
     * Adds a new private message to the chat.
     *
     * @param  sender   the sender of the message
     * @param  receiver the receiver of the message
     * @param  message  the content of the message
     */
    public void addPrivateMessage(String sender, String receiver, String message) {
        String toAdd = "[PRIVATE] " + sender + " -> " + receiver + ": " + message;
        messages.add(toAdd);
    }

    /**
     * Prints all the messages in the chat to the console.
     */
    public void viewChat() {
        if (messages.isEmpty()) {
            System.out.println("No messages yet! Try sending the first one ;)");
        } else {
        for (String message : messages) {
            System.out.println(message);
        }}
    }
}