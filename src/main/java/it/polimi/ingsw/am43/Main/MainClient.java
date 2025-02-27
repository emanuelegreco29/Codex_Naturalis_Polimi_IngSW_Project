package it.polimi.ingsw.am43.Main;

import it.polimi.ingsw.am43.View.GUI.GUI;
import it.polimi.ingsw.am43.View.TUI.TUI;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class MainClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException, NotBoundException {

        // Variables used for user input, such as server's IP and port,connection method and desired interface.
        String inputIP, inputPORT, conn, interfaceType;

        // A scanner that reads user input.
        Scanner sc = new Scanner(System.in);

        // A boolean that indicates if the connection is a socket or RMI
        boolean connectionIsSocket;

        cls();
        // Ask for IP and check if it's validly formatted
        do{
            System.out.println("Insert server IP (leave empty for localhost): ");
            inputIP = sc.nextLine();
            if(!inputIP.isEmpty() && invalidIP(inputIP)){
                cls();
                System.out.println("Invalid IP. Please try again.");
            }
        } while (!inputIP.isEmpty() && invalidIP(inputIP));
        if(inputIP.isEmpty()){
            inputIP="localhost";
        }

        // Ask for port and check if it's validly formatted
        do{
            System.out.println("Insert server port (leave empty for default port): ");
            inputPORT = sc.nextLine();
            if(!inputPORT.isEmpty() && (!inputPORT.matches("[0-9]+") || !(parseInt(inputPORT)>=0) || !(parseInt(inputPORT)<=65535))){
                cls();
                System.out.println("Invalid port. Please try again.");
            }
        } while (!inputPORT.isEmpty() && (!inputPORT.matches("[0-9]+") || !(parseInt(inputPORT)>=0) || !(parseInt(inputPORT)<=65535)));
        if(inputPORT.isEmpty()) {
            inputPORT="1098";
        }

        cls();

        // Ask for connection method, check if there are servers running on it and set it
        do {
            System.out.println("Select connection method:");
            System.out.println("(1) Socket");
            System.out.println("(2) RMI");
            conn = sc.nextLine();
            if(!conn.equals("1") && !conn.equals("2")){
                cls();
                System.out.println("Invalid option. Please try again.");
            }
        } while (!conn.equals("1") && !conn.equals("2"));
        if (conn.equals("1")) {
            connectionIsSocket = true;
        } else {
            connectionIsSocket = false;
            inputPORT = String.valueOf(parseInt(inputPORT) + 1);
        }

        // Ask for desired interface
        do {
            System.out.println("Select desired interface:");
            System.out.println("(1) TUI");
            System.out.println("(2) GUI");
            interfaceType = sc.nextLine();
            if(!interfaceType.equals("1") && !interfaceType.equals("2")){
                cls();
                System.out.println("Invalid option. Please try again.");
            }
        } while (!interfaceType.equals("1") && !interfaceType.equals("2"));

        if (interfaceType.equals("1")) {
            new TUI(inputIP, parseInt(inputPORT), connectionIsSocket);
        } else {
            // Initialize GUI
            new GUI(inputIP, parseInt(inputPORT), connectionIsSocket);
        }
    }

    /**
     * A method that clears the console screen by executing the 'cls' command.
     */
    private static void cls(){
        try{
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing cls command: " + e.getMessage());
        }
    }

    /**
     * A method that checks if the given string is a valid IP address.
     *
     * @param  ip   the string to be checked
     * @return      true if the string is a valid IP address, false otherwise
     */
    private static boolean invalidIP(String ip){
        return !ip.matches("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    }

    @Deprecated
    private static boolean validServer(String inputIP, String inputPORT) {
        boolean validServer;

        try {
            InetAddress address = InetAddress.getByName(inputIP);
            validServer = address.isReachable(Integer.parseInt(inputPORT));
        }  catch (IOException e) {
            System.err.println("Unable to connect to server!");
            validServer = false;
        }
        return validServer;
    }
}