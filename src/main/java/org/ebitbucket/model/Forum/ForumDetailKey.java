package org.ebitbucket.model.Forum;

@SuppressWarnings("unused")
public class ForumDetailKey {
    private Integer id;
    private String name;
    private final String short_name;
    private Object user;

    public ForumDetailKey(Integer id, String name, String short_name, Object userDetail) {
        this.name = name;
        this.short_name = short_name;
        this.user = userDetail;
        this.id = id;
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

    public Object getUser() {
        return user;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserDetail(Object userDetail) {
        this.user = userDetail;
    }

}
