package org.ebitbucket.model.User;

import org.ebitbucket.services.UserService;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class UserDetail {
    private String username;
    private String about;
    private String name;
    private String email;
    private Integer id;
    private Boolean isAnonymous;
    private List<String> followers;
    private List<String> following;
    private List<Integer> subscriptions;

    public UserDetail(String username, String about, String name, String email, Integer id, Boolean isAnonymous) {
        this.username = username;
        this.about = about;
        this.name = name;
        this.email = email;
        this.id = id;
        this.isAnonymous = isAnonymous;

        UserService userService = new UserService(new JdbcTemplate());
        setFollowers(userService.followers(getEmail()));
        setFollowing(userService.following(getEmail()));
        setSubscriptions(userService.subscriptions(getEmail()));
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public void setSubscriptions(List<Integer> subscriptions) {
        this.subscriptions = subscriptions;
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

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public List<Integer> getSubscriptions() {
        return subscriptions;
    }
}
