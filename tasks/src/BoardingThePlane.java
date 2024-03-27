import java.util.ArrayList;
import java.util.Scanner;

public class BoardingThePlane {
    int rowAmount;
    ArrayList<Row> rows = new ArrayList<>();
    int passengersGroupAmount;
    static class Row{
        boolean[] isEngaged = new boolean[6];
    }
    static class PassengerGroup {
        int passengersAmount;
        String side;
        String position;

        public PassengerGroup(int passengersAmount, String side, String position) {
            this.passengersAmount = passengersAmount;
            this.side = side;
            this.position = position;
        }

        @Override
        public String toString() {
            return "PassengerGroup{" +
                    "passengersAmount=" + passengersAmount +
                    ", side='" + side + '\'' +
                    ", position='" + position + '\'' +
                    '}';
        }
    }
    ArrayList<PassengerGroup> passengers = new ArrayList<>();
    public void readData(){
        Scanner scanner = new Scanner(System.in);
        rowAmount = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < rowAmount; i++) {
            StringBuilder tempLine = new StringBuilder(scanner.nextLine());
            tempLine.deleteCharAt(tempLine.indexOf("_"));
            Row tempRow = new Row();
            for (int j = 0; j < tempRow.isEngaged.length; j++) {
                if (tempLine.charAt(i)=='.'){
                    tempRow.isEngaged[i]=false;
                }
                if (tempLine.charAt(i)=='#'){
                    tempRow.isEngaged[i]=true;
                }
            }
        }
        passengersGroupAmount = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < passengersGroupAmount; i++) {
            String line = scanner.nextLine();
            String[] tempItem = line.split(" ");
            PassengerGroup temp = new PassengerGroup(Integer.parseInt(tempItem[0]), tempItem[1], tempItem[2]);
            passengers.add(temp);
        }
        for (int i = 0; i < passengersGroupAmount; i++) {
            System.out.println(passengers.get(i));
        }
    }

    public void solution(){
        
    }
}
