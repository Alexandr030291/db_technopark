package org.ebitbucket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Subscription{
    private final Integer thread;
    private final String user;

    @JsonCreator
    public Subscription(@JsonProperty("thread") Integer thread,
                        @JsonProperty("user") String user) {
        this.thread = thread;
        this.user = user;
    }

    public Integer getThread() {
        return thread;
    }

    public String getUser() {
        return user;
    }
}
