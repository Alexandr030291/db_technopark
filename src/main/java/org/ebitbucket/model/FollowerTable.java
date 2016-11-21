package org.ebitbucket.model;

public class FollowerTable {
    final private int followerId;
    final private String followerEmail;
    final private int followeeId;
    final private String followeeEmail;

    public FollowerTable(int followerId, String followerEmail, int followeeId, String followeeEmail) {
        this.followerId = followerId;
        this.followerEmail = followerEmail;
        this.followeeId = followeeId;
        this.followeeEmail = followeeEmail;
    }

    public int getFollowerId() {
        return followerId;
    }

    public String getFollowerEmail() {
        return followerEmail;
    }

    public int getFolloweeId() {
        return followeeId;
    }

    public String getFolloweeEmail() {
        return followeeEmail;
    }
}
