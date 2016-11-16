package org.ebitbucket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageUpdate {
    private Integer post;
    private Integer thread;
    private String message;
    private String slug;

    @JsonCreator
    public MessageUpdate(@JsonProperty("post") Integer post,
                         @JsonProperty("thread") Integer thread,
                         @JsonProperty("message") String message,
                         @JsonProperty("slug") String slug) {
        this.post = post;
        this.thread = thread;
        this.message = message;
        this.slug = slug;
    }

    public Integer getPost() {
        return post;
    }

    public Integer getThread() {
        return thread;
    }

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }
}
