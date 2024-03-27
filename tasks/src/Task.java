import java.util.Date;

public class Task {
    private String name;
    private String description;
    private Date dateOfCompletion;
    private String category;

    public Task(String name, String description, Date dateOfCompletion, String category) {
        this.name = name;
        this.description = description;
        this.dateOfCompletion = dateOfCompletion;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateOfCompletion() {
        return dateOfCompletion;
    }

    public void setDateOfCompletion(Date dateOfCompletion) {
        this.dateOfCompletion = dateOfCompletion;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dateOfCompletion=" + dateOfCompletion +
                ", category='" + category + '\'' +
                '}';
    }
}
