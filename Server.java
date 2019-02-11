import java.io.*;
import java.net.*;

public class Server {

    final static String TCP = "TCP";
    final static String UDP = "UDP";

    public static void main(String argv[]) throws Exception {
        int portNumber;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Please type port number to listen.\nUsage Example: 1234");

        portNumber = Integer.valueOf(inFromUser.readLine());

        while (true) {
            System.out.println("\nPlease select transport protocol.\nAvailable type: TCP, UDP");

            String selectedProtocol = inFromUser.readLine();

            if (selectedProtocol.equalsIgnoreCase(TCP)) {
                tcp(portNumber);
                break;
            } else if (selectedProtocol.equalsIgnoreCase(UDP)) {
                udp(portNumber);
                break;
            } else System.out.println("Not corrected transport protocol.");
        }
    }

    private static void tcp(int portNumber) throws Exception {
        Socket connectionSocket;
        BufferedReader inFromClient;
        DataOutputStream outToClient;
        String clientSentence;
        String lengthOfClientSentence;

        ServerSocket welcomeSocket = new ServerSocket(portNumber);

        while (true) {
            System.out.println("Waiting for Client on port " + portNumber + " ...");

            connectionSocket = welcomeSocket.accept();

            System.out.println("Connected to Client " + connectionSocket.getInetAddress());

            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            try {
                while (!connectionSocket.isClosed()) {
                    System.out.println("Waiting for the client to type...");

                    clientSentence = inFromClient.readLine();
                    lengthOfClientSentence = String.valueOf(clientSentence.length());
                    outToClient.writeBytes(lengthOfClientSentence + '\n');

                    System.out.println("Responded to the client");
                }
            } catch (Exception e) {
                System.out.println("Disconnected.");
            }
        }
    }

    private static void udp(int portNumber) throws Exception{
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket;
        InetAddress clientIPAddress;
        int clientPort;
        String clientSentence;
        String lengthOfClientSentence;
        byte[] sendData;
        DatagramPacket sendPacket;
        DatagramSocket serverSocket = new DatagramSocket(portNumber);

        while (true) {
            System.out.println("Waiting for a packet from Client on port " + portNumber + " ...");

            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            clientIPAddress = receivePacket.getAddress();
            clientPort = receivePacket.getPort();

            System.out.println("Received a packet from Client " + clientIPAddress + " " + clientPort);

            clientSentence = new String(receivePacket.getData());
            lengthOfClientSentence = String.valueOf(clientSentence.indexOf('\n'));
            sendData = lengthOfClientSentence.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, clientIPAddress, clientPort);
            serverSocket.send(sendPacket);

            System.out.println("Responded to the client");
        }
    }
}
