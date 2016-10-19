package org.ebitbucket.model.User;

public class UserDetail {
    private String username;
    private String about;
    private String name;
    private String email;
    private Integer id;
    private Boolean isAnonymous;
    private String[] followers;
    private String[] following;
    private Integer[] subscriptions;

    public UserDetail(String username, String about, String name, String email, Integer id, Boolean isAnonymous) {
        this.username = username;
        this.about = about;
        this.name = name;
        this.email = email;
        this.id = id;
        this.isAnonymous = isAnonymous;
    }

    public void setFollowers(String[] followers) {
        this.followers = followers;
    }

    public void setFollowing(String[] following) {
        this.following = following;
    }

    public void setSubscriptions(Integer[] subscriptions) {
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

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public String[] getFollowers() {
        return followers;
    }

    public String[] getFollowing() {
        return following;
    }

    public Integer[] getSubscriptions() {
        return subscriptions;
    }
}
