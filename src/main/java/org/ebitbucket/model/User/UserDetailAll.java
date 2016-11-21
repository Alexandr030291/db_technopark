package org.ebitbucket.model.User;

import org.ebitbucket.services.UserService;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class UserDetailAll {

    private String about;
    private String email;
    private Integer id;
    private Boolean isAnonymous;
    private String name;
    private String username;

    private List<String> followers;
    private List<String> following;
    private List<Integer> subscriptions;

    public UserDetailAll(Integer id, String username, String name, String email,  String about, Boolean isAnonymous) {
        this.username = username;
        this.about = about;
        this.name = name;
        this.email = email;
        this.id = id;
        this.isAnonymous = isAnonymous;
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
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

    public void addFollowing(String following){
        if (following == null)
            return;
        this.following.add(following);
    }

    public void addFollowers(String follower){
        if (follower == null)
            return;
        this.followers.add(follower);
    }

    public void addSubscriptions(Integer subscription){
        this.subscriptions.add(subscription);
    }
}


