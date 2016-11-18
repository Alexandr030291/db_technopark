package org.ebitbucket.model.Forum;


public class Forum {
    final private Integer id;
    final private String short_name;

    public Forum(Integer id, String short_name) {
        this.id = id;
        this.short_name = short_name;

    }

    public Integer getId() {
        return id;
    }

    public String getShort_name() {
        return short_name;
    }
}
