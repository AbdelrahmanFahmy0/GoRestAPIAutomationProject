package pojo;

public class Users {

    // Fields
    String name;
    String email;
    String gender;
    String status;

    // Setters and Getters

    //Name
    public String getName() {
        return name;
    }

    public Users setName(String name) {
        this.name = name;
        return this;
    }

    // Email
    public String getEmail() {
        return email;
    }

    public Users setEmail(String email) {
        this.email = email;
        return this;
    }

    // Gender
    public String getGender() {
        return gender;
    }

    public Users setGender(String gender) {
        this.gender = gender;
        return this;
    }

    // Status
    public String getStatus() {
        return status;
    }

    public Users setStatus(String status) {
        this.status = status;
        return this;
    }
}
