package org.ebitbucket.model;


public class ListObject{
    final private Integer id;
    final private Object value;

    public ListObject(Integer id, Object value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }
}
