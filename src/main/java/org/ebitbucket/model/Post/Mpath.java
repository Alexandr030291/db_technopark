package org.ebitbucket.model.Post;

public class Mpath {
    private final String path;
    private final Integer root;

    public Mpath(String path, Integer root) {
        this.path = path;
        this.root = root;
    }

    public String getPath() {
        return path;
    }

    public Integer getRoot() {
        return root;
    }
}
