package it.polimi.ingsw.am43.Main;

import it.polimi.ingsw.am43.Network.GameServer;
import it.polimi.ingsw.am43.Network.Socket.SocketServer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.net.*;

import static java.lang.Integer.parseInt;

@SuppressWarnings("SpellCheckingInspection")
public class MainServer {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        // The two variables below are used to get the IP address and port on which the servers will listen.
        String inputIP, inputPort;

        // The GameServer object used to start both Socket and RMI servers.
        GameServer server;

        // The code below is used to get the IP address of the machine on which the server is running.
        InetAddress localIP = InetAddress.getLocalHost();

        createGameDirectory();

        // Ask for IP on which server will start
        System.out.println("Choose IP to launch server on:");
        System.out.println("(1) localhost (JUST FOR PLAYING ON LOCAL MACHINE!)");
        System.out.println("(2) " + localIP.getHostAddress() + " (local IP - Choose this to play with friends!)");
        do {
            inputIP = new Scanner(System.in).nextLine();
            if(!inputIP.equals("1") && !inputIP.equals("2")) {
                System.out.println("Invalid choice. Please try again");
            }
        } while (!inputIP.equals("1") && !inputIP.equals("2"));

        if (inputIP.equals("2")) {
            inputIP = localIP.getHostAddress();
        } else {
            inputIP = "localhost";
        }

        // Ask for port on which server will listen
        do{
            System.out.println("Insert server port (leave empty for default): ");
            inputPort = new Scanner(System.in).nextLine();
            if(!inputPort.isEmpty() && (!inputPort.matches("[0-9]+") || !(parseInt(inputPort)>=0) || !(parseInt(inputPort)<=65535))) {
                System.out.println("Invalid port. Please try again");
            }
        } while (!inputPort.isEmpty() && (!inputPort.matches("[0-9]+") || !(parseInt(inputPort)>=0) || !(parseInt(inputPort)<=65535)));

        if(inputPort.isEmpty()){
            inputPort = String.valueOf(SocketServer.PORT);
        }

        System.out.println("Do you wanna create a new game or load an existing one?\n1) Create new game\n2) Load an existing game");
        String choice = new Scanner(System.in).nextLine();
        while(choice.trim().isEmpty() || !choice.matches("[1-2]")){
            System.out.println("Invalid choice. Please try again");
            choice = new Scanner(System.in).nextLine();
        }
        if(parseInt(choice) == 1){
            cls();
            // Creates and runs both Socket and RMI servers with a new Game
            GameServer gameServer = new GameServer(inputIP, parseInt(inputPort));
            gameServer.run();
        } else {
            System.out.println("Please insert the ID of the game you want to load:");
            String gameID = new Scanner(System.in).nextLine();
            while(gameID.isEmpty() || !gameID.matches("[0-9]{6}") || !existFile(gameID)) {
                if(gameID.isEmpty() || !gameID.matches("[0-9]{6}")) {
                    System.out.println("Invalid input. Please provide a number between 100000 and 999999:");
                } else if (!existFile(gameID)) {
                    System.out.println("No game with that ID. Please try again:");
                }
                gameID = new Scanner(System.in).nextLine();
            }
            cls();
            server= new GameServer(inputIP, parseInt(inputPort), gameID);
            server.run();
        }
    }

    /**
     * A method that clears the console screen by executing the 'cls' command.
     */
    private static void cls(){
        try{
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error executing cls command: " + e.getMessage());
        }
    }

    /**
     * A method that creates a game directory at the user's home directory with the name "CodexNaturalis".
     * If the directory already exists, the method returns without doing anything.
     * If an IOException occurs during the creation of the directory, the method throws a RuntimeException.
     *
     * @throws         	RuntimeException	if an IOException occurs during the creation of the directory
     */
    private static void createGameDirectory() {
        String homePath = System.getProperty("user.home");
        String path = homePath + File.separator + "CodexNaturalis";
        try {
            Path directory = Paths.get(path);
            if(Files.exists(directory)) {
                return;
            }
            Files.createDirectory(directory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a file with the given game ID exists in the "CodexNaturalis" directory
     * at the user's home directory.
     *
     * @param  gameID  the ID of the file to check
     * @return         true if the file exists, false otherwise
     */
    private static boolean existFile(String gameID) {
        String homePath = System.getProperty("user.home");
        String path = homePath + File.separator + "CodexNaturalis" + File.separator + gameID;
        File file = new File(path);
        return file.exists();
    }
}
