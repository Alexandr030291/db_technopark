package org.ebitbucket.model.Post;

@SuppressWarnings("unused")
public class PostDetails {
    private Integer id;
    private Object forum;
    private Object user;
    private Object thread;
    private Integer parent;
    private String message;
    private String date;
    private Boolean isApproved;
    private Boolean isDeleted;
    private Boolean isEdited;
    private Boolean isHighlighted;
    private Boolean isSpam;

    private int dislikes;
    private int likes;
    private int points;

    public PostDetails(Integer id,
                       Object forum,
                       Object user,
                       Object thread,
                       Integer parent,
                       String message,
                       String date,
                       Boolean isApproved,
                       Boolean isDeleted,
                       Boolean isEdited,
                       Boolean isHighlighted,
                       Boolean isSpam,
                       int dislikes,
                       int likes) {
        this.id = id;
        this.forum = forum;
        this.user = user;
        this.thread = thread;
        this.parent = parent;
        this.message = message;
        this.date = date;
        this.isApproved = isApproved;
        this.isDeleted = isDeleted;
        this.isEdited = isEdited;
        this.isHighlighted = isHighlighted;
        this.isSpam = isSpam;
        this.dislikes = dislikes;
        this.likes = likes;
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

    public Object getThread() {
        return thread;
    }

    public Integer getParent() {
        return parent;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public Boolean getIsEdited() {
        return isEdited;
    }

    public Boolean getIsHighlighted() {
        return isHighlighted;
    }

    public Boolean getIsSpam() {
        return isSpam;
    }

    public int getDislikes() {
        return dislikes;
    }

    public int getLikes() {
        return likes;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setForum(Object forum) {
        this.forum = forum;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public void setThread(Object thread) {
        this.thread = thread;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
