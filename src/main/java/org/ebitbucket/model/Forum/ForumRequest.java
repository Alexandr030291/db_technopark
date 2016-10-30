package org.ebitbucket.model.Forum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ForumRequest {
    private Integer id;
    private String name;
    private final String short_name;
    private String email;

    @JsonCreator
    public ForumRequest(@JsonProperty("name") String name,
                        @JsonProperty("short_name") String short_name,
                        @JsonProperty("user") String user) {
        id = -1;
        this.name = name;
        this.short_name = short_name;
        this.email = user;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShort_name() {
        return short_name;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUser(String email) {
        this.email = email;
    }
}
