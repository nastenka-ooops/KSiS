import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Main {
    public static void main(String[] args) throws IOException {
        MACSearcher macSearcher = new MACSearcher();
        macSearcher.findMACAddress();
        ResourcesSearcher resourcesSearcher = new ResourcesSearcher();
        resourcesSearcher.getResources(null);
    }

}