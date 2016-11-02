package org.ebitbucket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Vote {
    private Integer vote;
    private Integer post;
    private Integer thread;

    public Integer getVote() {
        return vote;
    }

    public Integer getPost() {
        return post;
    }

    public Integer getThread() {
        return thread;
    }

    @JsonCreator
    public Vote(@JsonProperty("vote") Integer vote,
                @JsonProperty("post") Integer post,
                @JsonProperty("thread") Integer thread) {

        this.vote = vote;
        this.post = post;
        this.thread = thread;
    }
}
