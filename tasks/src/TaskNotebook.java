import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;

public class TaskNotebook {
    ArrayList<Task> tasks;
    public TaskNotebook() {
        tasks = new ArrayList<>();
    }

    public void printTasks(){
        for (int i = 0; i < tasks.size(); i++) {
            System.out.print(i+": ");
            System.out.println(tasks.get(i));
        }
    }

    public void addTask() throws ParseException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter name of the task");
        String name = scanner.nextLine();

        System.out.println("Enter description of the task");
        String description = scanner.nextLine();

        System.out.println("Enter completion date of the task");
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd.MM.yyyy");
        Date date= format.parse(scanner.nextLine());

        System.out.println("Enter category of the task");
        String category = scanner.nextLine();

        tasks.add(new Task(name, description, date, category));
    }

    public void deleteTask(int index){
        if (tasks.size()<index+1) {
            tasks.remove(index);
        }
    }

    public void printTasksByCategory(String category){
        for (Task task :
                tasks) {
            if (task.getCategory().equals(category)){
                System.out.println(task);
            }
        }
    }

    public void sortByCompletionDate(){
        tasks.sort(Comparator.comparing(Task::getDateOfCompletion));
        printTasks();
    }
}
