public class ShortenString {
    public StringBuilder shortenString(String input){
        StringBuilder result = new StringBuilder();
        int count = 1;
        char prevChar = input.charAt(0);
        for (int i = 1; i <input.length(); i++) {
            if ((input.charAt(i)==prevChar)){
                count++;
            } else {
                result.append(prevChar).append(count);
                prevChar=input.charAt(i);
                count=1;
            }
        }
        result.append(prevChar).append(count);
        if (result.length()>input.length()){
            return new StringBuilder(input);
        } else {
            return result;
        }
    }
}
