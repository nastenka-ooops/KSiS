import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int port = 123;
        int packetSize = 1024;
        int numPackets = 1000000;

        AtomicLong udpEndTime = new AtomicLong();
        AtomicLong udpStartTime = new AtomicLong();

        AtomicLong tcpEndTime = new AtomicLong();
        AtomicLong tcpStartTime = new AtomicLong();

        try {
            DatagramSocket udpSocket = new DatagramSocket(port);
            ServerSocket tcpServerSocket = new ServerSocket(port);
//TODO UDP
            new Thread(()->{
                int i =0;
                try {
                    while (true) {
                        byte[] receiveDatagram = new byte[packetSize];
                        DatagramPacket receivePacket = new DatagramPacket(receiveDatagram, receiveDatagram.length);
                        udpSocket.receive(receivePacket);
                        i++;
                    }
                }
                catch(IOException e){
                    udpEndTime.set(System.nanoTime());

                    System.out.println("Speed of transferring data for UDP protocol");

                    double dataSent = numPackets * packetSize / 1024.0;
                    long totalTime = udpEndTime.get() - udpStartTime.get();
                    System.out.println(totalTime);
                    System.out.println((int)dataSent + " KBates transferred");
                    double transferRate = dataSent / (totalTime /1000000000.0);
                    System.out.printf("%.4f KBates per second%n", transferRate);

                    if(numPackets==i){
                        System.out.println("All packets have been transferred");
                    } else {
                        System.out.println(numPackets-i + " packets have been lost");
                    }
                }
            }).start();
//TODO TCP
            new Thread(()->{
                int i =0;
                try {
                    while (true) {
                        Socket tcpSocket=tcpServerSocket.accept();
                        InputStream tcpIn = tcpSocket.getInputStream();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[packetSize];

                        while ((tcpIn.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, packetSize);
                            i++;
                        }
                    }
                }
                catch(IOException e){
                    tcpEndTime.set(System.nanoTime());

                    System.out.println("\nSpeed of transferring data for TCP protocol");

                    long totalTime = tcpEndTime.get() - tcpStartTime.get();
                    System.out.println(totalTime);
                    double dataSent = numPackets * packetSize / 1024.0;
                    System.out.println((int) dataSent + " KBates transferred");
                    double transferRate = dataSent / (totalTime / 1000000000.0);
                    System.out.printf("%.4f KBates per second%n", transferRate);

                    if(numPackets==i){
                        System.out.println("All packets have been transferred");
                    } else {
                        System.out.println(numPackets-i + " packets have been lost");
                    }
                }
            }).start();

            //TODO UDP
            InetAddress udpServerAddress = InetAddress.getByName(serverAddress);

            udpStartTime.set(System.nanoTime());

                for (int i = 0; i < numPackets; i++) {
                    byte[] data = createRandomDatagram(packetSize);
                    DatagramPacket udpPacket = new DatagramPacket(data, data.length, udpServerAddress, port);
                    udpSocket.send(udpPacket);
                }
            udpSocket.close();

            // TODO TCP

            Socket tcpSocket = new Socket(serverAddress, port);
            OutputStream tcpOut = tcpSocket.getOutputStream();

            tcpStartTime.set(System.nanoTime());

            for (int i = 0; i < numPackets; i++) {
                byte[] data = createRandomDatagram(packetSize);
                tcpOut.write(data);
            }
            tcpOut.close();
            tcpSocket.close();
            tcpServerSocket.close();





            /*} catch (IOException e) {
                throw new RuntimeException(e);
            }*/

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static byte[] createRandomDatagram(int size){
        byte[] result = new byte[size];
        new Random().nextBytes(result);
        return result;
    }

}

