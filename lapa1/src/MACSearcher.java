import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MACSearcher {
    public void findMACAddress() throws SocketException {

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();


        while (networkInterfaces.hasMoreElements()) {

            byte[] mac = networkInterfaces.nextElement().getHardwareAddress();
            if (mac!=null) {
                System.out.println("MAC address of: " + networkInterfaces.nextElement().getName());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                System.out.println(sb);
            }
        }
        System.out.println();
    }
}