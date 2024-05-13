import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args){
        String[] serverAddresses = new String[]{"127.0.0.1","129.6.15.29", "129.6.15.28", "129.6.15.30", "129.6.15.27", "129.6.15.26",
                "132.163.97.1", "132.163.97.2", "132.163.97.3", "132.163.97.4", "132.163.97.6", "132.163.96.1",
                "132.163.96.2", "132.163.96.3", "132.163.96.4", "132.163.96.6", "128.138.140.44", "128.138.141.172", "128.138.140.211"};
        int port = 13;
        int packetSize = 1024;

        String regex = "(\\d{2}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}:\\d{2})";

        for (String serverAddress : serverAddresses) {

            try (Socket tcpSocket = new Socket(serverAddress, port)) {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[packetSize];
                byteArrayOutputStream.write(buffer, 0, packetSize);

                boolean isGetResponse = false;
                String response;
                while (true) {
                    BufferedReader tcpIn = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

                    while ((response = tcpIn.readLine()) != null) {
                        isGetResponse = true;
                        if (!response.isEmpty()) {
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(response);

                            if (matcher.find()) {
                                String dateStr = matcher.group(1);
                                String timeStr = matcher.group(2);

                                String[] dateComponents = dateStr.split("-");
                                int year = 2000 + Integer.parseInt(dateComponents[0]);
                                int month = Integer.parseInt(dateComponents[1]) - 1;
                                int day = Integer.parseInt(dateComponents[2]);

                                String[] timeComponents = timeStr.split(":");
                                int hour = Integer.parseInt(timeComponents[0])+3;
                                int minute = Integer.parseInt(timeComponents[1]);
                                int second = Integer.parseInt(timeComponents[2]);

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, month, day, hour, minute, second);

                                System.out.println(calendar.getTime()+"\nfrom " + tcpSocket.getInetAddress() + "/" + tcpSocket.getInetAddress().getHostName() + " port " + tcpSocket.getPort() + "\n");
                            } else {
                                System.out.println(response + "\nfrom " + tcpSocket.getInetAddress() + "/" + tcpSocket.getInetAddress().getHostName() + " port " + tcpSocket.getPort() + "\n");
                            }
                        }
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