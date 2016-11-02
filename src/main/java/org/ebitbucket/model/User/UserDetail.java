
package org.ebitbucket.model.User;

import org.ebitbucket.services.UserService;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SuppressWarnings("unused")
public class UserDetail {

    private String about;
    private String email;
    private int id;
    private boolean isAnonymous;
    private String name;
    private String username;


    public UserDetail(Integer id, String username, String name, String email,  String about, Boolean isAnonymous) {
        this.username = username;
        this.about = about;
        this.name = name;
        this.email = email;
        this.id =0;// id;
        this.isAnonymous = isAnonymous;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getAbout() {
        return about;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}