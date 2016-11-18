package org.ebitbucket.model.User;

public class User {
    final private Integer id;
    final private String email;

    public User(Integer id, String email) {
        this.id = id;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
