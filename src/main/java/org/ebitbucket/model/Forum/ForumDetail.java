package org.ebitbucket.model.Forum;

public class ForumDetail {
    private Integer id;
    private String name;
    private final String short_name;
    private Object userDetail;

    public ForumDetail(String short_name) {
        this.short_name = short_name;
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

    public Object getUserDetail() {
        return userDetail;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserDetail(Object userDetail) {
        this.userDetail = userDetail;
    }
}
