import java.text.ParseException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ParseException {
        TaskNotebook taskNotebook = new TaskNotebook();
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("Choose function");
            System.out.println("1-add task");
            System.out.println("2-delete task");
            System.out.println("3-get task by category");
            System.out.println("4-sort tasks by completion date");
            switch (Integer.parseInt(input.nextLine())) {
                case 1 -> taskNotebook.addTask();
                case 2 -> {
                    taskNotebook.printTasks();
                    System.out.println("Enter number of task you want to delete");
                    taskNotebook.deleteTask(input.nextInt());
                }
                case 3 -> {
                    System.out.println("Enter category of tasks you want to see");
                    taskNotebook.printTasksByCategory(input.nextLine());
                }
                case 4 -> taskNotebook.sortByCompletionDate();
            }
        }
    }
}