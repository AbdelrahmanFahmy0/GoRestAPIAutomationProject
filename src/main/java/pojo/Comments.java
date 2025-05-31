package pojo;

public class Comments {

    // Fields
    String name;
    String email;
    String body;


    // Setters and Getters
    // Name
    public String getName() {
        return name;
    }

    public Comments setName(String name) {
        this.name = name;
        return this;
    }

    // Email
    public String getEmail() {
        return email;
    }

    public Comments setEmail(String email) {
        this.email = email;
        return this;
    }

    // Body
    public String getBody() {
        return body;
    }

    public Comments setBody(String body) {
        this.body = body;
        return this;
    }
}
