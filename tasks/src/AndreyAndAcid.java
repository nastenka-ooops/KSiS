import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class AndreyAndAcid {
    public int amount;
    public ArrayList<Integer> values = new ArrayList<>();
    public void readData(){
        Scanner scanner = new Scanner(System.in);
        amount = scanner.nextInt();
        for (int i = 0; i < amount; i++) {
            values.add(scanner.nextInt());
        }
    }

    public void solution(){
        boolean isLess = false;
        int answer = -1;
        for (int i = 0; i < amount-1; i++) {
            if (values.get(i) > values.get(i+1)){
                isLess = true;
                break;
            }
        }
        if (!isLess) {
            answer = values.get(values.size() - 1) - values.get(0);
        }
        System.out.println(answer);
    }
}
