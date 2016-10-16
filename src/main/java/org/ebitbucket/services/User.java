package org.ebitbucket.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class User {
    JdbcTemplate template;

    public User(JdbcTemplate template) {
        this.template = template;
    }

    public void create(String email,String name,String username,String about,Boolean isAnonymous){
        final String sql = "INSERT INTO User(email, name, user_name, about, isAnonymous) VALUE(?,?,?,?,?)";
        template.update(sql, email, name, username, about, isAnonymous);
    }
}
