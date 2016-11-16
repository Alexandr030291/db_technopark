package org.ebitbucket.model.Tread;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ThreadRequest {
    private Integer id;
    private String forum;
    private String user;
    private String title;
    private String message;
    private String slug;
    private String date;
    private Boolean isClosed;
    private Boolean isDeleted;


    @JsonCreator
    public ThreadRequest(@JsonProperty("id") Integer id,
                         @JsonProperty("forum") String forum,
                         @JsonProperty("user") String user,
                         @JsonProperty("title") String title,
                         @JsonProperty("message") String message,
                         @JsonProperty("slug") String slug,
                         @JsonProperty("date") String date,
                         @JsonProperty("isClosed") Boolean isClosed,
                         @JsonProperty("isDeleted") Boolean isDeleted) {
        this.id = id;
        this.forum = forum;
        this.user = user;
        this.title = title;
        this.message = message;
        this.slug = slug;
        this.date = date;
        this.isClosed = isClosed;
        this.isDeleted = isDeleted;
    }

    public Integer getId() {
        return id;
    }

    public String getForum() {
        return forum;
    }

    public String getUser() {
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

    public Boolean getClosed() {
        return isClosed;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
