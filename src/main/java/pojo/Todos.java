package pojo;

public class Todos {

    // Fields
    String title;
    String due_on;
    String status;

    // Setters and Getters
    // Title
    public String getTitle() {
        return title;
    }

    public Todos setTitle(String title) {
        this.title = title;
        return this;
    }

    // Due On
    public String getDue_on() {
        return due_on;
    }

    public Todos setDue_on(String due_on) {
        this.due_on = due_on;
        return this;
    }

    // Status
    public String getStatus() {
        return status;
    }

    public Todos setStatus(String status) {
        this.status = status;
        return this;
    }
}
