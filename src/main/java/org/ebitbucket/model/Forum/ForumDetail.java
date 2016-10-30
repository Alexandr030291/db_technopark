package org.ebitbucket.model.Forum;

public class ForumDetail {
    private Integer id;
    private String name;
    private final String short_name;
    private Object userDetail;

    public ForumDetail(Integer id, String name, String short_name, Object userDetail) {
        this.id = id;
        this.name = name;
        this.short_name = short_name;
        this.userDetail = userDetail;
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
