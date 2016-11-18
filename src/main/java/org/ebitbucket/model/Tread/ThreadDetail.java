package org.ebitbucket.model.Tread;

import org.ebitbucket.lib.Functions;

@SuppressWarnings("unused")
public class ThreadDetail {
    private Integer id;
    private Object forum;
    private Object user;
    private String title;
    private String message;
    private String slug;
    private String date;

    private Boolean isClosed;
    private Boolean isDeleted;

    private Integer points;
    private Integer posts;
    private Integer likes;
    private Integer dislikes;

    public ThreadDetail(Integer id, Object forum, Object user, String title, String message, String slug, String date, Boolean isClosed, Boolean isDeleted, Integer likes,Integer dislikes, Integer posts) {
        this.id = id;
        this.forum = forum;
        this.user = user;
        this.title = title;
        this.message = message;
        this.slug = slug;
        this.date = date;
        this.isClosed = isClosed;
        this.isDeleted = isDeleted;
        this.likes = likes;
        this.dislikes = dislikes;
        this.posts = posts;
    }

    public Integer getId() {
        return id;
    }

    public Object getForum() {
        return forum;
    }

    public Object getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }

    public String getDate() {
        return date;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public Integer getPoints() {
        return points;
    }

    public Integer getPosts() {
        return posts;
    }

    public Integer getLikes() {
        return likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void setForum(Object forum) {
        this.forum = forum;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
