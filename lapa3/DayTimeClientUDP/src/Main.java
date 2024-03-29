import java.io.IOException;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        String serverAddress = "255.255.255.255";
        int port = 13;
        int packetSize = 1024;
        try (DatagramSocket udpSocket = new DatagramSocket()) {

            udpSocket.setBroadcast(true);
            udpSocket.setSoTimeout(1000);

            InetAddress udpServerAddress = InetAddress.getByName(serverAddress);

            byte[] data = new byte[packetSize];

            DatagramPacket udpPacket = new DatagramPacket(data, data.length, udpServerAddress, port);
            udpSocket.send(udpPacket);

            while (true) {
                byte[] receiveDatagram = new byte[packetSize];
                DatagramPacket receivePacket = new DatagramPacket(receiveDatagram, receiveDatagram.length);
                udpSocket.receive(receivePacket);

                InetAddress senderAddress = receivePacket.getAddress();
                int senderPort = receivePacket.getPort();
                String receiveData = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println(receiveData + "\nfrom " + senderAddress.getHostAddress() + " port " + senderPort);
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout reached");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}