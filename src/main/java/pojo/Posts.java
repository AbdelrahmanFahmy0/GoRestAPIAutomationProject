package pojo;

public class Posts {

    // Fields
    String title;
    String body;

    // Setters and Getters
    // Title
    public Posts setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTitle() {
        return title;
    }

    // Body
    public Posts setBody(String body) {
        this.body = body;
        return this;
    }

    public String getBody() {
        return body;
    }
}
