import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        int port = 13;
        int packetSize = 1024;

        try (DatagramSocket udpSocket = new DatagramSocket(port)) {
            while (true) {
                byte[] receiveDatagram = new byte[packetSize];
                DatagramPacket receivePacket = new DatagramPacket(receiveDatagram, receiveDatagram.length);
                udpSocket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                String dayTime = new Date().toString();
                byte[] sendDatagram = dayTime.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendDatagram, sendDatagram.length, clientAddress, clientPort);
                udpSocket.send(sendPacket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}