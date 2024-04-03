import java.io.*;
import java.net.*;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
                                String dateStr = matcher.group(1); // День в формате YY-MM-DD
                                String timeStr = matcher.group(2); // Время в формате HH:mm:ss

                                // Разбиваем день на компоненты
                                String[] dateComponents = dateStr.split("-");
                                int year = 2000 + Integer.parseInt(dateComponents[0]); // Добавляем 2000 год, предполагая, что YY - это 2000 + YY
                                int month = Integer.parseInt(dateComponents[1]) - 1; // Месяцы в Calendar начинаются с 0
                                int day = Integer.parseInt(dateComponents[2]);

                                // Разбиваем время на компоненты
                                String[] timeComponents = timeStr.split(":");
                                int hour = Integer.parseInt(timeComponents[0])+3;
                                int minute = Integer.parseInt(timeComponents[1]);
                                int second = Integer.parseInt(timeComponents[2]);

                                // Создаем объект Calendar и устанавливаем в него день и время
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