import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        int port = 13;

        try(ServerSocket tcpServerSocket = new ServerSocket(port)) {
            while (true) {
                Socket tcpSocket=tcpServerSocket.accept();

                String dayTime = new Date().toString();
                byte[] send = dayTime.getBytes();

                OutputStream tcpOut = tcpSocket.getOutputStream();
                tcpOut.write(send);

                tcpOut.close();
            }
        }
        catch(IOException e){
            throw new RuntimeException();
        }
    }
}