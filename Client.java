import java.io.*;
import java.net.*;

public class Client {

        final static String TCP = "TCP";
        final static String UDP = "UDP";
        final static String QUIT = "QUIT";

        public static void main(String argv[]) throws Exception {
            String serverName;
            int serverPortNumber;
            String selectedProtocol;
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Please type server name.\nUsage Example: localhost");

            serverName = inFromUser.readLine();

            System.out.println("Please type server port number.\nUsage Example: 1234");

            serverPortNumber = Integer.valueOf(inFromUser.readLine());

            while (true) {
                System.out.println("\nPlease select transport protocol.\nAvailable type: TCP, UDP");

                selectedProtocol = inFromUser.readLine();

                if (selectedProtocol.equalsIgnoreCase(TCP)) {
                    tcp(serverName, serverPortNumber, inFromUser);
                    break;
                } else if (selectedProtocol.equalsIgnoreCase(UDP)) {
                    udp(serverName, serverPortNumber, inFromUser);
                    break;
                } else System.out.println("Not corrected transport protocol.");
            }
        }

        private static void tcp(String serverName, int serverPortNumber, BufferedReader inFromUser) throws Exception{
            String message;
            String response;
            Socket clientSocket = new Socket(serverName, serverPortNumber);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while(true) {
                System.out.println("\nPlease type QUIT if you'd like to exit the program. Otherwise, you can type a message to server. A server will return the length of your message.");

                message = inFromUser.readLine();

                if(message.equalsIgnoreCase(QUIT)){
                    System.out.println("Disconnected.");

                    clientSocket.close();
                    break;
                }

                outToServer.writeBytes(message + '\n');
                response = inFromServer.readLine();

                System.out.println("From server: the last input's length is " + response);
            }
        }

    private static void udp(String serverName, int serverPortNumber, BufferedReader inFromUser) throws Exception{
        String message;
        byte[] sendData;
        DatagramPacket sendPacket;
        DatagramPacket receivePacket;
        String response;

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress serverIPAddress = InetAddress.getByName(serverName);

        byte[] receiveData = new byte[1024];

        while(true) {
            System.out.println("\nPlease type QUIT if you'd like to exit the program. Otherwise, you can type a message to server. A server will return the length of your message.");

            message = inFromUser.readLine();

            if(message.equalsIgnoreCase(QUIT)){
                System.out.println("Disconnected.");

                clientSocket.close();
                break;
            }

            sendData = (message+'\n').getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, serverPortNumber);
            clientSocket.send(sendPacket);
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            response = new String(receivePacket.getData());

            System.out.println("From server: the last input's length is " + response);
        }
    }
}
