import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int port = 13;
        int packetSize = 1024;

        try (Socket tcpSocket= new Socket(serverAddress, port)){

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[packetSize];
            byteArrayOutputStream.write(buffer, 0, packetSize);

            while (true) {
                BufferedReader tcpIn = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

                String response;
                while ((response = tcpIn.readLine()) != null) {
                    System.out.println(response+"\nfrom " + tcpSocket.getInetAddress()+" port "+tcpSocket.getPort());
                }
            }

        }catch (SocketTimeoutException e ){
            System.out.println("Timeout reached");
        } catch (IOException e){
            throw new RuntimeException();
        }
    }
}