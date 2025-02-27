package it.polimi.ingsw.am43.ModelTests;

import it.polimi.ingsw.am43.Model.Chat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatTest {

    private Chat chat;
    private final String sender = "sender";
    private final String receiver = "receiver";
    private final String message = "message";
    private String toAddPublic;
    private String toAddPrivate;

    @BeforeEach
    public void setUp() {
        chat = new Chat();
        toAddPublic = "[PUBLIC] " + sender + " -> " + receiver + ": " + message;
        toAddPrivate = "[PRIVATE] " + sender + " -> " + receiver + ": " + message;
    }

    @Test
    void constructorTest() {
        chat = new Chat();
    }

    @Test
    void getMessagesTest() {
        chat.addMessage(sender, receiver, message);
        chat.addPrivateMessage(sender, receiver, message);
        Assertions.assertEquals(toAddPublic, chat.getMessages().get(0));
        Assertions.assertEquals(toAddPrivate, chat.getMessages().get(1));
    }

    @Test
    void addMessageTest() {
        chat.addMessage(sender, receiver, message);
        Assertions.assertTrue(chat.getMessages().contains(toAddPublic));
    }

    @Test
    void addPrivateMessageTest() {
        chat.addPrivateMessage(sender, receiver, message);
        Assertions.assertTrue(chat.getMessages().contains(toAddPrivate));
    }

    @Test
    void viewChatTest() {
        chat.viewChat();
        chat.addMessage(sender, receiver, message);
        chat.addPrivateMessage(sender, receiver, message);
        chat.viewChat();
    }
}