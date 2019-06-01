package com.example.tim.onsdomeinga.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * How to use ProxyOnsDomein
 * - First determine if you are programming for a HC (Home Central) or a GA (User Application)
 * - Both the HC and GA are so called Clients
 * FOR GA:
 * - First always connect your Client with the server by using the method connectClientToServer(), always provide a unique client_ID
 * - After connection is made you can send commands from the GA to the HC and the HC will respond. The protocol used is:
 *  requesting client_ID(String);request for client_ID(String);command(string)
 *  Command needs to have the following syntax: get/set/res;text your HC understands
 * - After a request to a HC is made it will respond with the same protocol, make sure you catch the return value and handle accordingly.
 * - Use closeConnection() after sending and receiving the response to make sure no data is transmitted.
 * FOR HC:
 * - First always connect your Client with the server by using the method connectClientToServer(), always provide a unique client_ID
 * - Start listening for incoming traffic using the receiveRequest() method (use an infinite while loop)
 * - Handle the request accordingly
 * - ALWAYS send a response back to the requesting client_ID and follow the same protocol
 */
public class OnsProxy {
    private Scanner inServer;
    private PrintWriter outServer;

    /**
     * Connect with server and do initial handshake
     * @throws IOException, thrown exception needs to be catched using this proxy.
     */
    private void connectWithServer(String client_id) throws IOException {
        final int ODSP = 8888;
//        final String SERVER_IP = "172.20.10.11"; // laptop Mathijs
//        final String SERVER_IP = "172.20.10.10"; // telefoon Wouter
//        final String SERVER_IP = "172.20.10.4"; // telefoon Tim
        final String SERVER_IP = "192.168.2.139"; // laptop Tim
//        final String SERVER_IP = "192.168.2.159"; // Windows laptop Tim
//        final String SERVER_IP = "localhost"; // localhost

        // Get the socket from the server
        Socket s = new Socket(SERVER_IP, ODSP);
        // Open the streams to make the handshake
        InputStream inStream = s.getInputStream();
        OutputStream outStream = s.getOutputStream();
        inServer = new Scanner(inStream);
        outServer = new PrintWriter(outStream);

        boolean reactionFromServer = true;
        while (reactionFromServer) {
            String reaction = inServer.nextLine();

            if (reaction.equals("Who are you?")) {
                reactionFromServer = false;
                outServer.println(client_id);
                outServer.flush();
            }
        }
    }

    /**
     * Method to connect a client with the server.
     * Used for both the GA and the HC
     * @param client_id, Unique String that describes the client.
     * @throws IOException, this method can produce a IOException, always use in Try/catch block
     */
    public void connectClientToServer(String client_id) throws IOException {
        connectWithServer(client_id);
    }

    /**
     * Method for GA to use.
     * Used for sending requests to HC
     * @param requestFromId, String with unique id from requester (GA)
     * @param requestForId String with unique id from receiver (HC)
     * @param message String containing the message for receiver (HC)
     * @return String with the response from receiver to requester (HC responds to GA)
     */
    public String sendRequest(String command, String requestFromId, String requestForId, String message) {
        String request = command + ";" + requestFromId + ";" + requestForId + ";" + message;
        outServer.println(request);
        outServer.flush();
        return receiveRequest();
    }

    /**
     * Method only used by GA because HC needs a permanent connection
     * Use to close the connection with the server (and save some kb's on your bundle)
     */
    public void closeConnection() {
        inServer.close();
        outServer.close();
    }

    /**
     * Method used only by HC for responding to the GA's request.
     * Similar to sendRequest method except the lack of a return parameter.
     * @param requestFromId, String with unique id from responder (HC)
     * @param requestForId, String with unique id from requester (GA)
     * @param message, String containing the message for requester (GA)
     */
    public void sendResponse(String command, String requestFromId, String requestForId, String message) {
        String request = command + ";" + requestFromId + ";" + requestForId + ";" + message;
        outServer.println(request);
        outServer.flush();
    }

    /**
     * Method used by HC to listen for incoming requests and used by proxy to return the response from HC
     * @return string, String with: 1) a request for a HC 2) the response from HC
     */
    public String receiveRequest() {
        return inServer.nextLine();
    }
}