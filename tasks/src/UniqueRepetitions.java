import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class UniqueRepetitions {
    public boolean isRepetitionsUnique(int[] input){
        ArrayList<Integer> repetitionValues = new ArrayList<>();
        Arrays.sort(input);
        int count=1;
        int prevValue = input[0];
        for (int i = 1; i < input.length; i++) {
            if (input[i]==prevValue){
                count++;
            } else {
                repetitionValues.add(count);
                count=1;
                prevValue=input[i];
            }
        }
        repetitionValues.add(count);
        repetitionValues.sort(Comparator.naturalOrder());
        boolean result = true;
        for (int i = 0; i <repetitionValues.size()-1; i++) {
            if (repetitionValues.get(i)==repetitionValues.get(i+1)){
                result=false;
                break;
            }
        }
        return result;
    }
}
