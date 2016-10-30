package org.ebitbucket.model.Post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostRequest {
    private Integer id;
    private String forum;
    private String user;
    private Integer thread;
    private Integer parent;
    private String message;
    private String date;
    private Boolean isApproved;
    private Boolean isDeleted;
    private Boolean isEdited;
    private Boolean isHighlighted;
    private Boolean isSpam;

    @JsonCreator
    public PostRequest(@JsonProperty("id") Integer id,
                       @JsonProperty("date") String date,
                       @JsonProperty("forum") String forum,
                       @JsonProperty("isApproved") Boolean isApproved,
                       @JsonProperty("isDeleted") Boolean isDeleted,
                       @JsonProperty("isEdited") Boolean isEdited,
                       @JsonProperty("isHighlighted") Boolean isHighlighted,
                       @JsonProperty("isSpam") Boolean isSpam,
                       @JsonProperty("message") String message,
                       @JsonProperty("parent") Integer parent,
                       @JsonProperty("thread") Integer thread,
                       @JsonProperty("user") String user) {
        this.date = date;
        this.forum = forum;
        this.isApproved = isApproved;
        this.isDeleted = isDeleted;
        this.isEdited = isEdited;
        this.isHighlighted = isHighlighted;
        this.isSpam = isSpam;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
        this.user = user;
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public String getForum() {
        return forum;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public Boolean getEdited() {
        return isEdited;
    }

    public Boolean getHighlighted() {
        return isHighlighted;
    }

    public Boolean getSpam() {
        return isSpam;
    }

    public String getMessage() {
        return message;
    }

    public Integer getParent() {
        return parent;
    }

    public Integer getThread() {
        return thread;
    }

    public String getUser() {
        return user;
    }

    public void setId(Integer id) {
        this.id = id;
    }


}
