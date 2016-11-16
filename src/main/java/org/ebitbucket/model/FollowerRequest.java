package org.ebitbucket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ebitbucket.main.UserController;

public class FollowerRequest {
    private final String follower;
    private final String followee;

    @JsonCreator
    public FollowerRequest(@JsonProperty("follower") String follower, @JsonProperty("followee") String followee){
        this.follower = follower;
        this.followee = followee;
    }

    public String getFollower() {
        return follower;
    }

    public String getFollowee() {
        return followee;
    }
}
