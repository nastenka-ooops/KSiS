import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args){
        String[] serverAddresses = new String[]{"127.0.0.1","129.6.15.29", "129.6.15.28", "129.6.15.30", "129.6.15.27", "129.6.15.26",
                "132.163.97.1", "132.163.97.2", "132.163.97.3", "132.163.97.4", "132.163.97.6", "132.163.96.1",
                "132.163.96.2", "132.163.96.3", "132.163.96.4", "132.163.96.6", "128.138.140.44", "128.138.141.172", "128.138.140.211"};
        int port = 13;
        int packetSize = 1024;

        for (String serverAddress : serverAddresses) {

            try (Socket tcpSocket = new Socket(serverAddress, port)) {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[packetSize];
                byteArrayOutputStream.write(buffer, 0, packetSize);

                boolean isGetResponse = false;
                String response;
                while (true) {
                    BufferedReader tcpIn = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

                    //response = tcpIn.readLine();
                    while ((response = tcpIn.readLine()) != null) {
                        isGetResponse = true;
                        if (!response.isEmpty())
                        System.out.println(response + "\nfrom " + tcpSocket.getInetAddress()+ "/"+tcpSocket.getInetAddress().getHostName() + " port " + tcpSocket.getPort()+"\n");
                        //response = tcpIn.readLine();
                    }
                    if (isGetResponse) {
                        break;
                    }
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout reached");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}